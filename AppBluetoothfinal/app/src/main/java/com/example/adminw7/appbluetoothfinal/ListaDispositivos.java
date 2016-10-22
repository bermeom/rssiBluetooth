package com.example.adminw7.appbluetoothfinal;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.Set;

/**
 * Created by ADMINW7 on 17/09/2016.
 */

public class ListaDispositivos extends ListActivity {

    private BluetoothAdapter meuBluetoothAdapter2 = null;

    static String ENDERECO_MAC = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        meuBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosPareados = meuBluetoothAdapter2.getBondedDevices();

        if (dispositivosPareados.size() > 0){
            for (BluetoothDevice dispositivo : dispositivosPareados){
                String nomeBt = dispositivo.getName();
                String macBt = dispositivo.getAddress();

                ArrayBluetooth.add(nomeBt + "\n" + macBt);

            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String informacionGeneral = ((TextView) v).getText().toString();

        Toast.makeText(getApplicationContext(), "ENDERECO_MAC: " + ENDERECO_MAC, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Info: " + informacionGeneral, Toast.LENGTH_LONG).show();

        String direccionMac = informacionGeneral.substring(informacionGeneral.length() - 17);

        Toast.makeText(getApplicationContext(), "mac: " + direccionMac, Toast.LENGTH_LONG).show();

        Intent retornaMac = new Intent();
        retornaMac.putExtra(ENDERECO_MAC, direccionMac);
        setResult(RESULT_OK, retornaMac);
        finish();
    }

}
