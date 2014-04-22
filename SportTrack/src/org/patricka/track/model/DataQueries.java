package org.patricka.track.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class DataQueries {
	
	private SQLiteDatabase db; 
	private DataBase myDB;
	
	public DataQueries(Context context) {
		myDB = new DataBase(context, "speedSporTrackDataBase.db", null, 1);
	}

	public void open(){
		db = myDB.getWritableDatabase();
	}
	
	public void openR(){
		db = myDB.getReadableDatabase();
	}

	public void close(){
		db.close();
	}
	
	public SQLiteDatabase getdb(){
		return db;
	}
	
	private ContentValues eventDataToCV(EventData data){
		ContentValues values = new ContentValues();
		//time is used as id; so is not set here to avoid potential update conflict
		//values.put(DataBase.COL_TIME, data.getTime());
		values.put(DataBase.COL_NAME, data.getName());
		values.put(DataBase.COL_TOP, data.getTop());
		values.put(DataBase.COL_TOP_A, data.getTopAc());
		values.put(DataBase.COL_TOP_4, data.getTop4());
		values.put(DataBase.COL_TOP_4_A, data.getTop4Ac());
		values.put(DataBase.COL_TOP_GPS, data.getTopG());
//		values.put(DataBase.COL_TOP_GPS_A, data.getTopGA());
		values.put(DataBase.COL_TOT_D, data.getTotDist());
		values.put(DataBase.COL_TOT_T, data.getTotTime());
		values.put(DataBase.COL_TOP_X, data.getTopX());
		values.put(DataBase.COL_TOP_Y, data.getTopY());
		values.put(DataBase.COL_TOP_4_X, data.getTop4X());
		values.put(DataBase.COL_TOP_4_Y, data.getTop4Y());
		return values;
	}
	
	private ContentValues pointDataToCV(PointData data){
		ContentValues values = new ContentValues();
		//time is used as id and event_id as external key; 
		//so is not set here to avoid potential update conflict
		//values.put(DataBase.COL_TIME, data.getTime());
		//values.put(DataBase.COL_EVENT, id);
		values.put(DataBase.COL_LAT, data.getLatitude());
		values.put(DataBase.COL_LON, data.getLongitude());
		values.put(DataBase.COL_ALT, data.getAltitude());
		values.put(DataBase.COL_STOP, data.getStop());
		values.put(DataBase.COL_SPEED, data.getSpeed());
		values.put(DataBase.COL_ACCU, data.getAccuracy());
		return values;
	}
	
//	public void special() {
//		open();
//		try{
//			db.execSQL("ALTER TABLE " + DataBase.TABLE_EVENT + " ADD " + DataBase.COL_TOP_X + " FLOAT DEFAULT 0 NOT NULL");
//			db.execSQL("ALTER TABLE " + DataBase.TABLE_EVENT + " ADD " + DataBase.COL_TOP_Y + " FLOAT DEFAULT 0 NOT NULL");
//			db.execSQL("ALTER TABLE " + DataBase.TABLE_EVENT + " ADD " + DataBase.COL_TOP_4_X + " FLOAT DEFAULT 0 NOT NULL");
//			db.execSQL("ALTER TABLE " + DataBase.TABLE_EVENT + " ADD " + DataBase.COL_TOP_4_Y + " FLOAT DEFAULT 0 NOT NULL");
//			db.execSQL("ALTER TABLE " + DataBase.TABLE_POINT + " ADD " + DataBase.COL_ACCU + " FLOAT DEFAULT 0 NOT NULL");
//			Log.i("SporTrack", "table ok");
//		}catch (Exception e) {
//			Log.e("SporTrack", "Aïe: " + e, e);
//		}
//		close();
//	}
 
	public long insertEvent(EventData data){
		ContentValues cv = eventDataToCV(data);
		cv.put(DataBase.COL_TIME, data.getTime());
//		Log.i("SporTrack", "What? " + data);
		try{
			return db.insert(DataBase.TABLE_EVENT, null, cv);
		}catch (Exception e) {
			Log.e("SporTrack", "what the hell: " + e, e);
			return -1;
		}
	}
	
	public long insertPoint(PointData data, long eventID){
		ContentValues cv = pointDataToCV(data);
		cv.put(DataBase.COL_TIME, data.getTime());
		cv.put(DataBase.COL_EVENT, eventID);
		return db.insert(DataBase.TABLE_POINT, null, cv);				
	}
 
	public int updateEvent(long id, EventData data){
		return db.update(DataBase.TABLE_EVENT, eventDataToCV(data), DataBase.COL_TIME + " = " + id, null);
	}
	
	//There is no update/delete for the points; they can't be modify (that would bias the result) 
	public int deleteEvent(long id){
		return deletePoints(id) + db.delete(DataBase.TABLE_EVENT, DataBase.COL_TIME + " = ?", new String[]{id+""});
	}
	
	//delete all the points of an event
	public int deletePoints(long id){
		return db.delete(DataBase.TABLE_POINT, DataBase.COL_EVENT + " = ?", new String[]{id+""});
	}
 
	public EventData getDataWithID(long id){
		try {
			Cursor c = db.query(DataBase.TABLE_EVENT, null, DataBase.COL_TIME + " = " + id, null, null, null, null);
			EventData ed = cursorToEvent(c).get(0);
			c = db.query(DataBase.TABLE_POINT, null, DataBase.COL_EVENT + " = " + id, null, null, null, DataBase.COL_TIME);
			ed.setPts(cursorToPoint(c));
			return ed;
		} catch (Exception e) {
			Log.e("SporTrack", "If no data, then null pointer; otherwise oupsss: " + e, e);
			return null;
		}
	}
	
	public ArrayList<EventData> getAll(){
		try{
			Cursor c = db.query(DataBase.TABLE_EVENT, null, null, null, null, null, DataBase.COL_TIME + " DESC");
			return cursorToEvent(c);
		}catch (Exception e) {
			Log.e("SporTrack", "If no data, then null pointer; otherwise oupsss: " + e, e);
			return null;
		}
	}
	
	private ArrayList<EventData> cursorToEvent(Cursor c){
		if (c.getCount() == 0)
			return null;
		ArrayList<EventData> data = new ArrayList<EventData>();
		c.moveToFirst();
		do {
			data.add(new EventData(c.getLong(0), c.getLong(8), c.getString(1), c.getFloat(2), c.getFloat(3), c.getFloat(5), c.getFloat(6), c.getFloat(4), c.getFloat(7), c.getDouble(9), c.getDouble(10), c.getDouble(11), c.getDouble(12)));
		} while(c.moveToNext());
		c.close();
		return data;
	}
	
	private ArrayList<PointData> cursorToPoint(Cursor c){
		if (c.getCount() == 0)
			return null;
		ArrayList<PointData> data = new ArrayList<PointData>();
		c.moveToFirst();
		do {
			data.add(new PointData(c.getLong(0), c.getDouble(2), c.getDouble(3), c.getDouble(4), c.getInt(5), c.getFloat(6), c.getFloat(7)));
		} while(c.moveToNext());
		c.close();
		return data;
	}
	
	public ArrayList<Location> pointsToLocations(ArrayList<PointData> points){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(PointData pd : points){
			locs.add(pointToLocation(pd));
		}
		return locs;
	}
	
	public Location pointToLocation(PointData pd){
		Location l = new Location("from db");
		l.setAccuracy(pd.getAccuracy());
		l.setAltitude(pd.getAltitude());
		l.setLatitude(pd.getLatitude());
		l.setLatitude(pd.getLongitude());
		l.setSpeed(pd.getSpeed());
		l.setTime(pd.getTime());	
		return l;
	}
}