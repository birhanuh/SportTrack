package org.patricka.track;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.patricka.track.model.DataQueries;
import org.patricka.track.model.EventData;
import org.patricka.track.model.PointData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class Main extends TabActivity implements OnClickListener {
	
	private TabHost tabs = null;
	private static final String TAB_NAME_TRACK = "Track";
	private static final String TAB_NAME_MAP = "Map";
	private static final String TAB_NAME_MAN = "Manage";
	private TextView loc, loc2, cs, cs2, gs, tgs, ts, ts2, as, t, d;
	private Button sta, rese;//sto, , test 
	// private FindLocation fl;
	private LocationManager lmg; // lmn,
	private LocationListener lg;// ll,
	private boolean started = false;
	private long sumTime = 0, prevTime = 0; //startTime = 0, 
	private float top = 0, topac = 0, top2 = 0, top2ac = 0, topAv = 0, topG = 0, sumDistance = 0;
	private Location prev;
	private static DecimalFormat df;
	private ArrayList<Location> locs;
	//private int pos20;
	public static DataQueries DQ;
	private EventData ed;
	private PointData pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.setTitle("\t\t\t\t\t..:: SporTrack ::..");
		tabs = this.getTabHost();
		tabs.addTab(tabs.newTabSpec(TAB_NAME_TRACK).setIndicator(TAB_NAME_TRACK).setContent(R.id.tab_track));
        tabs.addTab(tabs.newTabSpec(TAB_NAME_MAP).setIndicator(TAB_NAME_MAP).setContent(new Intent(this.getApplicationContext(), Map.class)));
        //tabs.addTab(tabs.newTabSpec(TAB_NAME_MAN).setIndicator(TAB_NAME_MAN).setContent(R.id.tab_manage));
        
        //tab data management
        tabs.addTab(tabs.newTabSpec(TAB_NAME_MAN).setIndicator(TAB_NAME_MAN).setContent(new Intent(this.getApplicationContext(), List.class)));
        
        tabs.setCurrentTab(0);
    	tabs.setOnTabChangedListener(new TabChangeListener());
    	
    	//tab track
		loc = (TextView) findViewById(R.id.txtLoc);
		loc2 = (TextView) findViewById(R.id.txtLoc2);
		cs = (TextView) findViewById(R.id.txtCAS);
		cs2 = (TextView) findViewById(R.id.txtCAS20);
		gs = (TextView) findViewById(R.id.txtGS);
		tgs = (TextView) findViewById(R.id.txtTGS);
		ts = (TextView) findViewById(R.id.txtTS);
		ts2 = (TextView) findViewById(R.id.txtTS20);
		as = (TextView) findViewById(R.id.txtAS);
		t = (TextView) findViewById(R.id.txtT);
		d = (TextView) findViewById(R.id.txtD);
		setDefText();
		sta = (Button) findViewById(R.id.btnStart);
		sta.setOnClickListener(this);
//		sto = (Button) findViewById(R.id.btnStop);
//		sto.setOnClickListener(this);
//		sto.setEnabled(false);
		rese = (Button) findViewById(R.id.btnRe);
		rese.setOnClickListener(this);
		lmg = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		lg = new LocLis();
		df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);
		locs = new ArrayList<Location>();
		
		//tab map
//		test = (Button) findViewById(R.id.btnTest);
//		test.setOnClickListener(this);
		
		//database
		//pos20 = 0;
		if(DQ == null)
			DQ = new DataQueries(this);
//		DQ.special();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//kill gps and save data...
		Toast.makeText(getApplicationContext(), "Bye!", Toast.LENGTH_LONG).show();
	}
	
	private void setDefText() {
		loc.setText(getString(R.string.g) + " " + getString(R.string.loc) + ": ");
		loc2.setText("Provider Info");
		as.setText(getString(R.string.avg) + " " + getString(R.string.speed) + ": ");
		cs.setText(getString(R.string.cur) + " " + getString(R.string.speed) + ": ");
		ts.setText(getString(R.string.top) + " " + getString(R.string.speed) + ": ");
		cs2.setText(getString(R.string.four) + " " + getString(R.string.cur) + " " + getString(R.string.speed) + ": ");
		ts2.setText(getString(R.string.four) + " " + getString(R.string.top) + " " + getString(R.string.speed) + ": ");
		t.setText(getString(R.string.ti) + ": ");
		d.setText(getString(R.string.di) + ": ");
		gs.setText(getString(R.string.g) + " " + getString(R.string.speed) + ": ");
		tgs.setText(getString(R.string.g) + " " + getString(R.string.top) + " " + getString(R.string.speed) + ": ");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == sta.getId()) {
			if(sta.getText().toString().equalsIgnoreCase(getString(R.string.start)) || sta.getText().toString().equalsIgnoreCase(getString(R.string.restart))){
				lmg.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, lg);
				sta.setText(R.string.stop);
				rese.setText(R.string.res);
				if(ed == null){
					ed = new EventData(System.currentTimeMillis());
					DQ.open();
					DQ.insertEvent(ed);
					DQ.close();
				}
				tabs.getTabWidget().setEnabled(false);
			}else{
				started = false;
				lmg.removeUpdates(lg);
				sta.setText(R.string.restart);
				rese.setText(R.string.rese);
				tabs.getTabWidget().setEnabled(true);
				showDialog(1);
				
			}
		} else if (v.getId() == rese.getId()) {
			top2 = top = topG = topac = top2ac = topAv = sumDistance = prevTime = sumTime = 0;//startTime = 
			prev = null;
			locs = new ArrayList<Location>();
			started = false;
			setDefText();
			if(ed != null){
				DQ.open();
				DQ.deletePoints(ed.getTime());
				DQ.close();
			}
			if(rese.getText().toString().equalsIgnoreCase(getString(R.string.rese))){
				ed = null;
				rese.setText(R.string.res);
				sta.setText(R.string.start);
			}
		} 
