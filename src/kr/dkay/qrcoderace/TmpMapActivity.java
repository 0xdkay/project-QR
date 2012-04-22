package kr.dkay.qrcoderace;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class TmpMapActivity extends MapActivity {


	private MapView worldMap;

	private MyLocationOverlay me = null;
	private Drawable marker;
	private Double myLat;
	private Double myLng;
	
	
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.mapview);
    	marker = getResources().getDrawable(R.drawable.ic_launcher);
    	worldMap = (MapView)findViewById(R.id.worldmap);
    	me = new MyLocationOverlay(this, worldMap);
    	

        myLat = 36.368023;
        myLng = 127.365446;
    	worldMap.getController().setCenter(new GeoPoint((int)(myLat*1E6), (int)(myLng*1E6)));
    	worldMap.getController().setZoom(16);  // �󸶳� ���ܼ� �� ������
    	marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight()); // ũ������
    	worldMap.getOverlays().add(new MyOverlay(marker, myLat, myLng)); // ��Ŀ �׷��� Ŭ������ �ѱ��.
    	worldMap.getOverlays().add(me);
    }
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}



}
