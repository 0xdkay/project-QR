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
		

		Intent intent = getIntent(); // �� ��Ƽ��Ƽ�� �����ϰ� �� ����Ʈ�� ȣ��
		teamlist = (ListView)findViewById(R.id.teamlist);
	    teamlist.setAdapter(new ArrayAdapter <String> (this,
	            R.layout.listview_layout_probs,
	            intent.getStringArrayListExtra("teamlist")));
	    
//		TextView teamName = (TextView)findViewById(R.id.teamname);
//		intent.putExtra("team_name", teamName.getText().toString());
//		setResult(RESULT_OK,intent); // �߰� ������ ���� �� �ٽ� ����Ʈ�� ��ȯ�մϴ�.
//		finish(); // ��Ƽ��Ƽ ����
	}
}

