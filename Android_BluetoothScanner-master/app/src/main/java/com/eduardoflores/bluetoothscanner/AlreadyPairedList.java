package com.eduardoflores.bluetoothscanner;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Eduardo Flores on 3/23/15.
 */
public class AlreadyPairedList extends ListActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 0. Get the arrayList from the Intent. This was sent from MainActivity.java
        Intent intent = getIntent();
        ArrayList<BluetoothObject> arrayOfPairedDevices = new ArrayList<>();//intent.getParcelableArrayListExtra("arrayOfPairedDevices");
        for(Map.Entry<String,BluetoothObject> entry:MainActivity.mapBTDevices.entrySet()){
                arrayOfPairedDevices.add(entry.getValue());
        }
        // 1. Pass context and data to the custom adapter
        AlreadyPairedAdapter myAdapter = new AlreadyPairedAdapter(getApplicationContext(), arrayOfPairedDevices);

        // 2. setListAdapter
        setListAdapter(myAdapter);
    }
}//end class AlreadyPairedList