//		else if (v.getId() == test.getId()) {
//			DQ.special();
//		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.help:
			showDialog(-1);
			return true;
		case R.id.about:
			showDialog(-2);
			return true;
		case R.id.pref:
			startActivity(new Intent(this, org.patricka.track.Pref.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog ad = null;
		// final int gg = id;
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		if(id < 0){
			bui.setMessage(id == -1 ? "Help\nThis application will calculate your speed."
							+ "\nSimply click the start button to start a new speed tracking event.\n"
							: "SporTrack\nVersion: 0.9\nAuthor: Birhan Haielmariam, Sept 2011")
					.setCancelable(true).setPositiveButton("OK", null);
		}else{
			final EditText in = new EditText(this);
			in.setText(ed.getName());
			bui.setView(in)
			.setMessage("\nEnter the event name")
			.setCancelable(true)
			.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(in.getText().toString().trim().equalsIgnoreCase("")){
							Toast.makeText(getApplicationContext(), "Please fill in the name.", Toast.LENGTH_LONG).show();
						} else {
							ed.setName(in.getText().toString());
							Main.DQ.open();
							if(Main.DQ.updateEvent(ed.getTime(), ed) > 0)
								Toast.makeText(getApplicationContext(), "Event updated successfully", Toast.LENGTH_LONG).show();
							else 
								Toast.makeText(getApplicationContext(), "Problem in updating the event", Toast.LENGTH_LONG).show();
							Main.DQ.close();
						}
					}
				});
		}

		ad = bui.create();
		return ad;
	}
	
	public static SharedPreferences getSharedPreferences (Context ctxt) {
		   return ctxt.getSharedPreferences("FILE", 0);
		}


	public static String formatTime(long e) {
		String format = String.format("%%0%dd", 2);
		String mili = String.format("%03d", e % 1000);
		e /= 1000;
		String seconds = String.format(format, e % 60);
		String minutes = String.format(format, (e % 3600) / 60);
		String hours = String.format(format, e / 3600);
		return hours + ":" + minutes + ":" + seconds + (SporTrack.getApp().getSharedPreferences(Pref.APP_N, Activity.MODE_PRIVATE).getBoolean(Pref.MILI, false) ? "." + mili : "");
	}
	
	public static String formatSpeed(float f) {
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);
		switch (SporTrack.getApp().getSharedPreferences(Pref.APP_N, Activity.MODE_PRIVATE).getInt(Pref.SPEED, Pref.KMH)) {
		case Pref.MS:
			return df.format(f) + " m/s";
		case Pref.MPH:
			return df.format(f * 2.236936) + " mph";
		case Pref.KN:
			return df.format(f * 1.943844) + " knot";
		default:
			return df.format(f * 3.6) + " km/h";
		}
	}
	
	public static String formatDistance(float f) {
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);
		switch (SporTrack.getApp().getSharedPreferences(Pref.APP_N, Activity.MODE_PRIVATE).getInt(Pref.DIST, Pref.METER)) {
			case Pref.YARD:
				if(f >= 1609.344){
					df.setMaximumFractionDigits(3);
					df.setMinimumFractionDigits(3);
				}
				return f >= 1609.344 ? df.format((f / 1000) * (1 / 1.609344)) + " mi" : df.format(f * 1.0936) + " yd";
			case Pref.NAUT:
				df.setMaximumFractionDigits(3);
				df.setMinimumFractionDigits(3);
				return df.format((f / 1000) * (1 / 1.852)) + " nmi";
			default:
				if(f > 1000){
					df.setMaximumFractionDigits(3);
					df.setMinimumFractionDigits(3);
				}
				return f > 1000 ? df.format(f / 1000) + " km" : df.format(f) + " m";
		}
	}

	private class LocLis implements LocationListener {

		public LocLis() {
		}

		@Override
		public void onLocationChanged(Location l) {
			loc.setText(getString(R.string.g) + " " + getString(R.string.loc) + ": " + l.toString());
			if (l.getAccuracy() <= 10.0) {
				try {
					long time = l.getTime();//System.currentTimeMillis();
					pd = new PointData(time, l.getLatitude(), l.getLongitude(), l.getAltitude(), started ? 1 : 0, l.getSpeed(), l.getAccuracy());
					//if we are starting the app (either first time OR after a stop/start) we can't trust the previous points :)
					if(started){
						float lastD = l.distanceTo(prev), cur = prevTime + 10 < time ? lastD / ((time - prevTime) / 1000) : 0;
						sumDistance += lastD;
						sumTime += Math.abs(time - prevTime);//in case we travel in past :)
						as.setText(getString(R.string.avg) + " " + getString(R.string.speed) + ": " + formatSpeed(sumDistance / (sumTime / 1000)));
						cs.setText(getString(R.string.cur) + " " + getString(R.string.speed) + ": " + formatSpeed(cur));
						if (cur > top){
							top = cur;
							topac = ((lastD + l.getAccuracy() + prev.getAccuracy()) / ((time - prevTime) / 1000)) - top;
							ed.setTopX(l.getLatitude());
							ed.setTopY(l.getLongitude());
						}
						//will be override too many times... But otherwise, if user change unit preference...
						ts.setText(getString(R.string.top) + " " + getString(R.string.speed) + ": " + formatSpeed(top) + " \u00B1 " + formatSpeed(topac));
						
						//4 points average
						if(locs != null && locs.size() == 4){
							lastD = 0;
							float accu = 0;
							for(int i = 0; i < locs.size() - 1; i++){
								lastD += locs.get(i).distanceTo(locs.get(i + 1));
								accu += locs.get(i).getAccuracy();
							}
							accu += locs.get(locs.size() - 1).getAccuracy();
							lastD += l.distanceTo(locs.get(locs.size() - 1));
							//WRONG TIME???
							cur = lastD / ((time - locs.get(0).getTime()) / 1000);
							cs2.setText(getString(R.string.four) + " (" + formatDistance(lastD) + ") " + getString(R.string.cur) + " " + getString(R.string.speed) + ": " + formatSpeed(cur));
							if (cur > top2){
								top2 = cur;
								top2ac = ((lastD + l.getAccuracy() + accu) / ((time - locs.get(0).getTime()) / 1000)) - top2;
								topAv = lastD;
								ed.setTop4X(l.getLatitude());
								ed.setTop4Y(l.getLongitude());
							}
							//will be override too many times... But otherwise, if user change unit preference...
							ts2.setText(getString(R.string.four) + " (" + formatDistance(topAv) + ") " + getString(R.string.top) + " " + getString(R.string.speed) + ": " + formatSpeed(top2) + " \u00B1 " + formatSpeed(top2ac));

							try {
								//clean some memory
								locs.remove(0);
							} catch (Exception e) {
							}
						}
					}else{
						started = true;
						locs = new ArrayList<Location>();
					}
							
					t.setText(getString(R.string.ti) + ": " + formatTime(sumTime));
					d.setText(getString(R.string.di) + ": " + formatDistance(sumDistance));
					//GPS speed
					gs.setText(getString(R.string.g) + " " + getString(R.string.speed) + ": " + formatSpeed(l.getSpeed()));
					if (l.getSpeed() > topG){
						topG = l.getSpeed();
					}
					tgs.setText(getString(R.string.g) + " " + getString(R.string.top) + " " + getString(R.string.speed) + ": " + formatSpeed(topG));
					locs.add(l);
					prev = l;
					prevTime = time;
					ed.setEventData(sumTime, top, topac, top2, top2ac, topG, sumDistance);
					DQ.open();
					DQ.insertPoint(pd, ed.getTime());
					DQ.updateEvent(ed.getTime(), ed);
					DQ.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					loc2.setText("BOOM: " + e + " :: ");
				}
			}else{
				//loc2.setText("Wait till point get better accuracy");
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
			loc2.setText("On provider disabled: " + arg0);
		}

		@Override
		public void onProviderEnabled(String arg0) {
			loc2.setText("On provider enabled: " + arg0);
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			loc2.setText("On status changed: " + arg0 + " Arg1: " + arg1);
		}

	};
	
	private class TabChangeListener implements OnTabChangeListener{

		@Override
		public void onTabChanged(String tabId) {
			if(TAB_NAME_MAP.equals(tabId)) {
				
			} else if(TAB_NAME_TRACK.equals(tabId)){
				
			}
		}	
	}
}