package com.example.campus_navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;

public class BNaviGuideActivity extends AppCompatActivity {

    private BikeNavigateHelper mNaviHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bnavi_guide);
        //获取BikeNavigateHelper示例
        mNaviHelper = BikeNavigateHelper.getInstance();
// 获取诱导页面地图展示View
        View view = mNaviHelper.onCreate(BNaviGuideActivity.this);
        if (view != null) {
            setContentView(view);
        }
// 开始导航
        mNaviHelper.startBikeNavi(BNaviGuideActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }
}
