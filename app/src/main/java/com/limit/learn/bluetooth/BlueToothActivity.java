package com.limit.learn.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.util.RequestCodeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.google.gson.internal.bind.TypeAdapters.UUID;

public class BlueToothActivity extends BaseActivity {

    @BindView(R.id.bluetooth_search_content)
    TextView bluetoothContent;

    //获取系统蓝牙适配器管理类
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManger;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private List<BluetoothGattService> servicesList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        bluetoothManger = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManger != null) {
            mBluetoothAdapter = bluetoothManger.getAdapter();
        }else{
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        // 询问打开蓝牙
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, RequestCodeUtils.REQUEST_CODE_OPEN_BLUETOOTH);
        }
    }

    // 申请打开蓝牙请求的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtils.REQUEST_CODE_OPEN_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "没有蓝牙权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @OnClick(R.id.bluetooth_search)
    public void onClickBluetoothSearch() {

    }

    @OnClick(R.id.bluetooth_ble_search)
    public void onClickBleSearch() {
        // 通过 Adapter 的 startLeScan(callBack); 方法来开启扫描
        // 这个方法已经过时了，官方文档最新支持的扫描蓝牙的方法是通过一个专门的对象 BluetoothLeScanner 用来开启扫描低功耗蓝牙
        mBluetoothAdapter.startLeScan(callback);
//        mBluetoothAdapter.stopLeScan(callback);
    }

    private BluetoothAdapter.LeScanCallback callback = (device, arg1, arg2) -> {
        //device为扫描到的BLE设备
        if (TextUtils.isEmpty(device.getName()) || !device.getName().contains("小米")){
            return;
        }
        if (bluetoothContent != null){
            bluetoothContent.setText(device.getName());
            mDevice = device;
            Log.e("**************", "name = " + device.getName() + "\n address = " + device.getAddress() + "\n type = " + device.getType());
            mDevice.connectGatt(BlueToothActivity.this, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        Log.e("***************", "设备连接上 开始扫描服务");
                        // 开始扫描服务，安卓蓝牙开发重要步骤之一
                        mBluetoothGatt.discoverServices();
                    }
                    if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        // 连接断开
                        /*连接断开后的相应处理*/
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    //获取服务列表
                    servicesList = mBluetoothGatt.getServices();
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }
            });
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null && callback != null){
            mBluetoothAdapter.stopLeScan(callback);
            callback = null;
        }
    }
}
