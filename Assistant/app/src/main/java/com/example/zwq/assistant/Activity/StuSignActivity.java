package com.example.zwq.assistant.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.SignInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.SignRecord;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StuSignActivity extends BaseActivity implements SensorEventListener {
    /**
     * 规定到达距离范围距离
     */
    private int DISTANCE = 200;

    private ImageView ivReturn;
    private MapView mMapView;
    private TextView tvDistance;
    private RelativeLayout Arrive;
    public TextView tvTime;
    private BaiduMap mBaiduMap;
    private SensorManager mSensorManager;//方向传感器
    private LatLng mDestinationPoint;//目的地坐标点
    private LocationClient client;//定位监听
    private LocationClientOption mOption;//定位属性
    private MyLocationData locData;//定位坐标
    private InfoWindow mInfoWindow;//地图文字位置提醒
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private int mCurrentDirection = 0;
    private double mDistance = 0;
    private LatLng mCenterPos;
    private float mZoomScale = 0; //比例
    private Double lastX = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());//百度地图
        // 注册 SDK 广播监听者
        RegisterBroadcast();
        setContentView(R.layout.activity_stu_sign);
        initBaiduMap();     //1、初始化地图
        getLocationClientOption();//2、定位开启
        mHandler.post(run);//设置系统时间
        initView();
    }

    public void initView(){
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(this);
        tvDistance = findViewById(R.id.tvDistance);
        tvTime = findViewById(R.id.tvTime);
        Arrive = findViewById(R.id.Arrive);
        Arrive.setOnClickListener(this);
    }

    /**
     * 初始化地图
     */
    private void initBaiduMap() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mMapView = findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
    }

    //设置打卡目标范围圈
    private void setCircleOptions() {
        if (mDestinationPoint == null) return;
        OverlayOptions ooCircle = new CircleOptions().fillColor(0x4057FFF8)
                .center(mDestinationPoint).stroke(new Stroke(1, 0xB6FFFFFF)).radius(DISTANCE);
        mBaiduMap.addOverlay(ooCircle);
    }

    /***
     * 定位选项设置
     * @return
     */
    public void getLocationClientOption() {
        mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(true);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        client = new LocationClient(this);
        client.setLocOption(mOption);
        client.registerLocationListener(BDAblistener);
        client.start();
    }

    /***
     * 接收定位结果消息，并显示在地图上
     */
    private BDAbstractLocationListener BDAblistener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //定位方向
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            //定位
            locData = new MyLocationData.Builder()
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true, null));
            //更改UI
            Message message = new Message();
            message.obj = location;
            mHandler.sendMessage(message);
        }
    };

    /**
     * 处理连续定位的地图UI变化
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BDLocation location = (BDLocation) msg.obj;
            LatLng LocationPoint = new LatLng(location.getLatitude(), location.getLongitude());
            //打卡范围
            mDestinationPoint = new LatLng(28.951441,120.938619);//假设打卡坐标
//            mDestinationPoint = new LatLng(location.getLatitude() ,location.getLongitude());
            setCircleOptions();
            //计算两点距离,单位：米
            mDistance = DistanceUtil.getDistance(mDestinationPoint, LocationPoint);
            if (mDistance <= DISTANCE) {
                //显示文字
                setTextOption(mDestinationPoint, "您已在打卡范围内", "#7ED321");
                //目的地图标
                setMarkerOptions(mDestinationPoint, R.mipmap.arrive_icon);
                //按钮颜色
                Arrive.setBackgroundDrawable(getResources().getDrawable(R.mipmap.restaurant_btbg_yellow));
                mBaiduMap.setMyLocationEnabled(false);
            } else {
                setTextOption(LocationPoint, "您不在打卡范围之内", "#FF6C6C");
                setMarkerOptions(mDestinationPoint, R.mipmap.restaurant_icon);
                Arrive.setBackgroundDrawable(getResources().getDrawable(R.mipmap.restaurant_btbg_gray));
                mBaiduMap.setMyLocationEnabled(true);
            }
            tvDistance.setText("距离目的地：" + mDistance + "米");
            //缩放地图
            setMapZoomScale(LocationPoint);
        }
    };

    /**
     * 添加地图文字
     *
     * @param point
     * @param str
     * @param color 字体颜色
     */
    private void setTextOption(LatLng point, String str, String color) {
        //使用MakerInfoWindow
        if (point == null) return;
        TextView view = new TextView(getApplicationContext());
        view.setBackgroundResource(R.mipmap.map_textbg);
        view.setPadding(0, 23, 0, 0);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextSize(14);
        view.setGravity(Gravity.CENTER);
        view.setText(str);
        view.setTextColor(Color.parseColor(color));
        mInfoWindow = new InfoWindow(view, point, 170);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    /**
     * 设置marker覆盖物
     *
     * @param ll   坐标
     * @param icon 图标
     */
    private void setMarkerOptions(LatLng ll, int icon) {
        if (ll == null) return;
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(icon);
        MarkerOptions ooD = new MarkerOptions().position(ll).icon(bitmap);
        mBaiduMap.addOverlay(ooD);
    }

    //改变地图缩放
    private void setMapZoomScale(LatLng ll) {
        if (mDestinationPoint == null) {
            mZoomScale = getZoomScale(ll);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll, mZoomScale));//缩放
        } else {
            mZoomScale = getZoomScale(ll);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mCenterPos, mZoomScale));//缩放
        }
    }

    /**
     * 获取地图的中心点和缩放比例
     *
     * @return float
     */
    private float getZoomScale(LatLng LocationPoint) {
        double maxLong;    //最大经度
        double minLong;    //最小经度
        double maxLat;     //最大纬度
        double minLat;     //最小纬度
        List<Double> longItems = new ArrayList<>();    //经度集合
        List<Double> latItems = new ArrayList<>();     //纬度集合

        if (null != LocationPoint) {
            longItems.add(LocationPoint.longitude);
            latItems.add(LocationPoint.latitude);
        }
        if (null != mDestinationPoint) {
            longItems.add(mDestinationPoint.longitude);
            latItems.add(mDestinationPoint.latitude);
        }

        maxLong = longItems.get(0);    //最大经度
        minLong = longItems.get(0);    //最小经度
        maxLat = latItems.get(0);     //最大纬度
        minLat = latItems.get(0);     //最小纬度

        for (int i = 0; i < longItems.size(); i++) {
            maxLong = Math.max(maxLong, longItems.get(i));   //获取集合中的最大经度
            minLong = Math.min(minLong, longItems.get(i));   //获取集合中的最小经度
        }

        for (int i = 0; i < latItems.size(); i++) {
            maxLat = Math.max(maxLat, latItems.get(i));   //获取集合中的最大纬度
            minLat = Math.min(minLat, latItems.get(i));   //获取集合中的最小纬度
        }
        double latCenter = (maxLat + minLat) / 2;
        double longCenter = (maxLong + minLong) / 2;
        int jl = (int) getDistance(new LatLng(maxLat, maxLong), new LatLng(minLat, minLong));//缩放比例参数
        mCenterPos = new LatLng(latCenter, longCenter);   //获取中心点经纬度
        int zoomLevel[] = {2500000, 2000000, 1000000, 500000, 200000, 100000,
                50000, 25000, 20000, 10000, 5000, 2000, 1000, 500, 100, 50, 20, 0};
        int i;
        for (i = 0; i < 18; i++) {
            if (zoomLevel[i] < jl) {
                break;
            }
        }
        float zoom = i + 4;
        return zoom;
    }

    /**
     * 缩放比例参数
     *
     * @param var0
     * @param var1
     * @return
     */
    public double getDistance(LatLng var0, LatLng var1) {
        if (var0 != null && var1 != null) {
            Point var2 = CoordUtil.ll2point(var0);
            Point var3 = CoordUtil.ll2point(var1);
            return var2 != null && var3 != null ? CoordUtil.getDistance(var2, var3) : -1.0D;
        } else {
            return -1.0D;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 设置系统时间
     */
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
            Date date = new Date(System.currentTimeMillis());//获取当前时间
            tvTime.setText(simpleDateFormat.format(date)); //更新时间
            mHandler.postDelayed(run, 1000);
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        int signID = Integer.parseInt(intent.getStringExtra("signID"));
        switch (v.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.Arrive:
                if (mDistance <= DISTANCE) {
                    int uid = UserInfoManager.getInstance().getUid();
                    long signDate = System.currentTimeMillis();
                    RetrofitManager.getInstance().createReq(SignInfo.class)
                            .sign(signID,uid,signDate)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<HttpResult<SignRecord>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(HttpResult<SignRecord> signRecordHttpResult) {
                                    Toast.makeText(StuSignActivity.this, signRecordHttpResult.getMsg(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    Toast.makeText(this, "不在范围内，无法签到", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        if (BDAblistener != null) {
            client.unRegisterLocationListener(BDAblistener);

        }
        if (client != null && client.isStarted()) {
            client.stop();
        }
        mMapView.onDestroy();
        mMapView = null;
        mHandler.removeCallbacks(run);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    private SDKReceiver mReceiver;
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String tx = "";

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {

                tx = "key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置";
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                tx ="key 验证成功! 功能可以正常使用";
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                tx = "网络出错";
            }
            if (tx.contains("错")){
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(context);
                normalDialog.setTitle("提示");
                normalDialog.setMessage(tx);
                normalDialog.setPositiveButton("确定", null);
                normalDialog.setNegativeButton("关闭", null);
                // 显示
                normalDialog.show();
            }else {
                Toast.makeText(context,tx,Toast.LENGTH_SHORT).show();
            }

        }
    }
    protected void RegisterBroadcast(){
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }
}
