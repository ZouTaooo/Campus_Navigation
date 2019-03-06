package com.example.campus_navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //是否是第一次定位
    private boolean isFirstLoc = true;

    //private PoiSearch mPoiSearch;

    private BaiduMap mBaiduMap;

    private MapView mMapView = null;

    private WalkNavigateHelper walkNavigateHelper;

    private BikeNavigateHelper bikeNavigateHelper;

    private LocationClient mLocationClient;

    private  LatLng myLoc;

    private BaiduMap.OnMapLongClickListener mapLongClickListener = new BaiduMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(final LatLng latLng) {
            Log.e(TAG, "onMapClick: 长按");
            bikeNavigateHelper = BikeNavigateHelper.getInstance();
            bikeNavigateHelper.initNaviEngine(MainActivity.this, new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.e(TAG, "engineInitSuccess: ");
                    //起终点位置
                    LatLng startPt = myLoc;
                    LatLng endPt = latLng;
                    //构造WalkNaviLaunchParam
                    BikeNaviLaunchParam mParam = new BikeNaviLaunchParam().stPt(startPt).endPt(endPt);
                    bikeNavigateHelper.routePlanWithParams(mParam, new IBRoutePlanListener() {
                        @Override
                        public void onRoutePlanStart() {
                            Log.e(TAG, "onRoutePlanStart: ");
                        }

                        @Override
                        public void onRoutePlanSuccess() {
                            //算路成功
                            //跳转至诱导页面
                            Intent intent = new Intent(MainActivity.this, BNaviGuideActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {
                            Log.e(TAG, "onRoutePlanFail: ");
                        }
                    });
                }

                @Override
                public void engineInitFail() {
                    Log.e(TAG, "engineInitFail: ");
                }
            });
//             获取导航控制类
//             引擎初始化
//            walkNavigateHelper = WalkNavigateHelper.getInstance();
//            walkNavigateHelper.initNaviEngine(MainActivity.this, new IWEngineInitListener() {
//
//                @Override
//                public void engineInitSuccess() {
//                    //引擎初始化成功的回调
//                    Log.e(TAG, "engineInitSuccess: 初始化成功");
//                    routeWalkPlanWithParam(latLng, walkNavigateHelper);
//                }
//
//                @Override
//                public void engineInitFail() {
//                    //引擎初始化失败的回调
//                }
//            });
        }
    };

    //点击回调接口
    private BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(final LatLng latLng) {
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            Intent intent = new Intent(MainActivity.this, PanoramaViewActivity.class);
            intent.putExtra("PoiUid", mapPoi.getUid());
            intent.putExtra("PoiName", mapPoi.getName());
            startActivity(intent);
            return false;
        }
    };

//    private void routeWalkPlanWithParam(LatLng latLng, WalkNavigateHelper walkNavigateHelper) {
//        Log.e(TAG, "routeWalkPlanWithParam: 开始算路");
//        //起终点位置
//        LatLng startPt = new LatLng(30.756461, 103.93483);
//        LatLng endPt = latLng;
//        //构造WalkNaviLaunchParam
//        WalkNaviLaunchParam mParam = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);
//        walkNavigateHelper.routePlanWithParams(mParam, new IWRoutePlanListener() {
//            @Override
//            public void onRoutePlanStart() {
//                Log.e(TAG, "onRoutePlanStart: 算路开始");
//            }
//
//            @Override
//            public void onRoutePlanSuccess() {
//                Log.e(TAG, "onRoutePlanSuccess: 算路成功");
//                Intent intent = new Intent(MainActivity.this, WNaviGuideActivity.class);
//                Log.e(TAG, "onRoutePlanSuccess: Intent初始化成功");
//                startActivity(intent);
//            }
//
//            @Override
//            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
//                Log.e(TAG, "onRoutePlanSuccess: 算路失败");
//            }
//        });
//    }

//    private OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
//        @Override
//        public void onGetPoiResult(PoiResult poiResult) {
//            Log.e(TAG, "onGetPoiDetailResult: error" + poiResult.error);
//            for (PoiInfo x : poiResult.getAllPoi()) {
//                Log.e(TAG, "onGetPoiDetailResult: Address" + x.getAddress() + "Uid:" + x.getUid() + "Name: " + x.getName());
//            }
//        }
//
//        @Override
//        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
//            Log.e(TAG, "onGetPoiDetailResult: error" + poiDetailSearchResult.error);
//            for (PoiDetailInfo x : poiDetailSearchResult.getPoiDetailInfoList()) {
//                Log.e(TAG, "onGetPoiDetailResult: Address" + x.getAddress() + "Uid:" + x.getUid());
//            }
//        }
//
//        @Override
//        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//
//        }
//
//        //废弃
//        @Override
//        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//
//        }
//    };
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e(TAG, "onReceiveLocation: Longitude: " + location.getLongitude() + "Latitude: " + location.getLatitude() + "error: " + location.getLocType());
            mBaiduMap.setMyLocationData(locData);

            //配置定位图层显示方式
            //有两个不同的构造方法重载 分别为三个参数和五个参数的
            //这里主要讲一下常用的三个参数的构造方法
            //三个参数：LocationMode(定位模式：罗盘，跟随),enableDirection（是否允许显示方向信息）
            // ,customMarker（自定义图标）
            MyLocationConfiguration configuration = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, false, null);

            mBaiduMap.setMyLocationConfiguration(configuration);

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            //第一次定位需要更新下地图显示状态
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder()
                        .target(ll)//地图缩放中心点
                        .zoom(18f);//缩放倍数 百度地图支持缩放21级 部分特殊图层为20级
                //改变地图状态
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onLocDiagnosticMessage(int i, int i1, String s) {
            Log.e(TAG, "onLocDiagnosticMessage: " + i + "   " + i1 + "   " + s);
            super.onLocDiagnosticMessage(i, i1, s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("校园地图");

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        //普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMapClickListener(mapClickListener);
        mBaiduMap.setOnMapLongClickListener(mapLongClickListener);
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        //开启地图定位图层
        mLocationClient.start();
    }

//    private void PoiSearch() {
//        Log.e(TAG, "PoiSearch: " + 1);
//        mPoiSearch = PoiSearch.newInstance();
//        mPoiSearch.setOnGetPoiSearchResultListener(listener);
//        Log.e(TAG, "PoiSearch: 2");
//        mPoiSearch.searchInCity(new PoiCitySearchOption()
//                .city("成都") //必填
//                .keyword("电子科技大学") //必填
//                .pageNum(10));
//        Log.e(TAG, "PoiSearch: 3");
//    }
    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        //mPoiSearch.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //PoiSearch();
            //startActivity(new Intent(MainActivity.this, PanoramaViewActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

