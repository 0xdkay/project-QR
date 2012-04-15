package kr.dkay.qrcoderace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;


import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class QrcodeMain extends TabActivity {
	/** Called when the activity is first created. */
	
	private ImageView worldMap;
	private TextView about;
	private ListView remain;
	private ListView solved;
	private ListView setting;
	
	private boolean isTwoClickBack = false;
	private ArrayList<String> remainList;
	private ArrayList<String> solvedList;
 
	private TabHost mTabHost;
	
	private Double myLat;
	private Double myLng;
	private String myTeam = "test";
	private Context cont = this;
	
	private String message;
	private String deviceID;
	private String version;
	private String infotype;

	LocationManager lManager;
	LocationListener lListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources();

        
        //setting tabs
        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec("tab") 
        		.setIndicator("worldmap") 
        		.setContent(R.id.worldmap)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab") 
        		.setIndicator("about") 
        		.setContent(R.id.about)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab") 
        		.setIndicator("remain") 
                .setContent(R.id.remain)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab") 
                .setIndicator("solved") 
                .setContent(R.id.solved)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab") 
                .setIndicator("setting") 
                .setContent(R.id.setting)); 
        mTabHost.setCurrentTab(0);
        
        //setting variables related to each tab
        worldMap = (ImageView)findViewById(R.id.worldmap);
        about = (TextView)findViewById(R.id.about);
        solved = (ListView)findViewById(R.id.solved);
        remain = (ListView)findViewById(R.id.remain);
        setting = (ListView)findViewById(R.id.setting);
        
        
        
        //setting contents in the tabs
        remainList = new ArrayList<String>();
        solvedList = new ArrayList<String>();
        
        remain.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                remainList));
        solved.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                solvedList));
        
        
        String settingList[] = res.getStringArray(R.array.test);        
        setting.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                settingList));
        
        getMyLocation();
        
        
        ImageView.OnClickListener adapter = new ImageView.OnClickListener() {
        	@Override
        	public void onClick(View v){
        		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,lListener);
            	lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lListener);
        	}
        };
        worldMap.setOnClickListener(adapter);
    
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(event.getAction() == KeyEvent.ACTION_DOWN){
    		if(keyCode == KeyEvent.KEYCODE_BACK){
    			if(!isTwoClickBack){
    				if(mTabHost.getCurrentTab()!=0){
    					mTabHost.setCurrentTab(0);
    				}else{
    					Toast a = Toast.makeText(this, "Click 'close' button once more to exit." , Toast.LENGTH_SHORT);
    					a.setGravity(Gravity.CENTER, 0, 0);
    					a.show();
    					CntTimer timer = new CntTimer(2000, 1);
    					timer.start();
    				}
    			}else{
    				finish();
    				return true;
    			}
    		}
    	}
    	return false;
    }//onCreate
    
    class CntTimer extends CountDownTimer{
        public CntTimer(long millisInFuture, long countDownInterval) {
        	super(millisInFuture, countDownInterval);
        	isTwoClickBack = true;
        }
        @Override
        public void onFinish() {
        	// TODO Auto-generated method stub
        	isTwoClickBack = false;
        }
        @Override
        public void onTick(long millisUntilFinished) {
        	// TODO Auto-generated method stub
        	Log.i("Test"," isTwoClickBack " + isTwoClickBack);
        }
    }//class CntTimer
   
    
    
    
    private boolean getMyLocation(){
    	WifiManager wManage = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wManage.getConnectionInfo();
        deviceID = wInfo.getMacAddress();
    	version = Build.ID;
    	
    	//GPS가 켜져있는지 확인한다.
    	lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    	Location loc = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
    	
    	if(loc == null)
    	{
    		Log.i("Worldmap", "GPS OFF");
    		AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
    		gsDialog.setTitle("GPS Status OFF");
    		gsDialog.setMessage("Go to Change GPS Setting!");
    		gsDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				// GPS설정 화면으로 튀어요
    				Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    				intent.addCategory(Intent.CATEGORY_DEFAULT);
    				startActivity(intent);
    			}
    		});
    		gsDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    			}
    		});
    		gsDialog.create().show();
    		return false;
    	}
    	
    	
    	lListener = new LocationListener() {
    		@Override
    			public void onStatusChanged(String provider, int status, Bundle extras) {
    			}
    		@Override
    			public void onProviderEnabled(String provider) {
    			}
    		@Override
    			public void onProviderDisabled(String provider) {
    			}
    		//위치 정보가 변경 됐을 때 위치 정보를 가져온다.
    		@Override
    			public void onLocationChanged(Location location) {
    				Log.i("gps","Lat:"+location.getLatitude()+"Lng:"+location.getLongitude());
    				myLat = location.getLatitude();
    				myLng = location.getLongitude();
    				String result = "";
    				try{
    					result = new AccessToServer().execute(myLat.toString(), myLng.toString(), myTeam).get();
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    				Toast.makeText(cont, result, Toast.LENGTH_SHORT);
    			}
    	};

    	lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,lListener);
    	lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lListener);
    	
    	//가장 최근 위치를 저장한다.
    	myLat = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
    	myLng = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();

    	return true;
    }//getMyLocation
   
    private class AccessToServer extends AsyncTask<String, Void,String> {
    	String myLat; 
    	String myLng;
    	String myTeam;
        protected String doInBackground(String ... data) {
        	myLat = data[0];
        	myLng = data[1];
        	myTeam = data[2];
            return HttpPostData();
        }
        private String HttpPostData() {  	
        	String result = null;
        	try {
                HttpPost request = new HttpPost("http://drama.kaist.ac.kr/qr/getLocation.php");
                Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
                nameValue.add(new BasicNameValuePair("lat", myLat));
                nameValue.add(new BasicNameValuePair("long", myLng));
                nameValue.add(new BasicNameValuePair("team", myTeam));
                request.setEntity(makeEntity(nameValue));
                
                HttpClient client = new DefaultHttpClient();
                ResponseHandler<String> reshandler = new BasicResponseHandler();
                result = client.execute(request, reshandler);
                
            } catch (MalformedURLException e) {	
            	e.printStackTrace();
            } catch (IOException e) {
        		e.printStackTrace();
            } catch (Exception e){
            	e.printStackTrace();
            }//try
            return result;
        } // HttpPostData
        

        private HttpEntity makeEntity(Vector<NameValuePair> $nameValue){
        	HttpEntity result = null;
        	try{
        		result = new UrlEncodedFormEntity($nameValue,HTTP.UTF_8);
        	} catch (UnsupportedEncodingException e){
        		e.printStackTrace();
        	}
        	return result;
        }//makeEntity
    }
}


