package com.example.adminw7.appbluetoothfinal;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import android.os.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class MainActivityListConnectionsDevices extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private ListView listview;
    private boolean isScan ;
    BroadcastReceiver mReceiver;
    private  Handler handler;
    private  Runnable runnableCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_connetions_devices);
        this.isScan=false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listview = (ListView) findViewById(R.id.listConnectionsDevices);
        setListAdapter();

        // Create the Handler object (on the main thread by default)
        handler = new Handler();
        // Define the code block to be executed
        runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                for(Map.Entry<String,BluetoothObject> entry: MainActivity.mapBTDevices.entrySet()){
                    if(entry.getValue().isConnected()){
                        entry.getValue().updateRSSI();
                    }
                }
                handler.postDelayed(this, MainActivity.delayMillis);
            }
        };
        // Run the above code block on the main thread after 2 seconds
        handler.postDelayed(runnableCode, MainActivity.delayMillis);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                    if(isScan){
                        MenuView.ItemView item1= (MenuView.ItemView) findViewById(R.id.scan);
                        item1.setTitle("Escanear");
                        mBluetoothAdapter.cancelDiscovery();
                        this.isScan=false;
                    }else{
                        this.isScan=true;
                        MenuView.ItemView item1= (MenuView.ItemView) findViewById(R.id.scan);
                        item1.setTitle("Escaneando");
                        displayListOfFoundDevices();
                    }

                return true;
            default:
                return false;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void setListAdapter(){
        ArrayList<BluetoothObject> arrayOfPairedDevices = new ArrayList<>();//intent.getParcelableArrayListExtra("arrayOfPairedDevices");
        for(Map.Entry<String,BluetoothObject> entry:MainActivity.mapBTDevices.entrySet()){
            arrayOfPairedDevices.add(entry.getValue());
        }
        //AppCompatActivity activity = (AppCompatActivity) getApplicationContext();
        // 1. Pass context and data to the custom adapter
        AlreadyPairedAdapter myAdapter = new AlreadyPairedAdapter(MainActivityListConnectionsDevices.this, arrayOfPairedDevices);

        // 2. setListAdapter

        listview.setAdapter(myAdapter);
        //setListAdapter(myAdapter);

    }

    private void displayListOfFoundDevices()
    {


        // start looking for bluetooth devices
        mBluetoothAdapter.startDiscovery();
        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject;
                    if(device.getType()==2){
                        bluetoothObject = new BluetoothLEObject();
                    }else {
                        bluetoothObject = new BluetoothObject();
                    }
                    if(MainActivity.mapBTDevices.containsKey(device.getAddress())){
                        bluetoothObject=MainActivity.mapBTDevices.get(device.getAddress());
                    }
                    bluetoothObject.setBluetooth_name(device.getName());
                    bluetoothObject.setBluetooth_address(device.getAddress());
                    bluetoothObject.setBluetooth_state(device.getBondState());
                    bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                    bluetoothObject.setBluetooth_uuids(device.getUuids());
                    bluetoothObject.setBluetooth_rssi(rssi);
                    if(!MainActivity.mapBTDevices.containsKey(device.getAddress())){
                        MainActivity.mapBTDevices.put(device.getAddress(),bluetoothObject);
                    }
                    //arrayOfFoundBTDevices.add(bluetoothObject);

                    // 2. setListAdapter
                    setListAdapter();
                }
            }
        };
        try {
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
        }catch (Exception e){
            Log.d("MAListConnectionsDevice", "ERROR ->" +e);
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        for(Map.Entry<String,BluetoothObject> entry: MainActivity.mapBTDevices.entrySet()){
                if(entry.getValue().isConnected()){
                    try {
                        entry.getValue().disconnectDevice(MainActivityListConnectionsDevices.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
        try {
            if(mReceiver!=null) {
                unregisterReceiver(mReceiver);
                //mReceiver.abortBroadcast();
                //mReceiver.clearAbortBroadcast();

            }
        }catch (Exception e){
            Log.d("MAListConnectionsDevice", "ERROR ->" +e);
        }
    }


    void stopRepeatingTask() {
        handler.removeCallbacks(runnableCode);
    }
}
