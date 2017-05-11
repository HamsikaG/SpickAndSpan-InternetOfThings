package com.example.lab4.lab4android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lab4.lab4android.utils.BTUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;


public class DeviceScanListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private OnBeaconFoundListener listener;
    private List<IBeaconDevice> mDevices;

     DeviceScanListAdapter(Context context, OnBeaconFoundListener listener) {
        mLayoutInflater = LayoutInflater.from(context);
         this.listener = listener;
         mDevices = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public IBeaconDevice getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.listitem_device_scan, parent, false);
            holder = new DeviceHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DeviceHolder) convertView.getTag();
        }

        IBeaconDevice device = getItem(position);
        holder.beaconName.setText(device.getName());
        holder.beaconUUID.setText(device.getUUID());

        String major = Integer.toHexString(device.getMajor());
        String minor = Integer.toHexString(device.getMinor());

        holder.beaconMajor.setText(major);
        holder.beaconMinor.setText(minor);

      //  listener.onBeaconFound(major);

        return convertView;
    }



    public void add(IBeaconDevice device) {
        // Add only unique devices
        boolean isAlreadyInList = false;
        for (IBeaconDevice d : mDevices) {
            if (device.getAddress().equals(d.getAddress())) {
                isAlreadyInList = true;
                break;
            }
        }

        if (!isAlreadyInList) {
            if(device.getUUID().equalsIgnoreCase(BTUtils.SERVICE_UUID)) {
                String major = Integer.toHexString(device.getMajor());

                Timber.d("adding device with major " + major);

                try {
                    listener.onBeaconFound(major);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                mDevices.add(device);
                notifyDataSetChanged();
            }
        }
    }



     void clear() {
         mDevices.clear();
        notifyDataSetChanged();
    }

     public static class DeviceHolder {

        @BindView(R.id.textViewUUID)
        TextView beaconUUID;

         @BindView(R.id.textViewName)
         TextView beaconName;

        @BindView(R.id.textViewMajor1)
        TextView beaconMajor;

         @BindView(R.id.textViewMinor)
         TextView beaconMinor;

         DeviceHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnBeaconFoundListener{
        void onBeaconFound(String majorId) throws IOException;
    }

}