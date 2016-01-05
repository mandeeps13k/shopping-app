package com.rgretail.grocermax;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.HitBuilders;
import com.rgretail.grocermax.GCM.GCMClientManager;
import com.rgretail.grocermax.api.ConnectionService;
import com.rgretail.grocermax.api.MyReceiverActions;
import com.rgretail.grocermax.bean.LocationListBean;
import com.rgretail.grocermax.hotoffers.HomeScreen;
import com.rgretail.grocermax.preference.MySharedPrefs;
import com.rgretail.grocermax.utils.AppConstants;
import com.rgretail.grocermax.utils.Constants;
import com.rgretail.grocermax.utils.UrlsConstants;
import com.rgretail.grocermax.utils.UtilityMethods;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//import com.google.analytics.tracking.android.EasyTracker;

//import com.google.analytics.tracking.android.EasyTracker;

//https://developers.google.com/analytics/devguides/collection/android/v4/        //ga v4 eg
public class SplashScreen extends BaseActivity 
{
	private Handler handler;
	private Spinner spinner;

	private TextView txvTitle;
	private TextView txvMessage;
//	EasyTracker tracker;
	private Button btnGoShoping;


	private GCMClientManager pushClientManager;
	private String DeviceRegistrationId;
	private String SCREENNAME = "SplashScreen-";


	public int pxToDp(int px) {
		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);




        /*registering device on GCM server*/
        System.out.println("ishahhh=="+MySharedPrefs.INSTANCE.getGCMDeviceTocken());
        if(MySharedPrefs.INSTANCE.getGCMDeviceTocken()==null)
        registerGCM();

		addActionsInFilter(MyReceiverActions.LOCATION);


		MyApplication application = (MyApplication) getApplication();
		mTracker = application.getDefaultTracker();
//		Log.i(TAG, "Setting screen name: " + "SplashScreen");



//Your Dev Key: XNjhQZD7Yhe2dFs8kL7bpn  -  appsflyer dev key
				//////////// AppsFlyer code //////////
				//		6.1 �Set�Currency�Code
				AppsFlyerLib.setCurrencyCode("INR");
//		4
		AppsFlyerLib.setAppsFlyerKey("XNjhQZD7Yhe2dFs8kL7bpn");     //SDK�Initialization�and�Installation�Event (Minimum� Requirement�for�Tracking)�
		AppsFlyerLib.sendTracking(getApplicationContext());        //SDK�Initialization�and�Installation�Event (Minimum� Requirement�for�Tracking)�
//		5 /Example�2:�?Purchase�Event/
//		Map<String, Object> eventValue = new HashMap<String, Object>();
//		eventValue.put(AFInAppEventParameterName.PRICE,200);
////		eventValue.put(AFInAppEventParameterName.REVENUE,200);
//		eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,"category_a");
//		eventValue.put(AFInAppEventParameterName.CONTENT_ID,"1234567");
//		eventValue.put(AFInAppEventParameterName.CURRENCY, "INR");
//		AppsFlyerLib.trackEvent(getApplicationContext(), AFInAppEventType.ADD_TO_CART, eventValue);

//		6.2 �Get�AppsFlyer�Unique�ID�(Optional)�
//		String appsFlyerId = AppsFlyerLib.getAppsFlyerUID(this);         //�unique�ID�is�created�for�every�new�install�of�an�app
//      6.3 Set�Customer�User�ID
//		AppsFlyerLib.setCustomerUserId("myId");

//      6.6 Reporting�Deeplinks�for�Re�Targeting�Attribution�(Optional)�
//		AppsFlyerLib.setAppsFlyerKey("XNjhQZD7Yhe2dFs8kL7bpn");        //place in onCreate   //6.6   from report�launches�initiated�through�deeplinks
//		Intent intent = ((Activity)this).getIntent();
//		String action = intent.getAction();
//		if (action == Intent.ACTION_VIEW) {
//			AppsFlyerLib.sendTracking(this);
//		}                                                              //place in onCreate   //6.6  to report�launches�initiated�through�deeplinks

			//////////// AppsFlyer code //////////

		try{
			//UtilityMethods.clickCapture(this,"","","","",SCREENNAME+AppConstants.SPLASH_SCREEN);
		}catch(Exception e){}

//		String sd = String.valueOf(pxToDp(420));
//		System.out.println("======pxdp=====" + sd);
		
