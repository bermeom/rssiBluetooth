package com.eduardoflores.bluetoothscanner;

import android.content.Context;
import android.os.ParcelUuid;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Eduardo Flores on 3/23/15.
 */
public class AlreadyPairedAdapter extends ArrayAdapter<BluetoothObject>
{
    private Context context;
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
        final View rowView = inflater.inflate(R.layout.row_bt, parent, false);

        // 3. Get the widgets from the rowView
        TextView bt_name = (TextView) rowView.findViewById(R.id.textview_bt_name);
        TextView bt_address = (TextView) rowView.findViewById(R.id.textview_bt_address);
        TextView bt_bondState = (TextView) rowView.findViewById(R.id.textview_bt_state);
        TextView bt_type = (TextView) rowView.findViewById(R.id.textview_bt_type);
        TextView bt_uuid = (TextView) rowView.findViewById(R.id.textview_bt_uuid);
        TextView bt_signal_strength = (TextView) rowView.findViewById(R.id.textview_bt_signal_strength);
        Button connectButton = (Button) rowView.findViewById(R.id.button);

        connectButton.setOnClickListener(new View.OnClickListener()
        {
            BluetoothObject bObject=bluetoothObject;
            //View rowViewB=rowView;
            @Override
            public void onClick(View v)
            {

                TextView bt_name = (TextView) rowView.findViewById(R.id.textview_bt_name);
                TextView bt_address = (TextView) rowView.findViewById(R.id.textview_bt_address);
                TextView bt_bondState = (TextView) rowView.findViewById(R.id.textview_bt_state);
                TextView bt_type = (TextView) rowView.findViewById(R.id.textview_bt_type);
                TextView bt_uuid = (TextView) rowView.findViewById(R.id.textview_bt_uuid);
                TextView bt_signal_strength = (TextView) rowView.findViewById(R.id.textview_bt_signal_strength);

                //Toast.makeText(context, "Connect"+bObject.getBluetooth_name()+ " "+bObject.getBluetooth_address(), Toast.LENGTH_SHORT).show();

                if(BluetoothObject.connectDevice(context,bObject)){
                    Toast.makeText(context, "Connect"+bt_name.getText(), Toast.LENGTH_SHORT).show();
                }
                //*/
            }
        });

        // 4. Set the text for each widget
        bt_name.setText(bluetoothObject.getBluetooth_name());
        bt_address.setText("address: \n" + bluetoothObject.getBluetooth_address());
        bt_bondState.setText("state: " + bluetoothObject.getBluetooth_state());
        bt_type.setText("type: " + bluetoothObject.getBluetooth_type());
        bt_signal_strength.setText("RSSI: " + bluetoothObject.getBluetooth_rssi() + "dbm");
        ParcelUuid uuid[] = bluetoothObject.getBluetooth_uuids();
        if (uuid != null) {
            bt_uuid.setText("uuid \n" + uuid[0]);
            //bt_uuid.setTextSize(TypedValue.COMPLEX_UNIT_SP,8);
        }
            // 5. return rowView
        return rowView;

    }//end getView()


}//end class AlreadyPairedAdapter































