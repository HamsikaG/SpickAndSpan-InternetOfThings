package com.example.lab4.lab4android.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lab4.lab4android.DeviceScanListAdapter;
import com.example.lab4.lab4android.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

import static android.content.ContentValues.TAG;


public class BTUtils {

    public static final String SERVICE_UUID = "22222222-2222-2222-2222-222222222222";

    private static final int REQ_BLUETOOTH_ENABLE = 1000;
    private static final int DEVICE_SCAN_MILLISECONDS = 10000;
    private static SampleScanCallback mScanCallback;
    private static BluetoothLeScanner bluetoothLeScanner;

    public static  void checkBluetoothSupport(Context context) {
        // Check for BLE support - also checked from Android manifest.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            exitApp(context, context.getString(R.string.msg_no_ble));
        }

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            exitApp(context, context.getString(R.string.msg_no_ble));
        }

        if (btAdapter!=null && !btAdapter.isEnabled()) {
            enableBluetooth(context);
        }
    }

    private static void exitApp(Context context, String reason) {
        Toast.makeText(context, reason, Toast.LENGTH_LONG).show();
        ((Activity)context).finish();
    }

    private static void enableBluetooth(Context context) {
        ((Activity)context).startActivityForResult(
                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQ_BLUETOOTH_ENABLE);
    }

    public static void startScanning(Context context, DeviceScanListAdapter mDeviceAdapter, CompoundButton buttonView, ProgressBar progressBar) {

        if (mScanCallback == null) {
            Log.d(TAG, "Starting Scanning");

            bluetoothLeScanner = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter().getBluetoothLeScanner();
            // Kick off a new scan.
            mScanCallback = new SampleScanCallback(mDeviceAdapter);
            bluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
            Toast.makeText(context, "Starting Scanning", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.already_scanning, Toast.LENGTH_SHORT).show();
        }
    }

    public static void stopScanning(Context context) {
        // Stop the scan, wipe the callback.
        bluetoothLeScanner.stopScan(mScanCallback);
        mScanCallback = null;
        Timber.d("Scan stopped");
    }



    private static List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
//        builder.setServiceUuid(SERVICE_UUID);
        scanFilters.add(builder.build());

        return scanFilters;
    }


    private static ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }


    private static class SampleScanCallback extends ScanCallback {

        private DeviceScanListAdapter mAdapter;

        SampleScanCallback(DeviceScanListAdapter mAdapter){
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Timber.d("found device1");
            results.forEach(this::addToDeviceList);
        }
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            addToDeviceList(result);
        }

        private void addToDeviceList(ScanResult result) {
            BluetoothLeDevice bluetoothLeDevice = new BluetoothLeDevice(result.getDevice(), result.getRssi(),
                    result.getScanRecord().getBytes(), System.currentTimeMillis());

            if (BeaconUtils.getBeaconType(bluetoothLeDevice) == BeaconType.IBEACON) {
                Timber.d("found beacon");
                final IBeaconDevice iBeacon = new IBeaconDevice(bluetoothLeDevice);
                mAdapter.add(iBeacon);
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Timber.d("scan failed");

           /* Toast.makeText(getActivity(), "Scan failed with error: " + errorCode, Toast.LENGTH_LONG)
                    .show();*/
        }


    }
}
