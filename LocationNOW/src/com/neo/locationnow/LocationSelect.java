package com.neo.locationnow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.PoiOverlay;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.neo.locationnow.R;
import com.neo.xml.SaxResultService;
import com.neo.xml.xml_result;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationSelect extends MapActivity implements OnClickListener,
		OnItemClickListener, OnTouchListener {
	static BMapManager mBMapMan = null;
	static MapView mMapView = null;

	public ArrayList<GeoPoint> geo = new ArrayList<GeoPoint>();
	public ArrayList<String> addr = new ArrayList<String>();
	public ArrayList<String> name = new ArrayList<String>();

	private TextView LocInfo = null;
	private TextView LocBubble = null;
	private TextView LocTitle = null;
	private Button SearchBtn = null;
	private Button ListBtn = null;
	private FrameLayout LocFrame = null;

	private ListView LocationList = null;
	public static GeoPoint pt;
	public static GeoPoint MyLoc;
	public boolean istouch = false;
	private MyAdapter adapter;
	private ImageView im;
	private String urlstr1 = null;
	private String urlstr2 = null;
	private String urlstr3 = null;
	private String resultstr = null;
	MyLocationOverlay mLocationOverlay = null; // 定位图层
	LocationListener mLocationListener = null;// onResume时注册此listener，onPause时需要Remove
	MKSearch mMKSearch = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_select);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(LocationApplication.mStrKey, null);
		// 如果使用地图SDK，请初始化地图Activity
		super.initMapActivity(mBMapMan);
		LocInfo = (TextView) findViewById(R.id.Tv);
		LocBubble = (TextView) findViewById(R.id.LocationNow);
		LocTitle = (TextView) findViewById(R.id.LocationTitle);
		SearchBtn = (Button) findViewById(R.id.btn);
		SearchBtn.setOnClickListener(this);
		ListBtn = (Button) findViewById(R.id.poi_btn);
		ListBtn.setOnClickListener(this);
		LocFrame = (FrameLayout) findViewById(R.id.LocLayout);
		im = (ImageView) findViewById(R.id.im);
		im.setVisibility(View.INVISIBLE);
		LocationList = (ListView) findViewById(R.id.listView1);
		adapter = new MyAdapter(LocationSelect.this);
		LocationList.setAdapter(adapter);
		LocationList.setOnItemClickListener(this);
		adapter.notifyDataSetChanged();

		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		mMapView.setDrawOverlayWhenZooming(true);
		mMapView.setOnTouchListener(this);
		// 添加我的位置定位图层
		mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		// 注册定位事件
		// 初始化搜索模块，注册事件监听
		MKSearchListener mkSearchListener = new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				// 将地图移动到第一个POI中心点
				if (arg0.getCurrentNumPois() > 0) {
					// 将poi结果显示到地图上
					MyPoiOverlay poiOverlay = new MyPoiOverlay(
							LocationSelect.this, mMapView, mMKSearch);
					poiOverlay.setData(arg0.getAllPoi());
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(poiOverlay);
					mMapView.invalidate();
				} else if (arg0.getCityListNum() > 0) {
					String strInfo = "在";
					for (int i = 0; i < arg0.getCityListNum(); i++) {
						strInfo += arg0.getCityListInfo(i).city;
						strInfo += ",";
					}
					strInfo += "找到结果";
					Toast.makeText(LocationSelect.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
				if (arg0 != null) {
					geo = new ArrayList<GeoPoint>();
					name = new ArrayList<String>();
					addr = new ArrayList<String>();
					for (int i = 0; i < (arg0.getAllPoi()).size(); i++) {
						geo.add((arg0.getAllPoi()).get(i).pt);
						name.add((arg0.getAllPoi()).get(i).name);
						addr.add((arg0.getAllPoi()).get(i).address);
					}

				}
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetRGCShareUrlResult(String arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		};

		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, mkSearchListener);

		mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					pt = new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
					MyLoc = new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
					mMapView.getController().animateTo(pt);
					mMapView.getController().setZoom(17); // 设置地图zoom级别
					Log.d("latitude", pt.getLatitudeE6() + "");
					Log.d("Longitude", pt.getLongitudeE6() + "");
					MyLoc = pt;
					im.setVisibility(View.VISIBLE);
					new AsyncTask<Integer, Integer, String[]>() {
						protected void onPreExecute() {
							LocBubble.setVisibility(View.GONE);
							LocFrame.setVisibility(View.VISIBLE);
							super.onPreExecute();
						}

						@Override
						protected void onCancelled() {
							super.onCancelled();
						}

						protected String[] doInBackground(Integer... params) {
							// TODO
							RGeocode(MyLoc);
							return null;
						}

						protected void onPostExecute(String[] result) {
							super.onPostExecute(result);
						}
					}.execute(0);
				}
			}
		};
	}

	@Override
	protected boolean isLocationDisplayed() {
		return mLocationOverlay.isMyLocationEnabled();
	}

	@Override
	protected void onResume() {
		if (mBMapMan != null) {
			// 注册定位事件，定位后将地图移动到定位点
			mBMapMan.getLocationManager().requestLocationUpdates(
					mLocationListener);

			mLocationOverlay.enableMyLocation();
			mLocationOverlay.enableCompass(); // 打开指南针
			mBMapMan.start(); // 开启百度地图API

			// Toast.makeText(MapDemo.this, "onResume",
			// Toast.LENGTH_SHORT).show();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 在activity destroy前mBMapMan.destroy()
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mBMapMan != null) {
			mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			mLocationOverlay.disableMyLocation();
			mLocationOverlay.disableCompass(); // 关闭指南针
			mBMapMan.stop(); // 终止百度地图API，调用此函数后，不会再发生回调
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_select, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void RGeocode(GeoPoint Geo) {
		MKSearch search = new MKSearch();
		search.init(LocationSelect.mBMapMan, new MyMKSearchListener());
		search.reverseGeocode(Geo);
	}

	class MyMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo res, int error) {
			if (error != 0) {
				return;
			}
			mMapView.getController().animateTo(res.geoPt);
			LocInfo.setText(res.strAddr);
			LocBubble.setText(res.strAddr);
			LocFrame.setVisibility(View.GONE);
			LocBubble.setVisibility(View.VISIBLE);
			LocTitle.setText(res.addressComponents.city + ","
					+ res.addressComponents.district);
			// res.addressComponents.province 省份
			// res.addressComponents.district 区
			// res.addressComponents.city 城市
			// res.addressComponents.street 街道
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {

		}

		@Override
		public void onGetRGCShareUrlResult(String arg0, int arg1) {

		}

	}

	public boolean onTouch(View v, MotionEvent event)

	{
		Log.d("touch", "event");
		if (v == mMapView) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				LocBubble.setVisibility(View.GONE);
				istouch = true;
				break;
			case MotionEvent.ACTION_UP:
				// LocBubble.setVisibility(View.VISIBLE);
				if (istouch) {
					new AsyncTask<Integer, Integer, String[]>() {
						protected void onPreExecute() {
							LocFrame.setVisibility(View.VISIBLE);
							LocTitle.setText("正在定位...");
							super.onPreExecute();
						}

						@Override
						protected void onCancelled() {
							super.onCancelled();
						}

						protected String[] doInBackground(Integer... params) {
							// TODO
							pt = mMapView.getMapCenter();
							RGeocode(pt);
							return null;
						}

						protected void onPostExecute(String[] result) {
							super.onPostExecute(result);
						}
					}.execute(0);
				}
				istouch = false;
				break;
			default:
				break;
			}
		}
		return false;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn:
			im.setVisibility(View.VISIBLE);
			mMapView.getController().animateTo(MyLoc);
			mMapView.getController().setZoom(17); // 设置地图zoom级别
			RGeocode(MyLoc);
			break;
		case R.id.poi_btn:
			if (pt == null) {
				Toast.makeText(LocationSelect.this, "正在定位中……",
						Toast.LENGTH_SHORT).show();
			} else {
				im.setVisibility(View.VISIBLE);
				RGeocode(pt);

				name = new ArrayList<String>();
				addr = new ArrayList<String>();
				urlstr1 = "http://api.map.baidu.com/place/v2/search?&query="
						+ "公司"
						+ "&location="
						+ pt.getLatitudeE6()
						/ 1e6
						+ ","
						+ pt.getLongitudeE6()
						/ 1e6
						+ "&radius=2000&output=xml&ak=3a9f7b7dd90c5820cb3f229cb3ba18c9";
				urlstr2 = "http://api.map.baidu.com/place/v2/search?&query="
						+ "药店"
						+ "&location="
						+ pt.getLatitudeE6()
						/ 1e6
						+ ","
						+ pt.getLongitudeE6()
						/ 1e6
						+ "&radius=2000&output=xml&ak=3a9f7b7dd90c5820cb3f229cb3ba18c9";
				urlstr3 = "http://api.map.baidu.com/place/v2/search?&query="
						+ "道路"
						+ "&location="
						+ pt.getLatitudeE6()
						/ 1e6
						+ ","
						+ pt.getLongitudeE6()
						/ 1e6
						+ "&radius=2000&output=xml&ak=3a9f7b7dd90c5820cb3f229cb3ba18c9";
				Log.d("url1", urlstr1);
				Log.d("url2", urlstr2);
				Log.d("url3", urlstr3);
				getXML(urlstr1);
				getXML(urlstr2);
				getXML(urlstr3);
			}
			break;
		default:
			break;
		}

	}

	private void getXML(String url) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
			}

			@Override
			public void onFinish() {
				InputStream inStream = new ByteArrayInputStream(resultstr
						.getBytes());

				try {
					List<xml_result> xml_results = SaxResultService
							.readXml(inStream);
					StringBuilder sb = new StringBuilder();
					for (xml_result xml_result : xml_results) {
						sb.append(xml_result.toString()).append("\n");
						name.add(xml_result.getName());
						addr.add(xml_result.getAddress());
					}
					resultstr = sb.toString();
					Log.d("resultstr", resultstr);
					adapter = new MyAdapter(LocationSelect.this);
					LocationList.setAdapter(adapter);
				} catch (Exception e) {
				}
			}

			@Override
			public void onSuccess(String content) {
				Log.d("result", content);
				resultstr = content;
			}

			@Override
			public void onFailure(Throwable error) {
			}
		});

	}

	public final class ViewHolder {
		public TextView Company;
	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return name.size();
		}

		@Override
		public Object getItem(int position) {
			return name.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.locationlist_item, null);
				holder = new ViewHolder();
				holder.Company = (TextView) convertView
						.findViewById(R.id.locationText);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.Company.setText(name.get(position));
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		LocInfo.setText(name.get(arg2) + "\n" + "" + "地址:" + addr.get(arg2)
				+ "");
	}

	public class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
			super(activity, mapView);
			mMKSearch = search;
		}

		@Override
		protected boolean onTap(int i) {
			// super.onTap(i);
			MKPoiInfo info = getPoi(i);

			if (info.hasCaterDetails == true) {
				mMKSearch.poiDetailSearch(info.uid);
			}

			Toast.makeText(LocationSelect.this, info.name, Toast.LENGTH_SHORT)
					.show();
			return true;
		}

	}

}
