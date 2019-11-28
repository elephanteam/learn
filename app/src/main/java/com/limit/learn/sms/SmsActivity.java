package com.limit.learn.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Button;

import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SmsActivity extends BaseActivity {

    @BindView(R.id.sms_balance_value)
    Button smsBalanceValue;
    @BindView(R.id.sms_type)
    Button smsType;

    //0 中国联通 1 中国移动 2 中国电信
    private int simType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sms;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        getSIMType();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(receiver,filter);
    }

    @OnClick(R.id.sms_balance)
    public void onClickSmsBalanceView(){
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        List<String> divideContents = smsManager.divideMessage("YE");
        for(String text : divideContents){
            if (simType == 0){
                smsManager.sendTextMessage("10010", null, text, null, null);
            }else if (simType == 1){
                smsManager.sendTextMessage("10086", null, text, null, null);
            }else if (simType == 2){
                smsManager.sendTextMessage("10000", null, text, null, null);
            }
        }
    }

    private void getSIMType(){
        try {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                String operator = telephonyManager.getSimOperator();
                if(operator != null){
                    switch (operator) {
                        case "46000":
                        case "46002":
                        case "46007":
                            //中国移动
                            simType = 1;
                            smsType.setText("中国移动");
                            break;
                        case "46001":
                        case "46009":
                            //中国联通
                            simType = 0;
                            smsType.setText("中国联通");
                            break;
                        case "46003":
                            //中国电信
                            simType = 2;
                            smsType.setText("中国电信");
                            break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String format = intent.getStringExtra("format");
            if (bundle != null) {
                //根据pdus关键字获取短信字节数组，数组内的每个元素都是一条短信
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus == null || pdus.length <= 0){
                    return;
                }
                for (Object object : pdus) {
                    SmsMessage message;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        message = SmsMessage.createFromPdu((byte[])object,format);
                    }else{
                        message = SmsMessage.createFromPdu((byte[])object);
                    }
                    String balanceMessage = message.getMessageBody();
                    if (balanceMessage.contains("可用余额") && balanceMessage.contains("元")){
                        String balanceValue = balanceMessage.substring(balanceMessage.indexOf("可用余额"),balanceMessage.indexOf("元"));
                        smsBalanceValue.setText(balanceValue);
                    }else{
                        String[] splitValue = message.getMessageBody().split("账户当前可用余额");
                        if (splitValue.length>1){
                            String[] ss = splitValue[1].split("元。");
                            smsBalanceValue.setText(ss[0]);
                        }
                    }
                }
            }
        }
    };
}
