package kr.dkay.qrcoderace;
import java.util.ArrayList;

import kr.dkay.qrcoderace.R.id;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends ListActivity {
	private ListView teamlist;
	private String tname;
	private TextView teamName;
	private Button bSet;
	
	@Override   
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.team_name_setting);

		final Intent intent = getIntent(); // 이 액티비티를 시작하게 한 인텐트를 호출
	
		teamlist = (ListView)findViewById(android.R.id.list);
	    teamlist.setAdapter(new ArrayAdapter <String> (this,
	            android.R.layout.simple_list_item_1,
	            intent.getStringArrayListExtra("teamlist")));
	    
	    teamName = (TextView)findViewById(R.id.teamname);
	    bSet = (Button)findViewById(R.id.setbutton);
	    bSet.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v) {
	    		intent.putExtra("team_name", tname);
	    		setResult(RESULT_OK,intent); // 추가 정보를 넣은 후 다시 인텐트를 반환합니다.
	    		finish(); // 액티비티 종료
	    	}
	    });
	}

	@Override
	protected void onListItemClick (ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		tname = ((TextView)v).getText().toString();
	    teamName.setText("My Team: "+tname);
	}
}

