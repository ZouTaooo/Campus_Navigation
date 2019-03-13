package com.example.campus_navigation;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;

public class BNaviGuideActivity extends Activity {

    private BikeNavigateHelper mNaviHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bnavi_guide);
        setTitle("导航");
        //getActionBar().setDisplayHomeAsUpEnabled(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://增加点击事件
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
