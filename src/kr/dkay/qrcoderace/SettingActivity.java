package kr.dkay.qrcoderace;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends Activity {
	private ArrayList<String> items;
	private ListView teamlist;
	@Override   
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.team_name_setting);
		

		Intent intent = getIntent(); // 이 액티비티를 시작하게 한 인텐트를 호출
		teamlist = (ListView)findViewById(R.id.teamlist);
	    teamlist.setAdapter(new ArrayAdapter <String> (this,
	            R.layout.listview_layout_probs,
	            intent.getStringArrayListExtra("teamlist")));
	    
//		TextView teamName = (TextView)findViewById(R.id.teamname);
//		intent.putExtra("team_name", teamName.getText().toString());
//		setResult(RESULT_OK,intent); // 추가 정보를 넣은 후 다시 인텐트를 반환합니다.
//		finish(); // 액티비티 종료
	}
}

