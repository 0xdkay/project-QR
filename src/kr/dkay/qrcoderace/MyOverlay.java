package kr.dkay.qrcoderace;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyOverlay extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private Drawable marker = null;

	public MyOverlay(Drawable marker, double lat, double lng) {
		super(marker);
	  // TODO Auto-generated constructor stub
		this.marker = marker;
		items.add(new OverlayItem(new GeoPoint((int)(lat*1E6), (int)(lng*1E6)), "You're Hear", "Hear"));
		populate(); // 요거 실행함으로써 item 이 생성되는 듯 하다.
	} 

	@Override
	protected OverlayItem createItem(int i) {
	  // TODO Auto-generated method stub
		return items.get(i);
	} 

	@Override
	public int size() {
	  // TODO Auto-generated method stub
		return items.size();
	} 

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	  // TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}
}
