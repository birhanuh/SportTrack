package org.patricka.track;

import java.util.ArrayList;

import org.patricka.track.model.EventData;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class Map extends MapActivity {
	
	private MapView mv;
	private EventData ed;
	private ArrayList<EventData> ev;
	private Spinner sp;
	private ArrayAdapter<CharSequence> spAd;
	//to have a nice zoom (http://www.anddev.org/zoom_and_span_on_a_cluster_of_points-t464.html)
	//but sometimes sucks 
	private int minLatitude = (int)(+81 * 1E6);
    private int maxLatitude = (int)(-81 * 1E6);
    private int minLongitude  = (int)(+181 * 1E6);
    private int maxLongitude  = (int)(-181 * 1E6);
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mv = (MapView) findViewById(R.id.map);
		mv.setBuiltInZoomControls(true);
		mv.displayZoomControls(true);
		sp = (Spinner) findViewById(R.id.spin);
	}
    
    @Override
    protected void onResume(){
//    	Log.i("SporTrack", "onResume");
    	super.onResume();
    	updateSpin();
		sp.setOnItemSelectedListener(new MyOnItemSelectedListener());
		//by default, draw the first event on map
		Main.DQ.openR();
    	try{
    		ed = Main.DQ.getDataWithID(ev.get(0).getTime());
    	}catch (Exception e) {
			ed = null;
		}
    	Main.DQ.close();
    	mv.setSatellite(getSharedPreferences(Pref.APP_N, Activity.MODE_PRIVATE).getBoolean(Pref.SATEL, false));
    	drawMap(); 
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void updateSpin(){
		Main.DQ.openR();
		ev = Main.DQ.getAll();
		Main.DQ.close();
		spAd = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		spAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(spAd);
		try{
			for(EventData evd : ev){
				spAd.add(evd.getName() + " " + EventData.DATE_F.format(evd.getTime()));
			}
		}catch (Exception e) {
			spAd.add("NO EVENT :(");
		}
	}
	
	private void drawMap(){
		try{
			if(ed == null || ed.getPts().size() == 0)
				return;	
			//remove the "old event" from map
			mv.getOverlays().clear();
		}catch (Exception e) {
			Log.e("SporTrack", "maybe the overlays or event is empty :D " + e, e);
			return;
		}
		mv.getOverlays().add(new MyOverlay());
		//center map on the middle of the event
		MapController mc = mv.getController();
		mc.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));
		//mc.setCenter(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
		mc.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
	}
	
	private void checkMinMax(double lat, double lon){
		int latitude = (int) lat, longitude = (int) lon;
        minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
        maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;               
        minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
        maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
	}
	
	class MyOverlay extends Overlay{

	    public MyOverlay(){
	    }
	    
	    //to have readable text make it white with black border 
	    private void drawMyText(Canvas canvas, String text, int x, int y){
	    	//around the letters
	    	Paint mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setARGB(255, 0, 0, 0);
	        mPaint.setStrokeWidth(2);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setTextAlign(Paint.Align.CENTER);
	        mPaint.setTextSize(16);
	        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
	        //to add text
	        Paint textPaint = new Paint();
	        textPaint.setARGB(255, 255, 255, 255);
	        textPaint.setTextAlign(Paint.Align.CENTER);
	        textPaint.setTextSize(16);
	        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
	        canvas.drawText(text, x, y, mPaint);
            canvas.drawText(text, x, y, textPaint);
	    }

	    public void draw(Canvas canvas, MapView mapv, boolean shadow){
	        super.draw(canvas, mapv, shadow);
	        if(ed == null || ed.getPts().size() == 0)
	        	return;
	        float avg = ed.getTotDist() / (ed.getTotTime() / 1000), bigA = (ed.getTopG() + avg) / 2, smallA = avg / 2;//, cur;
	        GeoPoint gP1, gP2;
	        Point p1 = new Point(), p2 = new Point(), p3 = new Point();
	        Path path = new Path();
//	        ArrayList<Location> locs = new ArrayList<Location>();
//	        Location l1, l2;
	        Paint mPaint = new Paint();
	        mPaint.setDither(true);
//	        mPaint.setColor(Color.RED);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(2);
	        mPaint.setTextAlign(Paint.Align.CENTER);
	        mPaint.setTextSize(16);
	        mPaint.setTypeface(Typeface.DEFAULT_BOLD);

	        //to add text
//	        Paint textPaint = new Paint();
//	        textPaint.setARGB(255, 255, 255, 255);
//	        textPaint.setTextAlign(Paint.Align.CENTER);
//	        textPaint.setTextSize(16);
//	        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
	        
	        
            checkMinMax((ed.getPts().get(0).getLatitude() * 1E6), (ed.getPts().get(0).getLongitude() * 1E6));
	        for(int i = 0; i < ed.getPts().size() - 1; i++){
	        	
		        gP1 = new GeoPoint((int) (ed.getPts().get(i).getLatitude() * 1E6), (int) (ed.getPts().get(i).getLongitude() * 1E6));
		        gP2 = new GeoPoint((int) (ed.getPts().get(i + 1).getLatitude() * 1E6), (int) (ed.getPts().get(i + 1).getLongitude() * 1E6));
		        checkMinMax(ed.getPts().get(i + 1).getLatitude() * 1E6, ed.getPts().get(i + 1).getLongitude() * 1E6);
		        
//		        Log.i("SporTrack", "cur: " + ed.getPts().get(i + 1).getSpeed() + " :: avg: " + avg + " :: big: " + bigA + " :: sma: " + smallA);
		        if(ed.getPts().get(i + 1).getSpeed() > bigA)
		        	mPaint.setColor(Color.RED);
		        else if(ed.getPts().get(i + 1).getSpeed() > avg + (ed.getTopG() / 20))
		        	mPaint.setARGB(255, 255, 140, 0); //mPaint.setColor(Color.MAGENTA);
		        else if(Math.abs(ed.getPts().get(i + 1).getSpeed() - avg) <= (ed.getTopG() / 20))
		        	mPaint.setColor(Color.YELLOW);
		        else if(ed.getPts().get(i + 1).getSpeed() > smallA)
		        	mPaint.setColor(Color.GREEN);
		        else
		        	mPaint.setColor(Color.BLUE);
	
		        mapv.getProjection().toPixels(gP1, p1);
		        mapv.getProjection().toPixels(gP2, p2);
		        path = new Path();
		        path.moveTo(p2.x, p2.y);
		        path.lineTo(p1.x,p1.y);
	
		        canvas.drawPath(path, mPaint);
		        
		        //check the GPS speed (since it can be many points with that precision, only the last one will be show)
		        if(Math.abs(ed.getPts().get(i + 1).getSpeed() - ed.getTopG()) < 0.01){
		        	p3.set(p2.x, p2.y);
//		        	p3 = p2;
		        }
		        /* This sucks... I lost the precision into database :(
		        l1 = Main.DQ.pointToLocation(ed.getPts().get(i));
	        	l2 = Main.DQ.pointToLocation(ed.getPts().get(i + 1));
		        cur = l1.distanceTo(l2) / ((l2.getTime() - l1.getTime()) / 1000);
		        if(Math.abs(cur - ed.getTop()) < 0.01){
		        	canvas.drawText("Top " + Main.formatSpeed(ed.getTop()), p2.x - 10, p2.y - 10, mPaint);
		            canvas.drawText("Top " + Main.formatSpeed(ed.getTop()), p2.x - 10, p2.y - 10, textPaint);
		        }
		        */
	        }
//	        Log.i("SporTrack", "p1.x: " + p1.x + " :: p2.x: " + p2.y);
	        //if the end of the path go in the WEST direction, the "end text" should be on left
	        int mvTxt = (ed.getPts().size() > 11 && ed.getPts().get(ed.getPts().size() - 1).getLongitude() - ed.getPts().get(ed.getPts().size() - 10).getLongitude() < 0) ? -70 :70;
	       
	        drawMyText(canvas, "Average " + Main.formatSpeed(avg), p2.x + mvTxt, p2.y + 10);
            drawMyText(canvas, "Time " + Main.formatTime(ed.getTotTime()), p2.x + mvTxt, p2.y + 30);
            drawMyText(canvas, "Distance " + Main.formatDistance(ed.getTotDist()), p2.x + mvTxt, p2.y + 50);

            gP1 = new GeoPoint((int) (ed.getTopX() * 1E6), (int) (ed.getTopY() * 1E6));
            mapv.getProjection().toPixels(gP1, p2);
            mPaint.setColor(Color.RED);
            canvas.drawCircle(p2.x, p2.y, 3, mPaint);
            drawMyText(canvas, "Top " + Main.formatSpeed(ed.getTop()), p2.x, p2.y - (Math.abs(p3.y - p2.y) < 10 ? 30 : 10));
            gP1 = new GeoPoint((int) (ed.getTop4X() * 1E6), (int) (ed.getTop4Y() * 1E6));
            mapv.getProjection().toPixels(gP1, p1);
            canvas.drawCircle(p1.x, p1.y, 3, mPaint);
            drawMyText(canvas, "Top 5 points " + Main.formatSpeed(ed.getTop4()), p1.x, p1.y - (Math.abs(p3.y - p2.y) + Math.abs(p2.y - p1.y) < 20 ? 50 : (Math.abs(p2.y - p1.y) < 10 ? 30 : 10)));
            canvas.drawCircle(p3.x, p3.y, 3, mPaint);
        	drawMyText(canvas, "GPS top " + Main.formatSpeed(ed.getTopG()), p3.x, p3.y - 10);
//            Log.i("SporTrack", "p1.y: " + p1.y + " :: p2.y: " + p2.y + " :: p3.y: " + p3.y);
	    }
	}
	
	class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	Main.DQ.openR();
	    	try{
	    		ed = Main.DQ.getDataWithID(ev.get(pos).getTime());
	    	}catch (Exception e) {
				ed = null;
			}
	    	Main.DQ.close();
	    	drawMap();
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	      // Do nothing.
	    }
	}
}
