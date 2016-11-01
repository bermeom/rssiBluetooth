package com.example.adminw7.appbluetoothfinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by TG1619 on 23/10/2016.
 */

public class BluetoothObject  implements Parcelable
{

    private final static String TAG = BluetoothObject.class.getSimpleName();
    protected static final int SOLICITA_ATIVACION = 1;
    protected static final int SOLICITA_CONEXION = 2;
    protected BluetoothDevice meuDivece = null;
    protected BluetoothSocket meuSocket = null;
    protected String bluetooth_name;
    protected String bluetooth_address;
    protected int bluetooth_state;
    protected int bluetooth_type;
    protected ParcelUuid[] bluetooth_uuids;
    protected int bluetooth_rssi;
    protected boolean connected;
    protected UUID MI_UUID = UUID.fromString("0001101-0000-1000-8000-00805f9b34fb");
    protected int state;//0 none, 1 normal, 2 alter , 3 low

    protected BluetoothGattCallback gattCallback;
    protected BluetoothGatt bluetoothGatt;

    // Parcelable stuff
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothObject()
    {
        this.connected=false;
        this.state=0;
        this.gattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i(TAG, "Connected to GATT server.");
                      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.i(TAG, "Disconnected from GATT server.");
                        //broadcastUpdate(intentAction);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                        Log.w(TAG, "ACTION_GATT_SERVICES_DISCOVERED " + status);
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {

                }

        };

    }  //empty constructor

    public BluetoothObject(Parcel in)
    {
        super();
        readFromParcel(in);
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public String getBluetooth_name() {
        return bluetooth_name;
    }

    public void setBluetooth_name(String bluetooth_name) {
        this.bluetooth_name = bluetooth_name;
    }

    public String getBluetooth_address() {
        return bluetooth_address;
    }

    public void setBluetooth_address(String bluetooth_address) {
        this.bluetooth_address = bluetooth_address;
    }

    public int getBluetooth_state() {
        return bluetooth_state;
    }

    public void setBluetooth_state(int bluetooth_state) {
        this.bluetooth_state = bluetooth_state;
    }

    public int getBluetooth_type() {
        return bluetooth_type;
    }

    public void setBluetooth_type(int bluetooth_type) {
        this.bluetooth_type = bluetooth_type;
    }

    public ParcelUuid[] getBluetooth_uuids() {
        return bluetooth_uuids;
    }

    public void setBluetooth_uuids(ParcelUuid[] bluetooth_uuids) {
        this.bluetooth_uuids = bluetooth_uuids;
    }

    public int getBluetooth_rssi() {
        return bluetooth_rssi;
    }

    public void setBluetooth_rssi(int bluetooth_rssi) {
        this.bluetooth_rssi = bluetooth_rssi;
    }

    public boolean sendData(String data,Context context) {
            if(isConnected()){
                try{
                    OutputStream outputStream=meuSocket.getOutputStream();
                    byte [] toSend=data.getBytes();
                    outputStream.write(toSend);
                    outputStream.flush();
                    return true;
                } catch (IOException error){
                    Toast.makeText(context, "Ocurrio un error enviando: " + error, Toast.LENGTH_LONG).show();
                }

            }
        return false;
    }

    public BluetoothGattCallback getGattCallback() {
        return gattCallback;
    }

    public void setGattCallback(BluetoothGattCallback gattCallback) {
        this.gattCallback = gattCallback;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static int getSolicitaAtivacion() {
        return SOLICITA_ATIVACION;
    }

    public static int getSolicitaConexion() {
        return SOLICITA_CONEXION;
    }

    public BluetoothDevice getMeuDivece() {
        return meuDivece;
    }

    public void setMeuDivece(BluetoothDevice meuDivece) {
        this.meuDivece = meuDivece;
    }

    public BluetoothSocket getMeuSocket() {
        return meuSocket;
    }

    public void setMeuSocket(BluetoothSocket meuSocket) {
        this.meuSocket = meuSocket;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public UUID getMI_UUID() {
        return MI_UUID;
    }

    public void setMI_UUID(UUID MI_UUID) {
        this.MI_UUID = MI_UUID;
    }

    public void readFromParcel(Parcel in)
    {

        bluetooth_name = in.readString();
        bluetooth_address=in.readString();
        bluetooth_state=in.readInt();
        bluetooth_type=in.readInt();
        bluetooth_rssi=in.readInt();
        if(in.readInt()==1){
            connected=true;
        }else{
            connected=false;
        }

        meuDivece=in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Parcelable.Creator<BluetoothObject> CREATOR = new Parcelable.Creator<BluetoothObject>()
    {
        public BluetoothObject createFromParcel(Parcel in) {
            return new BluetoothObject(in);
        }

        public BluetoothObject[] newArray(int size) {
            return new BluetoothObject[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(bluetooth_name);
        out.writeString(bluetooth_address);
        out.writeInt(bluetooth_state);
        out.writeInt(bluetooth_type);
        out.writeInt(bluetooth_rssi);
        if(connected) {
            out.writeInt(1);
        }else{
            out.writeInt(0);
        }
        out.writeParcelable(meuDivece,0);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean connectDevice(Context context, BluetoothObject bluetoothObject){

        if (!bluetoothObject.isConnected()){
            String direccionMac =bluetoothObject.getBluetooth_address();
            BluetoothAdapter meuBluetoothAdapter = MainActivity.mBluetoothAdapter;
            BluetoothDevice meuDivece = meuBluetoothAdapter.getRemoteDevice(direccionMac);
            BluetoothSocket meuSocket;
            try {


                bluetoothObject.setBluetoothGatt(meuDivece.connectGatt(context, false, bluetoothObject.gattCallback));
                ParcelUuid uuid[] = bluetoothObject.getBluetooth_uuids();
                if (uuid != null) {
                    meuSocket = meuDivece.createRfcommSocketToServiceRecord(uuid[0].getUuid());
                }else{
                    meuSocket = meuDivece.createRfcommSocketToServiceRecord(bluetoothObject.getMI_UUID());
                }

                meuSocket.connect();
                bluetoothObject.setConnected(true);
                bluetoothObject.setMeuDivece(meuDivece);
                bluetoothObject.setMeuSocket(meuSocket);
                Toast.makeText(context, "Conectado con: " + direccionMac, Toast.LENGTH_LONG).show();
                return true;

            } catch (IOException error){
                bluetoothObject.setConnected(false);
                Toast.makeText(context, "Ocurrio un error: " + error, Toast.LENGTH_LONG).show();
            }
                //*/
        }
        return false;

    }

}//end class BluetoothObject
