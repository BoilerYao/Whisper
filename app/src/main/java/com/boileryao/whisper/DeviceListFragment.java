package com.boileryao.whisper;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by boiler-yao on 2016/10/16.
 * List Fragment with GridView
 */

public class DeviceListFragment extends Fragment {
    GridView devicesGridView;
    List<BluetoothDevice> devices;
    List<String> deviceNames;
    BluetoothService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = BluetoothService.getInstance();
        service.openBluetooth();
        devices = new LinkedList<>();
        deviceNames = new LinkedList<>();
        devices = new LinkedList<>(BluetoothService.getInstance().getPairedDevices());
        for (BluetoothDevice device : devices) {
            deviceNames.add(device.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device_list, container, false);
        devicesGridView = (GridView) v.findViewById(R.id.grid_view);
        devicesGridView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.grid_cell_device, deviceNames));
        devicesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), devices.get(position).getName(), Toast.LENGTH_SHORT).show();
                BluetoothDevice device = devices.get(position);
                service.connect(device);
                Intent intent = new Intent(getContext(), DialogActivity.class);
                intent.putExtra("name", deviceNames.get(position));
                intent.putExtra("address", devices.get(position).toString());
                startActivity(intent);
            }
        });
        return v;
    }

}
