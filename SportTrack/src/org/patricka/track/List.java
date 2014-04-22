package org.patricka.track;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.patricka.track.model.EventData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class List extends Activity {

	private ListView lv;
	private TextView tv;
	private ArrayList<EventData> dd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_tab);

		tv = (TextView) findViewById(R.id.txtIntro);
		lv = (ListView) findViewById(R.id.listEv);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showDialog(arg2);
			}
		});
	}
	
	@Override
    protected void onResume(){
		super.onResume();
		refreshList();
	}

	private void refreshList() {
		Main.DQ.openR();
		dd = Main.DQ.getAll();
		Main.DQ.close();
		lv.setAdapter(new SimpleAdapter(this, getDataList(), R.layout.list, new String[]{"tit", "dat", "id"}, new int[]{R.id.listItemTit, R.id.listItem, R.id.listItemID}));
	}

	private ArrayList<Map<String, String>> getDataList() {
		ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> data;
			for (EventData ed : dd) {
				data = new HashMap<String, String>();
				data.put("tit", ed.getName());
				data.put("dat", EventData.DATE_F.format(new Date(ed.getTime())));
				data.put("id", ed.getTime() + "");
				dataList.add(data);
			}
		} catch (Exception e) {
			Log.e("GET ALL", "Most probablly database is empty: " + e);
		}
		return dataList;
	}

	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog ad = null;
		final int gg = id;
		final LinearLayout la = new LinearLayout(this);
		la.setOrientation(LinearLayout.VERTICAL);
		final TextView txt = new TextView(this);
		txt.setText(dd.get(id).toString().substring(dd.get(id).getName().length()));
		final EditText in = new EditText(this);
		in.setText(dd.get(id).getName());
		la.addView(in);
		la.addView(txt);
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		bui.setView(la)
			.setMessage("\n\nHit back button to Cancel.\nDelete will permanently remove the event!")
			.setCancelable(true)
			.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(in.getText().toString().trim().equalsIgnoreCase("")){
							Toast.makeText(getApplicationContext(), "Please fill in the name.", Toast.LENGTH_LONG).show();
						} else {
							EventData ed = dd.get(gg);
							ed.setName(in.getText().toString());
							Main.DQ.open();
							if(Main.DQ.updateEvent(ed.getTime(), ed) > 0)
								tv.setText("Event updated successfully");
							else 
								tv.setText("Problem in updating the event");
							Main.DQ.close();
							refreshList();
						}
					}
				})
			.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Main.DQ.open();
						tv.setText(Main.DQ.deleteEvent(dd.get(gg).getTime()) > 0 ? "Event deleted sucessfully." : "Failed to delete event.");
						Main.DQ.close();
						refreshList();
					}
				});

		ad = bui.create();

		return ad;
	}

}
