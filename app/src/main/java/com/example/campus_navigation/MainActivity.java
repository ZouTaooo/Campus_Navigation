package com.example.campus_navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

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
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiFilter;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //是否是第一次定位
    private boolean isFirstLoc = true;

    //private PoiSearch mPoiSearch;

//    public VoiceRippleView voiceRipple;

    private BaiduMap mBaiduMap;

    private MapView mMapView = null;

    private MaterialSearchView searchView;

    private WalkNavigateHelper walkNavigateHelper;

    private BikeNavigateHelper bikeNavigateHelper;

    private LocationClient mLocationClient;

    private Toolbar mToolbar;

    private LatLng myLoc;

    private String city;

    //private SuggestionSearch mSuggestionSearch;

    private boolean isSubmit;

    private PoiSearch mPoiSearch;

    private String district;

//    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
//        @Override
//        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
//            //处理sug检索结果
//            if (suggestionResult.getAllSuggestions() != null) {
//                List<SuggestionResult.SuggestionInfo> list = suggestionResult.getAllSuggestions();
//                int num = list.size();
//                if (num > 0) {
//                    String[] result = new String[num];
//                    for (int i = 0; i < num; i++) {
//                        result[i] = list.get(i).getKey();
//                        Log.e(TAG, "onGetSuggestionResult: " + result[i]);
//                    }
//                    searchView.setSuggestions(result);
////                    Random random = new Random();
////                    searchView.setSuggestions(new String[] {"1"+random.nextInt(),"2"+random.nextInt(),"3"+random.nextInt()});
//                    searchView.showSuggestions();
//
//                }
//            }
//        }
//    };

    //地图交互接口
    private BaiduMap.OnMapLongClickListener mapLongClickListener = new BaiduMap.OnMapLongClickListener() {

        @Override
        public void onMapLongClick(final LatLng latLng) {
            Log.e(TAG, "onMapClick: 长按");
            showListDialog(latLng);
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

    private OnGetPoiSearchResultListener onGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
//            List<PoiInfo> poiInfos = poiResult.getAllPoi();
//            if (poiInfos != null) {
//                int num = poiInfos.size();
//                if (num > 0) {
//                    String[] result = new String[num];
//                    for (int i = 0; i < num; i++) {
//                        result[i] = poiInfos.get(i).name + "    " + poiInfos.get(i).getAddress();
//                        Log.e(TAG, "onGetPoiResult: " + result[i]);
//                    }
//                    searchView.setSuggestions(result);
//
//                    //searchView.showSuggestions();
//                }
//            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();

                //创建PoiOverlay对象
                PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);

                //设置Poi检索数据
                poiOverlay.setData(poiResult);

                //将poiOverlay添加至地图并缩放至合适级别
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        mSuggestionSearch = SuggestionSearch.newInstance();
//        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(onGetPoiSearchResultListener);
        isSubmit = false;
//        EventBus.getDefault().register(this);
    }

    private void initView() {
        setUpToolbar();
        setUpBDMap();
        setUpSearchView();
    }

    private void setUpSearchView() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: " + query);
                //Do some magic
                /**
                 *  PoiCiySearchOption 设置检索属性
                 *  city 检索城市
                 *  keyword 检索内容关键字
                 *  pageNum 分页页码
                 */
                isSubmit = true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                /**
                 * 在您的项目中，keyword为随您的输入变化的值
                 */
                Log.e(TAG, "onQueryTextChange: 设置CITY" + city);
                if (!isSubmit) {
                    mPoiSearch.searchNearby(new PoiNearbySearchOption()
                            .location(myLoc)
                            .radius(500)
                            .keyword(newText + "$" + district)
                            .scope(2)
                            .poiFilter(new PoiFilter.Builder()
                                    .sortName(PoiFilter.SortName.CaterSortName.CATER_DISTANCE)
                                    .sortRule(1)
                                    .build())
                            .pageNum(0)
                            .pageCapacity(1)
                    );
                }
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                isSubmit = false;
                Log.e(TAG, "onSearchViewShown: ");
            }

            @Override
            public void onSearchViewClosed() {
                Log.e(TAG, "onSearchViewClosed: ");
                mBaiduMap.clear();
                //Do some magic
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSubmit = true;
            }
        });
        searchView.setVoiceSearch(true); //or false
        searchView.setSuggestionIcon(getResources().getDrawable(R.drawable.ic_place_grey600_18dp));
    }

    private void setUpBDMap() {
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
        option.setIsNeedAddress(true);
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

    private void setUpToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("校园地图");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
//        voiceRipple.onStop();
        super.onStop();
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
//        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
//        voiceRipple.onDestroy();
        //mPoiSearch.destroy();
//        if(EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    private void showListDialog(final LatLng latLng) {
        final String[] items = {"步行导航", "骑行导航"};
        final AlertDialog.Builder listDialog =
                new AlertDialog.Builder(MainActivity.this);
        listDialog.setTitle("请选择导航模式");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startWalkNavigation(latLng);
                        break;
                    case 1:
                        startBikeNavigation(latLng);
                        break;
                    default:
                        break;
                }
            }
        });
        listDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        listDialog.show();
    }

    private void routeWalkPlanWithParam(LatLng latLng, WalkNavigateHelper walkNavigateHelper) {
        Log.e(TAG, "routeWalkPlanWithParam: 开始算路");
        //起终点位置
        LatLng startPt = myLoc;
        LatLng endPt = latLng;
        //构造WalkNaviLaunchParam
        WalkNaviLaunchParam mParam = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);
        walkNavigateHelper.routePlanWithParams(mParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.e(TAG, "onRoutePlanStart: 算路开始");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.e(TAG, "onRoutePlanSuccess: 算路成功");
                Intent intent = new Intent(MainActivity.this, WNaviGuideActivity.class);
                Log.e(TAG, "onRoutePlanSuccess: Intent初始化成功");
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                Log.e(TAG, "onRoutePlanSuccess: 算路失败");
            }
        });
    }

    private void startWalkNavigation(final LatLng latLng) {
        //             获取导航控制类
//             引擎初始化
        walkNavigateHelper = WalkNavigateHelper.getInstance();
        walkNavigateHelper.initNaviEngine(MainActivity.this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //引擎初始化成功的回调
                Log.e(TAG, "engineInitSuccess: 初始化成功");
                routeWalkPlanWithParam(latLng, walkNavigateHelper);
            }

            @Override
            public void engineInitFail() {
                //引擎初始化失败的回调
            }
        });
    }

    private void startBikeNavigation(final LatLng latLng) {
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
    }

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
            city = location.getCity();
            district = location.getDistrict();
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
}

