package com.example.adminw7.appbluetoothfinal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by angel on 30/10/16.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLEObject extends BluetoothObject {
    private final static String TAG = BluetoothLEObject.class.getSimpleName();

    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    final String  CHAR_WRITE = "0000fff3-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");//BluetoothHelper.sixteenBitUuid(0xFFE0);
    public final static UUID UUID_RECEIVE = BluetoothHelper.sixteenBitUuid(0x0224);
    public final static UUID UUID_SEND = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");;
    //public final static UUID UUID_DISCONNECT = BluetoothHelper.sixteenBitUuid(0x2223);
    public final static UUID UUID_CLIENT_CONFIGURATION = BluetoothHelper.sixteenBitUuidOld(0x2902);
    /*
    protected BluetoothGattCallback gattCallback;
    protected BluetoothGatt bluetoothGatt;
    * */
    // Various callback methods defined by the BLE API.
    public BluetoothGattCallback mGattCallback;
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read or notification operations.
    private BroadcastReceiver mGattUpdateReceiver;
    private Runnable callback;
    public BluetoothLEObject() {
        super();
        MI_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    intentAction = ACTION_GATT_CONNECTED;
                    mConnectionState = STATE_CONNECTED;
                    connection_state= BluetoothEnumerationObject.CONNECTED;
                    //broadcastUpdate(intentAction);
                    boolean rssiStatus = mBluetoothGatt.readRemoteRssi();
                    Log.i(TAG, "Connected to GATT server. "+rssiStatus);
                    Log.i(TAG, "Attempting to start service discovery:" +
                            mBluetoothGatt.discoverServices());

                    try {
                        ((Activity)context).runOnUiThread(callback);
                    }catch (Exception e){
                        Log.i(TAG, "Error -> "+e);
                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connection_state= BluetoothEnumerationObject.DISCONNECTED;
                    intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = STATE_DISCONNECTED;
                    Log.i(TAG, "Disconnected from GATT server.");
                    //broadcastUpdate(intentAction);
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                //super.onReadRemoteRssi(gatt, rssi, status);
                // use rssi value here
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "RSSI-> "+rssi);
                    setBluetooth_rssi(rssi);
                }
            }

            @Override
            // New services discovered
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }

            @Override
            // Result of a characteristic read operation
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            }

            //...
        };
        mGattUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (BluetoothLEObject.ACTION_GATT_CONNECTED.equals(action)) {
                    connection_state=BluetoothEnumerationObject.CONNECTED;
                    //mConnected = true;
                    //updateConnectionState(R.string.connected);
                    //invalidateOptionsMenu();
                } else if (BluetoothLEObject.ACTION_GATT_DISCONNECTED.equals(action)) {
                    connection_state=BluetoothEnumerationObject.DISCONNECTED;
                    //mConnected = false;
                    //updateConnectionState(R.string.disconnected);
                    //invalidateOptionsMenu();
                    //clearUI();
                } else if (BluetoothLEObject.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    // Show all the supported services and characteristics on the
                    // user interface.
                    //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                } else if (BluetoothLEObject.ACTION_DATA_AVAILABLE.equals(action)) {
                    //displayData(intent.getStringExtra(BluetoothLEObject.EXTRA_DATA));
                }
            }
        };
    }

    public void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        //sendBroadcast(intent);
    }

    public void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (this.MI_UUID.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        //sendBroadcast(intent);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothEnumerationObject connectDevice(Context context, Runnable callback,Runnable rssiUpadateCallback){
        Log.d(TAG, "Bluetooth LE CONNECT");
        if(!isConnected()){
            BluetoothAdapter meuBluetoothAdapter = MainActivity.mBluetoothAdapter;
            meuDivece = meuBluetoothAdapter.getRemoteDevice(this.getBluetooth_address());
            this.mBluetoothGatt=meuDivece.connectGatt(context, false, mGattCallback);
            this.callback=callback;
            this.context=context;
            this.rssiUpadateCallback=rssiUpadateCallback;
            return BluetoothEnumerationObject.CONNECTING;

        }
        return  connection_state;
    }

    @Override
    public BluetoothEnumerationObject disconnectDevice(Context context) throws IOException {
        if (this.isConnected()&& mBluetoothGatt != null ){
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            this.setConnection_state(BluetoothEnumerationObject.DISCONNECTED);
        }
        return this.connection_state;
    }

    @Override
    public boolean sendData(String data,Context context) {
        if(isConnected()&& mBluetoothGatt!=null){
            BluetoothGattService gattService=mBluetoothGatt.getService(UUID_SERVICE);
            //bluetooth_rssi=

            if(gattService!=null){
                BluetoothGattCharacteristic characteristic =gattService.getCharacteristic(UUID_SEND);
                if(characteristic!=null){

                    characteristic.setValue(data.getBytes());
                    //characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    return mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }

        }
        return false;
    }

    @Override
    public void updateRSSI(){
        mBluetoothGatt.readRemoteRssi();
    }

}