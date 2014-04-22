package org.patricka.track;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Pref extends Activity implements OnClickListener {

	public static final String APP_N = "SporTrack";
	public static final String DIST = "distance";
	public static final int METER = 0;
	public static final int YARD = 1;
	public static final int NAUT = 2;
	public static final String SPEED = "speed";
	public static final int MS = 0;
	public static final int KMH = 1;
	public static final int MPH = 2;
	public static final int KN = 3;
	public static final String MILI = "milis";
	public static final String SATEL = "satel";

	private Button ok, ca;
	private RadioGroup rd, rs;
	private CheckBox pr, ma;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pref);

		ok = (Button) findViewById(R.id.btOk);
		ok.setOnClickListener(this);
		ca = (Button) findViewById(R.id.btCa);
		ca.setOnClickListener(this);
		rd = (RadioGroup) findViewById(R.id.rgDist);
		rs = (RadioGroup) findViewById(R.id.rgSp);
		pr = (CheckBox) findViewById(R.id.cbPr);
		ma = (CheckBox) findViewById(R.id.cbMa);

		int s = getSharedPreferences(APP_N, Activity.MODE_PRIVATE).getInt(DIST, METER);
		((RadioButton) findViewById(s == METER ? R.id.rM : (s == YARD ? R.id.rY : R.id.rN))).setChecked(true);
		s = getSharedPreferences(APP_N, Activity.MODE_PRIVATE).getInt(SPEED, KMH);
		((RadioButton) findViewById(s == KMH ? R.id.rKh : (s == MPH ? R.id.rMh : (s == MS ? R.id.rMs : R.id.rKn)))).setChecked(true);
		pr.setChecked(getSharedPreferences(APP_N, Activity.MODE_PRIVATE).getBoolean(MILI, false));
		ma.setChecked(getSharedPreferences(APP_N, Activity.MODE_PRIVATE).getBoolean(SATEL, false));
	}

	@Override
	public void onBackPressed() {
		this.finish();
		return;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == ok.getId()) {
			switch (rd.getCheckedRadioButtonId()) {
			case R.id.rY:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(DIST, YARD).commit();
				break;
			case R.id.rN:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(DIST, NAUT).commit();
				break;
			default:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(DIST, METER).commit();
				break;
			}
			switch (rs.getCheckedRadioButtonId()) {
			case R.id.rMh:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(SPEED, MPH).commit();
				break;
			case R.id.rKn:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(SPEED, KN).commit();
				break;
			case R.id.rMs:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(SPEED, MS).commit();
				break;
			default:
				getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putInt(SPEED, KMH).commit();
				break;
			}
			getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putBoolean(MILI, pr.isChecked()).commit();
			getSharedPreferences(APP_N, Activity.MODE_PRIVATE).edit().putBoolean(SATEL, ma.isChecked()).commit();
			Toast.makeText(getApplicationContext(), "Preferences saved. If the app is running, the units will change as soon as new GPS points are received.", Toast.LENGTH_LONG).show();
		}
		this.finish();
		return;
	}
}
