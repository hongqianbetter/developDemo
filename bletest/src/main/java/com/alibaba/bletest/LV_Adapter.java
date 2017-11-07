package com.alibaba.bletest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hongqian.wang on 2017/9/1.
 */

class LV_Adapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> devicesList;

    public LV_Adapter(MainActivity mainActivity, List<BluetoothDevice> devicesList) {
        this.context = mainActivity;
        this.devicesList = devicesList;
    }

    @Override
    public int getCount() {
        return devicesList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = View.inflate(context, R.layout.item_lv, null);
      TextView tv_name = inflate.findViewById(R.id.tv_name);
      TextView  tv_address = inflate.findViewById(R.id.tv_address);
        BluetoothDevice device = devicesList.get(i);
        tv_name.setText(device.getName());
        tv_address.setText(device.getAddress());

        return inflate;
    }


}
