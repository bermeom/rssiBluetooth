package com.example.adminw7.appbluetoothfinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static BluetoothAdapter mBluetoothAdapter;
    public static Map<String, BluetoothObject> mapBTDevices;
    public static int delayMillis=2000;
    private final static int REQUEST_ENABLE_BT = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        this.mapBTDevices = new HashMap<>();
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth 4.0", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void OnClickListenerModo1(View view) {
        //Toast.makeText(getApplicationContext(), "que onda", Toast.LENGTH_LONG).show();
        getArrayOfAlreadyPairedBluetoothDevices();
        MainActivity.delayMillis=2000;
        Intent intent = new Intent(this, MainActivityListConnectionsDevices.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void OnClickListenerModo2(View view) {
        //Toast.makeText(getApplicationContext(), "que onda", Toast.LENGTH_LONG).show();
        getArrayOfAlreadyPairedBluetoothDevices();
        MainActivity.delayMillis=2000;
        Intent intent = new Intent(this, MainActivityListConnectionsDevices.class);
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void OnClickListenerModo3(View view) {
        //Toast.makeText(getApplicationContext(), "que onda", Toast.LENGTH_LONG).show();
        getArrayOfAlreadyPairedBluetoothDevices();
        MainActivity.delayMillis=1000;
        Intent intent = new Intent(this, MainActivityListConnectionsDevices.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getArrayOfAlreadyPairedBluetoothDevices() {

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0) {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {

                // Create the device object and add it to the arrayList of devices
                BluetoothObject bluetoothObject;
                if(device.getType()==2){
                    bluetoothObject = new BluetoothLEObject();
                }else {
                    bluetoothObject = new BluetoothObject();
                }
                if (MainActivity.mapBTDevices.containsKey(device.getAddress())) {
                    bluetoothObject = MainActivity.mapBTDevices.get(device.getAddress());
                }
                bluetoothObject.setBluetooth_name(device.getName());
                bluetoothObject.setBluetooth_address(device.getAddress());
                bluetoothObject.setBluetooth_state(device.getBondState());
                bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                bluetoothObject.setBluetooth_uuids(device.getUuids());
                if (!MainActivity.mapBTDevices.containsKey(device.getAddress())) {
                    MainActivity.mapBTDevices.put(device.getAddress(), bluetoothObject);
                }
                //Toast.makeText(this, bluetoothObject.getBluetooth_name()+" MAC "+bluetoothObject.getBluetooth_address(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
