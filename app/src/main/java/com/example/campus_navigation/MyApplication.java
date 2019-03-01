package com.example.campus_navigation;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class MyApplication extends Application {
    private static MyApplication mInstance = null;
    public BMapManager mBMapManager = null;

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        mInstance = this;
        initEngineManager(this);
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(MyApplication.getInstance().getApplicationContext(), "BMapManager  初始化错误!",
                    Toast.LENGTH_LONG).show();
        }
        Log.d("ljx", "initEngineManager");
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int iError) {
            // 非零值表示key验证未通过
            if (iError != 0) {
                // 授权Key错误：
                Log.e(TAG, "onGetPermissionState: " + "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError);
                Toast.makeText(MyApplication.getInstance().getApplicationContext(),
                        "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "onGetPermissionState: \"key认证成功\"");
                Toast.makeText(MyApplication.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}