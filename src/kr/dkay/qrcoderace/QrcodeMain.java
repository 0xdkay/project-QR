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
import android.widget.TabHost.TabSpec;

public class QrcodeMain extends TabActivity {
	/** Called when the activity is first created. */
	
	private TextView about;
	private ListView remain;
	private ListView solved;
	private ListView setting;
	
	private boolean isTwoClickBack = false;
	private ArrayList<String> remainList;
	private ArrayList<String> solvedList;
	private ArrayList<String> teamList = null;
 
	private TabHost mTabHost;
	
	private Double myLat = 0.0;
	private Double myLng = 0.0;
	private String myTeam = "";
	private Context cont;
	
	private String deviceID;
	private String version;

	LocationManager lManager;
	LocationListener lListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources();
        cont = this;
        
        
        //setting tabs
        mTabHost = getTabHost();        
        
        TabSpec tabSpec = mTabHost.newTabSpec("tab");
        tabSpec.setIndicator("worldmap");
        Context ctx = this.getApplicationContext();
        Intent i = new Intent(ctx, TmpMapActivity.class);
        tabSpec.setContent(i);
        mTabHost.addTab(tabSpec);

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
        
        
        myLat = 36.368023;
        myLng = 127.365446;
        
        about = (TextView)findViewById(R.id.about);
        solved = (ListView)findViewById(R.id.solved);
        remain = (ListView)findViewById(R.id.remain);
        setting = (ListView)findViewById(R.id.setting);
        
        //setting contents in the tabs
        String settingList[] = res.getStringArray(R.array.test);        
        setting.setAdapter(new ArrayAdapter <String> (this,
                android.R.layout.simple_list_item_1,
                settingList));
        
        
        WifiManager wManage = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wManage.getConnectionInfo();
        deviceID = wInfo.getMacAddress();
    	version = Build.ID;
        teamList = getListFromServer("teamlist");
        if(myTeam==""){
       		setTeam();
    	}        
        
        getMyLocation();
        checkUpdate.start();
        
        /*
        ImageView.OnClickListener adapter = new ImageView.OnClickListener() {
        	@Override
        	public void onClick(View v){
        		Toast.makeText(cont, myTeam, Toast.LENGTH_SHORT).show();
        	}
        };
        worldMap.setOnClickListener(adapter);
        */

    }
    
    private Thread checkUpdate = new Thread() {
        public void run() {
        	try{
        		while(true){
        			lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,lListener);
        			lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lListener);
        			Thread.sleep(5000);
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
    };
    
    ArrayList<String> getListFromServer(String a){
		String result = "";
		try{
			result = new AccessToServer().execute(a, myLat.toString(), myLng.toString(), myTeam, deviceID, version).get();
		}catch(Exception e){
			e.printStackTrace();
		}
		return parseRes(a, result);
    }
    
    ArrayList<String> parseRes(String mode, String result){
    	ArrayList<String> tmp = new ArrayList<String>();
    	String tmp2[] = result.split(",");
    	for(int i=1; i<tmp2.length-1; i++){
    		tmp.add(tmp2[i]);
    	}
        return tmp;
    }
    
    void setTeam(){
   		AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
   		gsDialog.setTitle("No Team Name");
   		gsDialog.setMessage("Choose your team.");
   		gsDialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {
   			public void onClick(DialogInterface dialog, int which) {
   				Intent intent = new Intent(cont, SettingActivity.class);
   				intent.putStringArrayListExtra("teamlist", teamList);
   				intent.addCategory(Intent.CATEGORY_DEFAULT);
   				startActivityForResult(intent, 1);			
   			}
   		});
   		gsDialog.create().show();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode==RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
    	{
    		if(requestCode==1) // InformationInput에서 호출한 경우에만 처리합니다.
    		{               
    			myTeam = data.getStringExtra("team_name");
    		}
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
        	isTwoClickBack = false;
        }
        @Override
        public void onTick(long millisUntilFinished) {
        	Log.i("Test"," isTwoClickBack " + isTwoClickBack);
        }
    }//class CntTimer
   
    
    private boolean getMyLocation(){
    	//GPS가 켜져있는지 확인한다.
    	lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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
    				myLat = location.getLatitude();
    				myLng = location.getLongitude();

    				teamList = getListFromServer("teamlist");
    				solvedList = getListFromServer("solvedlist");
    				remainList = getListFromServer("remainedlist");
    				
    				remain.setAdapter(new ArrayAdapter <String> (cont,
    						android.R.layout.simple_list_item_1,
    			            remainList));
    			    solved.setAdapter(new ArrayAdapter <String> (cont,
    			            android.R.layout.simple_list_item_1,
    			            solvedList));
    			}
    	};

    	lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, lListener);
    	lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, lListener);
    	
    	
    	if(lManager == null || lListener == null || !LocationManager.GPS_PROVIDER.equals("gps"))
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
    	
    	return true;
    }//getMyLocation
}


