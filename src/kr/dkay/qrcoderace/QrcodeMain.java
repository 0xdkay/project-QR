package kr.dkay.qrcoderace;

import java.util.ArrayList;

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
	private ArrayList<String> teamList;
 
	private TabHost mTabHost;
	
	private Double myLat;
	private Double myLng;
	private String myTeam = "";
	private Context cont = this;
	
	private String deviceID;
	private String version;

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
        String settingList[] = res.getStringArray(R.array.test);        
        setting.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                settingList));
        
        getTeamList();
        
        setTeam();
        
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
    
    void getTeamList(){
		String result = "";
		try{
			result = new AccessToServer().execute("teamlist", myLat.toString(), myLng.toString(), myTeam, deviceID, version).get();
		}catch(Exception e){
			e.printStackTrace();
		}

		AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
		gsDialog.setTitle("No Team Name");
		gsDialog.setMessage(result);
		gsDialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(cont, SettingActivity.class);
				intent.putStringArrayListExtra("teamlist", teamList);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(intent);			
			}
		});
		gsDialog.create().show();
		
		
		teamList = parseRes("teamlist", result);
    }
    
    
    void setTeam(){
    	if(myTeam==""){
    		Log.i("teamName", "No Team Name");
    		AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
    		gsDialog.setTitle("No Team Name");
    		gsDialog.setMessage("Choose your team!");
    		gsDialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				Intent intent = new Intent(cont, SettingActivity.class);
    				intent.putStringArrayListExtra("teamlist", teamList);
    				intent.addCategory(Intent.CATEGORY_DEFAULT);
    				startActivity(intent);			
    			}
    		});
    		gsDialog.create().show();
    	}
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
    				String result2 = "";
    				try{
    					result = new AccessToServer().execute("solvedlist", myLat.toString(), myLng.toString(), myTeam, deviceID, version).get();
    					result2 = new AccessToServer().execute("remainedlist", myLat.toString(), myLng.toString(), myTeam, deviceID, version).get();
    				}catch(Exception e){
    					e.printStackTrace();
    				}

    				solvedList = parseRes("solvedlist", result);
    				remainList = parseRes("remainedlist", result2);
    				
    				remain.setAdapter(new ArrayAdapter <String> (cont,
    						R.layout.listview_layout_probs,
    			            remainList));
    			    solved.setAdapter(new ArrayAdapter <String> (cont,
    			            R.layout.listview_layout_probs,
    			            solvedList));
    			}
    	};

    	lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,lListener);
    	lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lListener);
    	
    	//가장 최근 위치를 저장한다.
    	myLat = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
    	myLng = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();

    	return true;
    }//getMyLocation
   
    
    ArrayList<String> parseRes(String mode, String result){
    	ArrayList<String> tmp = new ArrayList<String>();
    	String tmp2[] = result.split(",");
    	for(int i=1; i<tmp2.length; i++){
    		tmp.add(tmp2[i]);
    	}
        return tmp;
    }
}


