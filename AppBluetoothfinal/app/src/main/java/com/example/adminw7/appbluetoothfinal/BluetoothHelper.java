package com.example.adminw7.appbluetoothfinal;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Created by berme_000 on 05/11/2016.
 */

public class BluetoothHelper{
    public static String shortUuidFormat = "871e%04X-38ff-77b1-ed41-9fb3aa142db2";
    public static String oldUuidFormat = "0000%04X-0000-1000-8000-00805f9b34fb";

    public static UUID sixteenBitUuid(long shortUuid) {
        assert shortUuid >= 0 && shortUuid <= 0xFFFF;
        return UUID.fromString(String.format(shortUuidFormat, shortUuid & 0xFFFF));
    }

    public static UUID sixteenBitUuidOld(long shortUuid) {
        assert shortUuid >= 0 && shortUuid <= 0xFFFF;
        return UUID.fromString(String.format(oldUuidFormat, shortUuid & 0xFFFF));
    }



}