		try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.rgretail.grocermax",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
				String str = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
		
		addActionsInFilter(MyReceiverActions.CATEGORY_LIST);

		spinner = (Spinner) findViewById(R.id.spinner1);

		txvTitle = (TextView) findViewById(R.id.txvTitle);
		txvMessage = (TextView) findViewById(R.id.txvMessage);
		btnGoShoping = (Button) findViewById(R.id.btnGoShoping);

		if (MySharedPrefs.INSTANCE.getUserCity() != null) {
			spinner.setVisibility(View.INVISIBLE);
			btnGoShoping.setVisibility(View.VISIBLE);

			handler = new Handler();
			handler.postDelayed(runningThread, 2000);
		}

		String strTitle = "<html><br><br><font color=\"red\">* Coming soon to Delhi NCR</font></html>"; // "<html><font color=\"black\">Choose your Location.</font><font color=\"red\">*</font></html>";
		Spanned spanned = Html.fromHtml(strTitle, null, null);
		txvTitle.setText(spanned);

		txvMessage
				.setText(Html
						.fromHtml("<html><font color=\"black\">Easy Shoping, Great Value, Friendly Service<br><br>Currently delivering in Gurgaon.</font></html>"));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.location_spinner_item, AppConstants.placesList);
		spinner.setAdapter(dataAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0) {
					MySharedPrefs.INSTANCE.putUserCity(AppConstants.placesList
							.get(position));
					handler = new Handler();
					handler.postDelayed(runningThread, 500);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		btnGoShoping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MySharedPrefs.INSTANCE.putUserCity(AppConstants.placesList.get(1));
				handler = new Handler();
				handler.postDelayed(runningThread, 500);

			}
		});
	}

	Runnable runningThread4Minutes = new Runnable() {
		public void run() {
			Intent call = new Intent(SplashScreen.this, HomeScreen.class);
			startActivity(call);
			/*comment by ishan*/
            //registerGCM();
            /*-----*/
			finish();
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		try{
			AppsFlyerLib.onActivityPause(this);
		}catch(Exception e){}
	}

	@Override
	public void onResume() {
		super.onResume();

		mTracker.setScreenName(SCREENNAME);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//		mTracker.send(new HitBuilders.EventBuilder()
//				.setCategory("Actionssss")
//				.setAction("Sharessss")
//				.setLabel("labelssss")
////				.set(UtilityMethods.GA_EVENTNAME, "event passed")
////				.setAll(params)
//				.build());

		try{
			AppsFlyerLib.onActivityResume(this);
		}catch(Exception e){}

			try {
					if (!UtilityMethods.isInternetAvailable(activity)) {                 //exit app after 4 sec
						UtilityMethods.customToast(AppConstants.ToastConstant.msgNoInternet, activity);
						handler = new Handler();
						handler.postDelayed(runningThread, 4000);
						return;
					}

					if (MySharedPrefs.INSTANCE.getSelectedCity() != null) {
//			showDialog();
						//String url = UrlsConstants.CATEGORY_COLLECTION_LISTING_URL;
						if (UtilityMethods.isInternetAvailable(activity)) {                //start app after 4 sec
//				myApi.reqCategorySubCategoryList(url);
							handler = new Handler();
							handler.postDelayed(runningThread4Minutes, 4000);
						} else {
							UtilityMethods.customToast(AppConstants.ToastConstant.msgNoInternet, activity);
							handler = new Handler();
							handler.postDelayed(runningThread, 4000);
						}
					} else {
                        String url = UrlsConstants.GET_LOCATION;
						if (UtilityMethods.isInternetAvailable(activity)) {             //call location api
							myApi.reqLocation(url);
						} else {
							UtilityMethods.customToast(AppConstants.ToastConstant.msgNoInternet, activity);
							handler = new Handler();
							handler.postDelayed(runningThread, 4000);
						}
					}
			}catch(Exception e){
                Log.d("getting reg", e.getMessage());
            }

//		if(MySharedPrefs.INSTANCE.getSelectedCity() != null){
//			Intent call = new Intent(SplashScreen.this, HomeScreen.class);
////			Bundle call_bundle = new Bundle();
////			call_bundle.putSerializable("Categories", category);
////			call.putExtras(call_bundle);
//			startActivity(call);
//			registerGCM();
//			finish();
//		}else{
//			String url = UrlsConstants.GET_LOCATION;
//			if(UtilityMethods.isInternetAvailable(activity)) {
//				myApi.reqLocation(url);
//			}else{
//				UtilityMethods.customToast(AppConstants.ToastConstant.msgNoInternet, activity);
//				handler = new Handler();
//				handler.postDelayed(runningThread, 4000);
//			}
//		}

	}

	public void onBackPressed() {

		super.onBackPressed();
		if (handler != null)
			handler.removeCallbacks(runningThread);
	};

	Runnable runningThread = new Runnable() {
		public void run() {
			finish();
			/*Intent intent;
			intent = new Intent(SplashScreen.this, HomeScreen.class);
			startActivity(intent);
			finish();*/
//			registerGCM();
		}
	};

	@Override
	public void OnResponse(Bundle bundle) {
		String action = bundle.getString("ACTION");
		if (action.equals(MyReceiverActions.LOCATION)) {
//			LocationListBean locationBean = (LocationListBean) bundle.getSerializable(ConnectionService.RESPONSE);
			AppConstants.locationBean = (LocationListBean) bundle.getSerializable(ConnectionService.RESPONSE);
			if(AppConstants.locationBean.getFlag().equals("1")) {
                MyApplication.isFromDrawer=false;
				Intent call = new Intent(SplashScreen.this, CityActivity.class);
				Bundle call_bundle = new Bundle();
//				call_bundle.putSerializable("Location", locationBean);
				call_bundle.putSerializable("Location", AppConstants.locationBean);
				call_bundle.putSerializable("FromDrawer", "");
				call.putExtras(call_bundle);
				startActivity(call);
				/*comment by ishan*/
                //registerGCM();
                /*-----*/
				finish();
			}else{
				/*comment by ishan*/
                //registerGCM();
               /*-----*/
				UtilityMethods.customToast(AppConstants.ToastConstant.DATA_NOT_FOUND, mContext);
			}

		}else{                   //category
			/*dismissDialog();
			String jsonResponse = (String) bundle.getSerializable(ConnectionService.RESPONSE);
			//UtilityMethods.write("response",jsonResponse,SplashScreen.this);
			ArrayList<CategorySubcategoryBean> category = UtilityMethods.getCategorySubCategory(jsonResponse);
			if (!jsonResponse.trim().equals("") && category.size() > 0) {
				UtilityMethods.writeCategoryResponse(SplashScreen.this, AppConstants.categoriesFile, jsonResponse);
				Intent call = new Intent(SplashScreen.this, HomeScreen.class);
				Bundle call_bundle = new Bundle();
				call_bundle.putSerializable("Categories", category);
				call.putExtras(call_bundle);
				startActivity(call);
                *//*comment by ishan*//*
				//registerGCM();
                *//*-----*//*
				finish();
			} else {
				*//*comment by ishan*//*
                //registerGCM();
                *//*-----*//*
				UtilityMethods.customToast(AppConstants.ToastConstant.DATA_NOT_FOUND, mContext);
			}*/
		}

//		String jsonResponse = (String) bundle.getSerializable(ConnectionService.RESPONSE);
//		//UtilityMethods.write("response",jsonResponse,SplashScreen.this);
//		ArrayList<CategorySubcategoryBean> category = UtilityMethods.getCategorySubCategory(jsonResponse);
//		if (!jsonResponse.trim().equals("") && category.size() > 0) {
//			UtilityMethods.writeCategoryResponse(SplashScreen.this, AppConstants.categoriesFile, jsonResponse);
//			Intent call = new Intent(SplashScreen.this, HomeScreen.class);
//			Bundle call_bundle = new Bundle();
//			call_bundle.putSerializable("Categories", category);
//			call.putExtras(call_bundle);
//			startActivity(call);
//			finish();
//		} else {
//			UtilityMethods.customToast(ToastConstant.DATA_NOT_FOUND, mContext);
//		}

	}

	@Override
	public void onStart() {
		super.onStart();
		AppsFlyerLib.onActivityResume(this);
	}


	
//	@Override
//    protected void onStart() {
//    	// TODO Auto-generated method stub
//    	super.onStart();
//    	try{
//			EasyTracker.getInstance(this).activityStart(this);
////	    	tracker.activityStart(this);
//	    	FlurryAgent.onStartSession(this,getResources().getString(R.string.flurry_api_key));
//	    	FlurryAgent.onPageView();         //Use onPageView to report page view count.
//    	}catch(Exception e){}
//    }
    
//    @Override
//    protected void onStop() {
//    	// TODO Auto-generated method stub
//    	super.onStop();
//    	try{
//	    	tracker.activityStop(this);
//	    	FlurryAgent.onEndSession(this);
//    	}catch(Exception e){}
//    }

	@Override
	public void onStop() {
		super.onStop();
		AppsFlyerLib.onActivityPause(this);
	}

	public void registerGCM() {
		pushClientManager = new GCMClientManager(this, Constants.GCM_SENDER_KEY);
		pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
			@Override
			public void onSuccess(String registrationId,
								  boolean isNewRegistration) {
				DeviceRegistrationId = registrationId;
//				new PreferenceHelper(HoverSplashActivity.this)
//						.putDeviceToken(registrationId);,
//				Toast.makeText(SplashScreen.this,"RegistrationScreen"+DeviceRegistrationId,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFailure(String ex) {
				super.onFailure(ex);
				//Show Toast here.
			}
		});

	}





