package com.example.adminw7.appbluetoothfinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private BluetoothAdapter mBluetoothAdapter;
    public static Map<String,BluetoothObject> mapBTDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mapBTDevices = new HashMap<>();

    }

    public void OnClickListenerListConnectionsDevices(View view) {
        //Toast.makeText(getApplicationContext(), "que onda", Toast.LENGTH_LONG).show();
        getArrayOfAlreadyPairedBluetoothDevices();
        Intent intent = new Intent(this, MainActivityListConnectionsDevices.class);
        startActivity(intent);
    }

    private void getArrayOfAlreadyPairedBluetoothDevices()
    {

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0)
        {

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices)
            {

                // Create the device object and add it to the arrayList of devices
                BluetoothObject bluetoothObject = new BluetoothObject();
                if(MainActivity.mapBTDevices.containsKey(device.getAddress())){
                    bluetoothObject=MainActivity.mapBTDevices.get(device.getAddress());
                }
                bluetoothObject.setBluetooth_name(device.getName());
                bluetoothObject.setBluetooth_address(device.getAddress());
                bluetoothObject.setBluetooth_state(device.getBondState());
                bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                bluetoothObject.setBluetooth_uuids(device.getUuids());
                if(!MainActivity.mapBTDevices.containsKey(device.getAddress())){
                    MainActivity.mapBTDevices.put(device.getAddress(),bluetoothObject);
                }
                //Toast.makeText(this, bluetoothObject.getBluetooth_name()+" MAC "+bluetoothObject.getBluetooth_address(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
