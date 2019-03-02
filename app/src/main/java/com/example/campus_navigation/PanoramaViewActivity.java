package com.example.campus_navigation;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.mapapi.utils.poi.BaiduMapPoiSearch;

public class PanoramaViewActivity extends AppCompatActivity {

    private PanoramaView mPanoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama_view);
        Intent intent = getIntent();
        String uid = intent.getStringExtra("PoiUid");
        String name = intent.getStringExtra("PoiName");
        mPanoView = findViewById(R.id.panorama);
        MyApplication app = (MyApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);

            app.mBMapManager.init(new MyApplication.MyGeneralListener());
        }
        mPanoView.setPanoramaByUid(uid,PanoramaView.PANOTYPE_STREET);
        android.app.ActionBar actionBar = getActionBar();
        setTitle("全景图-"+name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPanoView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanoView.onResume();
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        super.onDestroy();
    }
}