//	class PlayStoreInfn extends AsyncTask<String, String, String> {
//		Context context;
//		String strResponse;
//		String strCheck = "test";
//		boolean bHadResponse = false;
//
//		public PlayStoreInfn(Context mContext) {
//			try {
//				context = mContext;
//			} catch (Exception e) {
//			}
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			try {
//				MarketSession session = new MarketSession();
//				String androidId= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//				session.getContext().setAndroidId(androidId);
//
//
////				String query = "maps";
//				String query = "com.rgretail.grocermax";
//				Market.AppsRequest appsRequest = Market.AppsRequest.newBuilder()
//						.setQuery(query)
//						.setStartIndex(0).setEntriesCount(1)
//						.setWithExtendedInfo(true)
//						.build();
//
//				session.append(appsRequest, new MarketSession.Callback<Market.AppsResponse>() {
//					@Override
//					public void onResult(Market.ResponseContext context, Market.AppsResponse response) {
//						// Your code here
//						// response.getApp(0).getCreator() ...
//						// see AppsResponse class definition for more infos
//						Log.i(context.toString(),response.toString());
//						strResponse = String.valueOf(response);
//						strCheck = response.getApp(0).getRating();
//					}
//				});
//				session.flush();
//
//
////				MarketSession session = new MarketSession();
//////				session.login(email, password);
////				String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
////				androidId = "dead00beef";
////				session.getContext().setAndroidId(androidId);
////
////				String query = "maps";
////				Market.AppsRequest appsRequest = Market.AppsRequest.newBuilder()
////						.setQuery(query)
////						.setStartIndex(0).setEntriesCount(10)
////						.setWithExtendedInfo(true)
////						.build();
////
////				session.append(appsRequest, new MarketSession.Callback<Market.AppsResponse>() {
////					@Override
////					public void onResult(Market.ResponseContext context, Market.AppsResponse response) {
////						bHadResponse = true;
////						Toast.makeText(SplashScreen.this, "response====" + response, Toast.LENGTH_LONG).show();
////						strCheck = response.getApp(0).getRating();
////						strResponse = String.valueOf(response);
////						// Your code here
////						// response.getApp(0).getCreator() ...
////						// see AppsResponse class definition for more infos
////					}
////				});
////
////				if(bHadResponse) {
////					session.flush();
////				}
//
//
////				MarketSession session = new MarketSession();
////
////				Market.CommentsRequest commentsRequest = Market.CommentsRequest.newBuilder()
////						.setAppId("7065399193137006744")
////						.setStartIndex(0)
////						.setEntriesCount(10)
////						.build();
////
////				session.append(commentsRequest, new MarketSession.Callback<Market.CommentsResponse>() {
////					@Override
////					public void onResult(Market.ResponseContext context, Market.CommentsResponse response) {
////						System.out.println("Response : " + response);
////						int rate = response.getComments(0).getRating();
////						// response.getComments(0).getAuthorName()
////						// response.getComments(0).getCreationTime()
////						// ...
////					}
////				});
//
////				session.flush();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			return strResponse;
//		}
//
//		@Override
//		protected void onPostExecute(String str) {
//			super.onPostExecute(str);
//			Toast.makeText(SplashScreen.this, strCheck+"response onpostexecute====" + str, Toast.LENGTH_LONG).show();
//		}
//	}

	}
