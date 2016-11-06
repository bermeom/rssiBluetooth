package com.example.adminw7.appbluetoothfinal;

import android.app.Activity;
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
import java.util.concurrent.Callable;

/**
 * Created by TG1619 on 23/10/2016.
 */

public class BluetoothObject  implements Parcelable {

    private final static String TAG = BluetoothObject.class.getSimpleName();
    protected static final int SOLICITA_ATIVACION = 1;
    protected static final int SOLICITA_CONEXION = 2;
    protected BluetoothEnumerationObject connection_state;
    protected int bluetooth_state;
    protected BluetoothDevice meuDivece = null;
    protected BluetoothSocket meuSocket = null;
    protected String bluetooth_name;
    protected String bluetooth_address;
    protected int bluetooth_type;
    protected ParcelUuid[] bluetooth_uuids;
    protected int bluetooth_rssi;
    protected UUID MI_UUID = UUID.fromString("0001101-0000-1000-8000-00805f9b34fb");
    protected int state;//0 none, 1 normal, 2 alter , 3 low
    protected Context context;
    protected Runnable rssiUpadateCallback;
    // Parcelable stuff
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothObject()
    {
        this.state=0;
        this.connection_state=BluetoothEnumerationObject.DISCONNECTED;


    }  //empty constructor

    public BluetoothObject(Parcel in)
    {
        super();
        readFromParcel(in);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Runnable getRssiUpadateCallback() {
        return rssiUpadateCallback;
    }

    public void setRssiUpadateCallback(Runnable rssiUpadateCallback) {
        this.rssiUpadateCallback = rssiUpadateCallback;
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

    public BluetoothEnumerationObject getConnection_state() {
        return connection_state;
    }

    public void setConnection_state(BluetoothEnumerationObject connection_state) {
        this.connection_state = connection_state;
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
        if(!(context==null || rssiUpadateCallback==null)){
            try {
                ((Activity)context).runOnUiThread(rssiUpadateCallback);
            }catch (Exception e){
                Log.i(TAG, "Error -> "+e);
            }
        }
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
        return this.connection_state==BluetoothEnumerationObject.CONNECTED;
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
        //bluetooth_state=in.readInt();
        bluetooth_type=in.readInt();
        bluetooth_rssi=in.readInt();

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
        //out.writeInt(bluetooth_state);
        out.writeInt(bluetooth_type);
        out.writeInt(bluetooth_rssi);
        out.writeParcelable(meuDivece,0);

    }


    public BluetoothEnumerationObject connectDevice(Context context, Runnable callback,Runnable rssiUpadateCallback){
        Log.d(TAG, "Bluetooth CONNECT");
        if (!this.isConnected()){
            String direccionMac =this.getBluetooth_address();
            BluetoothAdapter meuBluetoothAdapter = MainActivity.mBluetoothAdapter;
            BluetoothDevice meuDivece = meuBluetoothAdapter.getRemoteDevice(direccionMac);
            BluetoothSocket meuSocket;
            try {

                ParcelUuid uuid[] = this.getBluetooth_uuids();
                if (uuid != null) {
                    meuSocket = meuDivece.createRfcommSocketToServiceRecord(uuid[0].getUuid());
                }else{
                    meuSocket = meuDivece.createRfcommSocketToServiceRecord(this.getMI_UUID());
                }

                meuSocket.connect();
                this.setConnection_state(BluetoothEnumerationObject.CONNECTED);
                this.setMeuDivece(meuDivece);
                this.setMeuSocket(meuSocket);
                this.rssiUpadateCallback=rssiUpadateCallback;
                Toast.makeText(context, "Conectado con: " + direccionMac, Toast.LENGTH_LONG).show();
                try {
                    ((Activity)context).runOnUiThread(callback);
                }catch (Exception e){
                    Log.i(TAG, "Error -> "+e);
                }

                return BluetoothEnumerationObject.CONNECTED;

            } catch (IOException error){
                this.setConnection_state(BluetoothEnumerationObject.DISCONNECTED);
                Toast.makeText(context, "Ocurrio un error: " + error, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //*/
        }
        return BluetoothEnumerationObject.DISCONNECTED;

    }

    public BluetoothEnumerationObject disconnectDevice(Context context) throws IOException {
        if (this.isConnected()){
            meuSocket.close();
            this.setConnection_state(BluetoothEnumerationObject.DISCONNECTED);

        }
        return this.connection_state;

    }

    public void updateRSSI(){

    }

}//end class BluetoothObject
