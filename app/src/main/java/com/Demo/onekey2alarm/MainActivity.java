package com.Demo.onekey2alarm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.net.sip.SipAudioCall.Listener;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerBase;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.LatLng;


import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener
,AMapLocationListener,LocationSource,OnClickListener{
	
	private  MapView mapView;
	private  AMap aMap;
	
	  
	//定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能
    private AMapLocationClient aMapLocationClient;
    //声明定位回调监听器
    private OnLocationChangedListener listener;
    //定位参数设置
    private AMapLocationClientOption aMapLocationClientOption;
    //菜单按钮
    private Button bt_c,bt_m,bt_a;
    private LatLng myLocation = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private UiSettings mUiSettings;
    private boolean isPerview;
    //报警按钮
  	private Button bt;
 
  	//messages
  	static String Messages;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 
		
		bt = (Button) findViewById(R.id.bt_Location);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v1) {
				
				
				//Toast.makeText(MainActivity.this, "已报警",1).show();
				Toast.makeText(MainActivity.this, Messages,1).show();
				sendMessages(contacts.item_num,messages.msn.getText().toString());
			}
		});
		
		bt_c = (Button) findViewById(R.id.bt_contacts);
		bt_m = (Button) findViewById(R.id.bt_messages);
		bt_a = (Button) findViewById(R.id.bt_about);
		bt_c.setOnClickListener(this);
		bt_m.setOnClickListener(this);
		bt_a.setOnClickListener(this);
	
	
		mapView = (MapView) findViewById(R.id.map);

		mapView.onCreate(savedInstanceState);
		
		//初始化定位
		initMap();
		
		
	}
	 
	private void sendMessages(String number, String message){
	    String SENT = "sms_sent";
	    String DELIVERED = "sms_delivered";
	    
	    PendingIntent sentPI = PendingIntent.getActivity(this, 0, new Intent(SENT), 0);
	    PendingIntent deliveredPI = PendingIntent.getActivity(this, 0, new Intent(DELIVERED), 0);
	    
	    registerReceiver(new BroadcastReceiver(){
	 
	            @Override
	            public void onReceive(Context context, Intent intent) {
	                switch(getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Log.i("====>", "Activity.RESULT_OK");
	                        Toast.makeText(MainActivity.this, "短信发送成功",1).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                        Log.i("====>", "RESULT_ERROR_GENERIC_FAILURE");
	                        Toast.makeText(MainActivity.this, "短信发送失败",1).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NO_SERVICE:
	                        Log.i("====>", "RESULT_ERROR_NO_SERVICE");
	                        Toast.makeText(MainActivity.this, "无服务",1).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NULL_PDU:
	                        Log.i("====>", "RESULT_ERROR_NULL_PDU");
	                        break;
	                    case SmsManager.RESULT_ERROR_RADIO_OFF:
	                        Log.i("====>", "RESULT_ERROR_RADIO_OFF");
	                        break;
	                }
	            }
	    }, new IntentFilter(SENT));
	    
	    registerReceiver(new BroadcastReceiver(){
	        @Override
	        public void onReceive(Context context, Intent intent){
	            switch(getResultCode())
	            {
	                case Activity.RESULT_OK:
	                    Log.i("====>", "RESULT_OK");
	                    break;
	                case Activity.RESULT_CANCELED:
	                    Log.i("=====>", "RESULT_CANCELED");
	                    break;
	            }
	        }
	    }, new IntentFilter(DELIVERED));
	    
	        SmsManager smsm = SmsManager.getDefault();
	        smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
	}
	  /** 
     * 初始化AMap对象 
     */  
	private void initMap() {  
         
            aMap = mapView.getMap();  
            mUiSettings = aMap.getUiSettings();  
            
        // 自定义系统定位小蓝点  
        MyLocationStyle myLocationStyle = new MyLocationStyle();  
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory  
                .fromResource(R.drawable.transdrawable));// 设置小蓝点的图标  
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色  
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色  
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
       
        aMap.setMyLocationStyle(myLocationStyle);  
        aMap.setMyLocationRotateAngle(180);  
        aMap.setLocationSource(this);// 设置定位监听  
        mUiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮  
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);  
        mUiSettings.setTiltGesturesEnabled(false);// 设置地图是否可以倾斜  
        mUiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示  
        mUiSettings.setZoomControlsEnabled(false);  
        
        //设置地图类型
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        //自定义定位图标
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.transdrawable));
        locationStyle.strokeColor(Color.RED);
        locationStyle.strokeWidth(5);
        aMap.setMyLocationStyle(locationStyle);
        
        // 设置定位监听
        aMap.setLocationSource((LocationSource) this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        // 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false
        aMap.setMyLocationEnabled(true);
        
        // 去除缩放按钮
        aMap.getUiSettings().setZoomControlsEnabled(false);
        
        // 添加指南针  
        //aMap.getUiSettings().setCompassEnabled(true);
        //初始化定位
	    aMapLocationClient = new AMapLocationClient(getApplicationContext());
	    //设置定位回调监听
	    aMapLocationClient.setLocationListener((AMapLocationListener) this);
		
	    //初始化定位参数
	    aMapLocationClientOption = new AMapLocationClientOption();
	    //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
	    aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
	    //设置是否返回地址信息（默认返回地址信息）
	    aMapLocationClientOption.setNeedAddress(true);
	    //设置是否只定位一次,默认为false
	    aMapLocationClientOption.setOnceLocation(false);
	    //设置是否强制刷新WIFI，默认为强制刷新
	    aMapLocationClientOption.setWifiActiveScan(true);
	    //设置是否允许模拟位置,默认为false，不允许模拟位置
	    aMapLocationClientOption.setMockEnable(false);
	    //设置定位间隔,单位毫秒,默认为2000ms
	    aMapLocationClientOption.setInterval(2000);
	    //给定位客户端对象设置定位参数
	    aMapLocationClient.setLocationOption(aMapLocationClientOption);
	    //启动定位
	    aMapLocationClient.startLocation();	    
	}
	/**
	    * 定位回调监听，当定位完成后调用此方法
	    * @param aMapLocation
	    */
		@Override
		public void onLocationChanged(AMapLocation aMapLocation) {
			if(listener!=null && aMapLocation!=null) {
	           listener.onLocationChanged(aMapLocation);// 显示系统小蓝点
	           if (aMapLocation.getErrorCode() == 0) {
	               //定位成功回调信息，设置相关消息
	               aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
	               aMapLocation.getLatitude();//获取经度
	               aMapLocation.getLongitude();//获取纬度;
	               aMapLocation.getAccuracy();//获取精度信息
	               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	               Date date = new Date(aMapLocation.getTime());
	               df.format(date);//定位时间
	               aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
	               aMapLocation.getCountry();//国家信息
	               aMapLocation.getProvince();//省信息
	               aMapLocation.getCity();//城市信息
	               aMapLocation.getDistrict();//城区信息
	               aMapLocation.getRoad();//街道信息
	               aMapLocation.getCityCode();//城市编码
	               aMapLocation.getAdCode();//地区编码
	           		} // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
	           		if (isFirstLoc) {
	        	   
 	               //设置缩放级别
 	               aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
 	               //将地图移动到定位点
 	               aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(  
 	                       aMapLocation.getLatitude(), aMapLocation.getLongitude())));  
 	               //点击定位按钮 能够将地图的中心移动到定位点
 	               listener.onLocationChanged(aMapLocation);
 	               //添加图钉
 	               //aMap.addMarker(getMarkerOptions(aMapLocation));
 	               //获取定位信息
 	               StringBuffer buffer = new StringBuffer();
 	               buffer.append(aMapLocation.getCountry() + "," + aMapLocation.getProvince() + "," + aMapLocation.getCity() + "," + aMapLocation.getProvince() + "," + aMapLocation.getDistrict() + "," + aMapLocation.getStreet() + "," + aMapLocation.getStreetNum());
 	               Messages = buffer.toString();
 	               Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
 	               isFirstLoc = false;
	           	}
 	       } else {
 	           //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
 	           Log.e("AmapError", "location Error, ErrCode:"
 	                   + aMapLocation.getErrorCode() + ", errInfo:"
 	                   + aMapLocation.getErrorInfo());
 	           Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
 	       }
	      
			}
	    
    
  
    
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_contacts:
			Intent intent = new Intent(MainActivity.this, contacts.class);
			startActivity(intent);
			break;
		case R.id.bt_messages:
			Intent intent1 = new Intent(MainActivity.this, messages.class);
			startActivity(intent1);
			break;
		case R.id.bt_about:
			Intent intent2 = new Intent(MainActivity.this, about.class);
			startActivity(intent2);
			break;
		}
	}
	
	
		
		  



   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
   }
   @Override
   public void activate(OnLocationChangedListener onLocationChangedListener) {
	    
	   listener = onLocationChangedListener;
   }
   @Override
   public void deactivate() {
	   listener = null;
   }

	
   	@Override
   	protected void onDestroy() {
	    super.onDestroy();
	    //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
	    mapView.onDestroy();
	    //销毁定位客户端
	    if(aMapLocationClient!=null)
	    {
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
            aMapLocationClientOption = null;
        }
    }
	 
	 @Override
	 protected void onResume() {
	    super.onResume();
	    //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
	    mapView.onResume();
	    }
	 @Override
	 protected void onPause() {
	    super.onPause();
	    //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
	    mapView.onPause();
	    }
	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
	    mapView.onSaveInstanceState(outState);
	  }
      
	}
