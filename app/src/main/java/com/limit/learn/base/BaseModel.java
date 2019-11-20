package com.limit.learn.base;

import android.text.TextUtils;

import com.limit.learn.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * MVP model 基类
 */
public class BaseModel {

    /**
     * 生成 RequestBody 网络请求
     */
    protected String getRequestJson(boolean needToken,String service,String method,Map<String,String> params) {

        if (needToken && (BaseApplication.myInfo == null || TextUtils.isEmpty(BaseApplication.myInfo.getToken()))){
            return null;
        }

        boolean isEncrypt = false ; // 是否加密
        if(params != null && params.containsKey("encrypt")){
            String encrypt = params.get("encrypt");
            isEncrypt = TextUtils.equals("1", encrypt);   // 1: 加密, 0: 不加密
            params.remove("encrypt");
        }

        JSONObject jsonParams = new JSONObject();
        JSONObject jsonBody = new JSONObject();
        try {
            if(params != null){
                for (Map.Entry<String, String> s : params.entrySet()) {
                    jsonParams.put(s.getKey(), s.getValue());
                }
            }
            jsonParams.put("lat", 0);
            jsonParams.put("lng", 0);
            jsonParams.put("system", "android" + android.os.Build.VERSION.RELEASE);
            jsonParams.put("model", android.os.Build.MODEL);
            try {
//                jsonParams.put("imei", Utils.getIMEI(BaseApplication.getContext()));
                jsonParams.put("resolution", Utils.getScreenPixels(BaseApplication.getContext()));
                jsonParams.put("version", Utils.getVersionName(BaseApplication.getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonBody.put("lang","1");
            jsonBody.putOpt("service", service);
            jsonBody.putOpt("method", method);
            jsonBody.putOpt("encrypt", isEncrypt ? "1":"0");
            jsonBody.putOpt("sn", UUID.randomUUID().toString());
            jsonBody.putOpt("params", jsonParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody.toString();
    }

}
