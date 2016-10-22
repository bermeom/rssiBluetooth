package com.eduardoflores.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends ActionBarActivity
{
    private String LOG_TAG; // Just for logging purposes. Could be anything. Set to app_name
    private int REQUEST_ENABLE_BT = 99; // Any positive integer should work.
    private BluetoothAdapter mBluetoothAdapter;

    private Button button_enableBT;
    private Button button_displayPairedBT;
    private Button button_scanBT;
    public static Map<String,BluetoothObject> mapBTDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LOG_TAG = getResources().getString(R.string.app_name);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mapBTDevices = new HashMap<>();
        button_enableBT = (Button) findViewById(R.id.button_enableBT);
        button_displayPairedBT = (Button) findViewById(R.id.button_alreadyPairedBT);
        button_scanBT = (Button) findViewById(R.id.button_scanBT);

       button_enableBT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                enableBluetoothOnDevice();
            }
        });

        // In a real app you should check first if bluetooth is enabled first
        button_displayPairedBT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ArrayList<BluetoothObject> arrayOfAlreadyPairedBTDevices =getArrayOfAlreadyPairedBluetoothDevices();
                if (arrayOfAlreadyPairedBTDevices != null)
                {
                    Intent intent = new Intent(MainActivity.this, AlreadyPairedList.class);
                    intent.putParcelableArrayListExtra("arrayOfPairedDevices", arrayOfAlreadyPairedBTDevices);
                    startActivity(intent);
                }
            }
        });

        // In a real app you should check first if bluetooth is enabled first
        button_scanBT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                scanForBluetoothDevices();
            }
        });
    }//end onCreate

    private void enableBluetoothOnDevice()
    {
        if (mBluetoothAdapter == null)
        {
            Log.e(LOG_TAG, "This device does not have a bluetooth adapter");
            finish();
            // If the android device does not have bluetooth, just return and get out.
            // There's nothing the app can do in this case. Closing app.
        }

        // Check to see if bluetooth is enabled. Prompt to enable it
        if( !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == 0)
            {
                // If the resultCode is 0, the user selected "No" when prompt to
                // allow the app to enable bluetooth.
                // You may want to display a dialog explaining what would happen if
                // the user doesn't enable bluetooth.
                Toast.makeText(this, "The user decided to deny bluetooth access", Toast.LENGTH_LONG).show();
            }
            else
                Log.i(LOG_TAG, "User allowed bluetooth access!");
        }
    }

    private ArrayList<BluetoothObject> getArrayOfAlreadyPairedBluetoothDevices()
    {
        ArrayList<BluetoothObject> arrayOfAlreadyPairedBTDevices = null;

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0)
        {
            arrayOfAlreadyPairedBTDevices = new ArrayList<BluetoothObject>();

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
                arrayOfAlreadyPairedBTDevices.add(bluetoothObject);
            }
        }

        return arrayOfAlreadyPairedBTDevices;
    }

    private void scanForBluetoothDevices()
    {
        // Start this on a new activity without passing any data to it
        Intent intent = new Intent(this, FoundBTDevices.class);
        startActivity(intent);
    }
}//end MainActivity
