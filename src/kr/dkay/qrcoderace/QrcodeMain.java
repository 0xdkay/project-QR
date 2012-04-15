package kr.dkay.qrcoderace;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

public class QrcodeMain extends Activity {
	/** Called when the activity is first created. */
	
	private Button bAbout;
	private Button bRemain;
	private Button bSolved;
	private Button bSetting;
	
	private ImageView worldMap;
	private TextView about;
	private ListView remain;
	private ListView solved;
	private ListView setting;
	
	
	private boolean isTwoClickBack = false;
	private char curVisible = 'w';
	private ArrayList<String> remainList;
	private ArrayList<String> solvedList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        remainList = new ArrayList<String>();
        solvedList = new ArrayList<String>();
        
        worldMap = (ImageView)findViewById(R.id.worldMap);
        about = (TextView)findViewById(R.id.about);
        solved = (ListView)findViewById(R.id.solved);
        remain = (ListView)findViewById(R.id.remain);
        setting = (ListView)findViewById(R.id.setting);
        
        bAbout = (Button)findViewById(R.id.aboutbutton);
        bRemain = (Button)findViewById(R.id.remainbutton);
        bSolved = (Button)findViewById(R.id.solvedbutton);
        bSetting = (Button)findViewById(R.id.settingbutton);
        
        
        remain.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                remainList));
        solved.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                solvedList));
        
        Resources res = getResources();
        String settingList[] = res.getStringArray(R.array.test);        
        setting.setAdapter(new ArrayAdapter <String> (this,
                R.layout.listview_layout_probs,
                settingList));

        Button.OnClickListener adapter = new Button.OnClickListener() {
        	public void onClick(View v){
        		switch(v.getId()){
        		case R.id.aboutbutton:
        			About();
        			break;
        		case R.id.remainbutton:
        			Remain();
        			break;
        		case R.id.solvedbutton:
        			Solved();
        			break;
        		case R.id.settingbutton:
        			Setting();
        			break;
        		}
        	}
        };
        
        bAbout.setOnClickListener(adapter);
        bRemain.setOnClickListener(adapter);
        bSolved.setOnClickListener(adapter);
        bSetting.setOnClickListener(adapter);
    }
/* 
	AlertDialog.Builder ad = new AlertDialog.Builder(this);
	ad.setTitle("Result");
	ad.setCancelable(true);
	ad.setPositiveButton("Close", null);
	ad.setMessage(curVisible);
	ad.show();
*/
    protected void About(){
    	if(curVisible=='a')
    		makeVisible('w');
    	else
    		makeVisible('a');
    }
    protected void Remain(){
    	if(curVisible=='r')
    		makeVisible('w');
    	else
    		makeVisible('r');
    }
    protected void Solved(){
    	if(curVisible=='s')
    		makeVisible('w');
    	else
    		makeVisible('s');
    }
    protected void Setting(){
    	if(curVisible=='e')
    		makeVisible('w');
    	else
    		makeVisible('e');
    }
    
    void makeVisible(char token){
    	if(curVisible == token) return;
    	switch(curVisible){
		case 'a':
			this.about.setVisibility(View.INVISIBLE);
			break;
		case 'r':
			this.remain.setVisibility(View.INVISIBLE);
			break;
		case 's':
			this.solved.setVisibility(View.INVISIBLE);
			break;
		case 'e':
			this.setting.setVisibility(View.INVISIBLE);
			break;
		case 'w':
			this.worldMap.setVisibility(View.INVISIBLE);
			break;
		}
    	switch(token){
    	case 'a':
    		this.about.setVisibility(View.VISIBLE);
    		break;
    	case 'r':
    		this.remain.setVisibility(View.VISIBLE);
    		break;
    	case 's':
    		this.solved.setVisibility(View.VISIBLE);
    		break;
    	case 'e':
    		this.setting.setVisibility(View.VISIBLE);
    		break;
    	case 'w':
    		this.worldMap.setVisibility(View.VISIBLE);
    	}
    	curVisible = token;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(event.getAction() == KeyEvent.ACTION_DOWN){
    		if(keyCode == KeyEvent.KEYCODE_BACK){
    			if(!isTwoClickBack){
    				if(curVisible!='w'){
    					makeVisible('w');		
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
    }
    
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
    }
}


