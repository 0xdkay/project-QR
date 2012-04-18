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

		final Intent intent = getIntent(); // �� ��Ƽ��Ƽ�� �����ϰ� �� ����Ʈ�� ȣ��
	
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
	    		setResult(RESULT_OK,intent); // �߰� ������ ���� �� �ٽ� ����Ʈ�� ��ȯ�մϴ�.
	    		finish(); // ��Ƽ��Ƽ ����
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

