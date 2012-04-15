package kr.dkay.qrcoderace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        
        
        HttpPostData();
        ImageView.OnClickListener adapter = new ImageView.OnClickListener() {
        	@Override
        	public void onClick(View v){
        		HttpPostData();
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
    
    void HttpPostData() {
    	
    	if(!getMyLocation()){
    		Toast.makeText(this, "No GPS Information", 0).show();
    		return;
    	}
        try {
             //--------------------------
             //   URL �����ϰ� �����ϱ�
             //--------------------------
             URL url = new URL("http://drama.kaist.ac.kr/qr/getLocation.php");       // URL ����
             HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ����
             //--------------------------
             //   ���� ��� ���� - �⺻���� �����̴�
             //--------------------------
             http.setDefaultUseCaches(false);                                           
             http.setDoInput(true);                         // �������� �б� ��� ����
             http.setDoOutput(true);                       // ������ ���� ��� ���� 
             http.setRequestMethod("POST");         // ���� ����� POST

             // �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش�
             http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
             //--------------------------
             //   ������ �� ����
             //--------------------------
             StringBuffer buffer = new StringBuffer();
             buffer.append("lat").append("=").append(myLat).append("&");
             buffer.append("long").append("=").append(myLng).append("&");
             buffer.append("team").append("=").append(myTeam);
             
             OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
             PrintWriter writer = new PrintWriter(outStream);
             writer.write(buffer.toString());
             writer.flush();
             //--------------------------
             //   �������� ���۹ޱ�
             //--------------------------
             InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR"); 
             BufferedReader reader = new BufferedReader(tmp);
             StringBuilder builder = new StringBuilder();
             String str;
             while ((str = reader.readLine()) != null) {       // �������� ���δ����� ������ ���̹Ƿ� ���δ����� �д´�
                  builder.append(str + "\n");                     // View�� ǥ���ϱ� ���� ���� ������ �߰�
             }
             
             String myResult = builder.toString();                       // ���۰���� ���� ������ ����
             Toast.makeText(this, myResult, 0).show();
        } catch (MalformedURLException e) {
               //
        } catch (IOException e) {
               // 
        } // try
   } // HttpPostData
    
    
    private boolean getMyLocation(){     
    	LocationManager myLocationManager;
    	LocationListener myLocationListener;
    	//GPS�� �����ִ��� Ȯ���Ѵ�.
    	myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	Location loc = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(loc == null)
    	{
    		Log.i("Worldmap", "GPS OFF");
    		AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
    		gsDialog.setTitle("GPS Status OFF");
    		gsDialog.setMessage("Go to Change GPS Setting!");
    		gsDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				// GPS���� ȭ������ Ƣ���
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

    	myLocationListener = new LocationListener() {
    		@Override
    			public void onStatusChanged(String provider, int status, Bundle extras) {
    			}
    		@Override
    			public void onProviderEnabled(String provider) {
    			}
    		@Override
    			public void onProviderDisabled(String provider) {
    			}
    		//��ġ ������ ���� ���� �� ��ġ ������ �����´�.
    		@Override
    			public void onLocationChanged(Location location) {
    				Log.i("HONEYMAP","Lat:"+location.getLatitude()+"Lng:"+location.getLongitude());
    				myLat = location.getLatitude()*1E6;
    				myLng = location.getLongitude()*1E6;
    			}
    	};

    	//���ǿ����� ���� ��ġ ������ �� ��� ������Ʈ �ϵ��� ����
    	myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, myLocationListener);

    	//���� �ֱ� ��ġ�� �����Ѵ�.
    	myLat = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()*1E6;
    	myLng = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()*1E6;

    	Log.i("HONEYMAP","LAT:"+myLat.intValue()+
    			"Lng:"+myLng.intValue());

    	return true;
    }//get my location


}


