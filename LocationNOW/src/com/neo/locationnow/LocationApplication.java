package com.neo.locationnow;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class LocationApplication extends Application {
	static LocationApplication LocApp;
	// 百度MapAPI的管理类
	BMapManager mBMapMan = null;
	// 授权Key
	// 申请地址：http://developer.baidu.com/map/android-mobile-apply-key.htm
	public static String mStrKey = "3B6C6FC640621C8E7A524A382895B933B0685254";
	boolean m_bKeyRight = true; // 授权Key正确，验证通过

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + iError);
			Toast.makeText(LocationApplication.LocApp.getApplicationContext(),
					"您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(
						LocationApplication.LocApp.getApplicationContext(),
						"请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG)
						.show();
				LocationApplication.LocApp.m_bKeyRight = false;
			}
		}
	}

	@Override
	public void onCreate() {
		Log.v("BMapApiDemoApp", "onCreate");
		LocApp = this;
		mBMapMan = new BMapManager(this);
		boolean isSuccess = mBMapMan
				.init(LocationApplication.mStrKey, new MyGeneralListener());
		// 初始化地图sdk成功，设置定位监听时间
		if (isSuccess) {
			mBMapMan.getLocationManager().setNotifyInternal(10, 5);
		} else {
			// 地图sdk初始化失败，不能使用sdk
		}
		super.onCreate();
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

}
