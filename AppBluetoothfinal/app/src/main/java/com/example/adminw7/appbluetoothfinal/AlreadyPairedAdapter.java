package com.example.adminw7.appbluetoothfinal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Handler;

/**
 * Created by TG1619 on 23/10/2016.
 */

public class AlreadyPairedAdapter extends ArrayAdapter<BluetoothObject>
{
    private Context context;
    private AppCompatActivity activity;
    private ArrayList<BluetoothObject> arrayOfAlreadyPairedDevices;

    public AlreadyPairedAdapter(Context context, ArrayList<BluetoothObject> arrayOfAlreadyPairedDevices)
    {
        super(context, R.layout.row_bt, arrayOfAlreadyPairedDevices);
        this.context = context;
        this.arrayOfAlreadyPairedDevices = arrayOfAlreadyPairedDevices;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final BluetoothObject bluetoothObject = arrayOfAlreadyPairedDevices.get(position);
        // 1. Create Inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_bt, parent, false);

        // 3. Get the widgets from the rowView
        TextView bt_name = (TextView) rowView.findViewById(R.id.textview_bt_name);
        TextView bt_address = (TextView) rowView.findViewById(R.id.textview_bt_address);

        TextView bt_type = (TextView) rowView.findViewById(R.id.textview_bt_type);
        //TextView bt_uuid = (TextView) rowView.findViewById(R.id.textview_bt_uuid);
        final TextView bt_signal_strength = (TextView) rowView.findViewById(R.id.textview_bt_signal_strength);
        final TextView bt_distance = (TextView) rowView.findViewById(R.id.textview_bt_distance);
        final Button connectButton = (Button) rowView.findViewById(R.id.button);
        final ToggleButton normalToggleButton= (ToggleButton) rowView.findViewById(R.id.normal_button);
        final ToggleButton alertToggleButton= (ToggleButton) rowView.findViewById(R.id.alert_button);
        final ToggleButton lowToggleButton= (ToggleButton) rowView.findViewById(R.id.low_button);
        final Runnable rssiCallBack=new Runnable() {
            @Override
            public void run() {
                double distance =Math.exp(((double)10*(-1000*bluetoothObject.getAverage_rssi()-60969))/(double)82229);
                distance=(double) ((int)(distance*1000))/1000;
                bt_signal_strength.setText("RSSI: " + bluetoothObject.getBluetooth_rssi() + "dbm");
                bt_distance.setText("DISTANCE: "+distance+" m");
                if(distance>=20){
                    bluetoothObject.sendData("AT+PIO21",context);
                    bt_distance.setTextColor(Color.RED);
                    Toast.makeText(context, "PITANDO " + bluetoothObject.getBluetooth_name(), Toast.LENGTH_SHORT).show();
                    bluetoothObject.playAlarm();
                    //bt_distance.setSoundEffectsEnabled(true);
                }else{
                    bluetoothObject.sendData("AT+PIO20",context);
                    bt_distance.setTextColor(Color.BLACK);
                    bluetoothObject.stopAlarm();
                }
            }

        };
        bluetoothObject.setRssiUpadateCallback(rssiCallBack);
        connectButton.setOnClickListener(new View.OnClickListener()
        {
            //private final Handler handler= new Handler(context.getMainLooper());
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(context, "Connect"+bObject.getBluetooth_name()+ " "+bObject.getBluetooth_address(), Toast.LENGTH_SHORT).show();
                if(bluetoothObject.isConnected()){
                    try {

                        BluetoothEnumerationObject result=bluetoothObject.disconnectDevice(context);
                        if(result==BluetoothEnumerationObject.DISCONNECTED) {
                            connectButton.setText("CONECTAR");
                            switch (bluetoothObject.getState()) {
                                case (1):
                                    normalToggleButton.setChecked(false);
                                    normalToggleButton.setEnabled(true);
                                    break;
                                case (2):
                                    alertToggleButton.setChecked(false);
                                    alertToggleButton.setEnabled(true);
                                    break;
                                case (3):
                                    lowToggleButton.setChecked(false);
                                    lowToggleButton.setEnabled(true);
                                    break;
                            }
                            bluetoothObject.setState(0);
                        }
                        //Toast.makeText(context, "Bluetotth desconectado: ", Toast.LENGTH_LONG).show();
                    }catch (IOException error){
                        Toast.makeText(context, "Ocurrio un error: " + error, Toast.LENGTH_LONG).show();
                    }
                }else{

                    BluetoothEnumerationObject result=bluetoothObject.connectDevice(context,
                            new Runnable() {
                                @Override
                                public void run() {
                                if(bluetoothObject.isConnected()) {
                                    try {
                                            connectButton.setText("DESCONECTAR");
                                            normalToggleButton.setChecked(true);
                                            normalToggleButton.setEnabled(false);
                                            alertToggleButton.setEnabled(true);
                                            alertToggleButton.setChecked(false);
                                            lowToggleButton.setEnabled(true);
                                            lowToggleButton.setChecked(false);
                                    }catch (Exception  e){
                                        Log.d("Button", "ERROR -> "+e);
                                    }
                                    boolean replay =bluetoothObject.sendData("AT+ADVI4", context);
                                    bluetoothObject.setState(1);
                                    Toast.makeText(context, "Connectado " + bluetoothObject.getBluetooth_name(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    },rssiCallBack);


                    if(result==BluetoothEnumerationObject.CONNECTING) {
                        connectButton.setText("CONECTANDO..");
                    }
                    }
                //*/
            }




        });

        normalToggleButton.setOnClickListener(new View.OnClickListener()
        {
            //BluetoothObject bObject=bluetoothObject;
            //View rowViewB=rowView;
            @Override
            public void onClick(View v)
            {

                if(normalToggleButton.isChecked()){//off-on
                    if(bluetoothObject.sendData("AT+ADVI4",context)) {
                        bluetoothObject.sendData("AT+PIO21",context);
                        //bluetoothObject.sendData("AT+PIO20",context);
                        normalToggleButton.setEnabled(false);
                        alertToggleButton.setEnabled(true);
                        alertToggleButton.setChecked(false);
                        lowToggleButton.setEnabled(true);
                        lowToggleButton.setChecked(false);
                        bluetoothObject.setState(1);
                        Toast.makeText(context, "NORMAL " + bluetoothObject.getBluetooth_name(), Toast.LENGTH_SHORT).show();
                    }else{
                        normalToggleButton.setChecked(false);
                    }
                }else{
                    normalToggleButton.setChecked(true);
                }

            }
        });

        alertToggleButton.setOnClickListener(new View.OnClickListener()
        {
            //BluetoothObject bObject=bluetoothObject;
            //View rowViewB=rowView;
            @Override
            public void onClick(View v)
            {

                if(alertToggleButton.isChecked()){//off-on
                    if(bluetoothObject.sendData("AT+ADVI2",context)) {
                        bluetoothObject.sendData("AT+PIO21",context);
                        alertToggleButton.setEnabled(false);
                        normalToggleButton.setEnabled(true);
                        normalToggleButton.setChecked(false);
                        lowToggleButton.setEnabled(true);
                        lowToggleButton.setChecked(false);
                        bluetoothObject.setState(2);
                        Toast.makeText(context, "ALERT " + bluetoothObject.getBluetooth_name(), Toast.LENGTH_SHORT).show();
                    }else{
                        alertToggleButton.setChecked(false);
                    }
                }else{
                    alertToggleButton.setChecked(true);
                }

             }
        });

        lowToggleButton.setOnClickListener(new View.OnClickListener()
        {
            //BluetoothObject bObject=bluetoothObject;
            //View rowViewB=rowView;
            @Override
            public void onClick(View v)
            {

                if(lowToggleButton.isChecked()){//off-on
                    if(bluetoothObject.sendData("AT+ADVI7",context)) {
                        bluetoothObject.sendData("AT+PIO21",context);
                        lowToggleButton.setEnabled(false);
                        alertToggleButton.setEnabled(true);
                        alertToggleButton.setChecked(false);
                        normalToggleButton.setEnabled(true);
                        normalToggleButton.setChecked(false);
                        bluetoothObject.setState(3);
                        Toast.makeText(context, "BAJO " + bluetoothObject.getBluetooth_name(), Toast.LENGTH_SHORT).show();


                    }else{
                        lowToggleButton.setChecked(false);
                    }
                }else{
                    lowToggleButton.setChecked(true);
                }

            }
        });

        // 4. Set the text for each widget
        if(!bluetoothObject.isConnected()){
            connectButton.setText("CONECTAR");
        }else {
            connectButton.setText("DESCONECTAR");
            switch (bluetoothObject.getState()){
                case (1):
                            normalToggleButton.setChecked(true);
                            normalToggleButton.setEnabled(false);
                            break;
                case (2):
                            alertToggleButton.setChecked(true);
                            alertToggleButton.setEnabled(false);
                            break;
                case (3):
                            lowToggleButton.setChecked(true);
                            lowToggleButton.setEnabled(false);
                            break;


            }
        }
        bt_name.setText(bluetoothObject.getBluetooth_name());
        bt_address.setText("address: \n" + bluetoothObject.getBluetooth_address());
        bt_type.setText("type: " + bluetoothObject.getBluetooth_type());
        bt_signal_strength.setText("RSSI: " + bluetoothObject.getBluetooth_rssi() + "dbm");
        ParcelUuid uuid[] = bluetoothObject.getBluetooth_uuids();
        if (uuid != null) {
            //bt_uuid.setText("uuid \n" + uuid[0]);
            //bt_uuid.setTextSize(TypedValue.COMPLEX_UNIT_SP,8);
        }
        // 5. return rowView
        return rowView;

    }//end getView()


}//end class AlreadyPairedAdapter


























