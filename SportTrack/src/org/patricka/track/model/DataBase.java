package org.patricka.track.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DataBase extends SQLiteOpenHelper {

	public static final String TABLE_EVENT = "table_event";
	public static final String COL_TIME = "time";
	public static final String COL_NAME = "name";
	public static final String COL_TOP = "top";
	public static final String COL_TOP_A = "top_accuracy";
	public static final String COL_TOP_GPS = "topGPS";
//	public static final String COL_TOP_GPS_A = "topGPS_accuracy";
	public static final String COL_TOP_4 = "top_four";
	public static final String COL_TOP_4_A = "top_four_accuracy";
	public static final String COL_TOT_T = "total_time";
	public static final String COL_TOT_D = "total_distance";
	public static final String COL_TOP_X = "top_x";
	public static final String COL_TOP_Y = "top_y";
	public static final String COL_TOP_4_X = "top_f_x";
	public static final String COL_TOP_4_Y = "top_f_y";
	public static final String TABLE_POINT = "table_point";
	public static final String COL_EVENT = "event_id";
	public static final String COL_LAT = "latitude";
	public static final String COL_LON = "longitude";
	public static final String COL_ALT = "altitude";
	public static final String COL_STOP = "stop";
	public static final String COL_SPEED = "speed";
	public static final String COL_ACCU = "accuracy";
	
	
	
	public DataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		+ COL_TOP_GPS_A + " FLOAT NOT NULL, "
		db.execSQL("CREATE TABLE " + TABLE_EVENT + " ("
				+ COL_TIME + " INTEGER PRIMARY KEY, " 
				+ COL_NAME + " TEXT NOT NULL, "
				+ COL_TOP + " FLOAT NOT NULL, "
				+ COL_TOP_A + " FLOAT NOT NULL, "
				+ COL_TOP_GPS + " FLOAT NOT NULL, "
				+ COL_TOP_4 + " FLOAT NOT NULL, "
				+ COL_TOP_4_A + " FLOAT NOT NULL, "
				+ COL_TOT_D + " FLOAT NOT NULL, "
				+ COL_TOT_T + " INTEGER NOT NULL, "
				+ COL_TOP_X + " FLOAT NOT NULL, "
				+ COL_TOP_Y + " FLOAT NOT NULL, "
				+ COL_TOP_4_X + " FLOAT NOT NULL, "
				+ COL_TOP_4_Y + " FLOAT NOT NULL);");
		db.execSQL("CREATE TABLE " + TABLE_POINT + " ("
				+ COL_TIME + " INTEGER PRIMARY KEY, " 
				+ COL_EVENT + " INTEGER NOT NULL, "
				+ COL_LAT + " REAL NOT NULL, "
				+ COL_LON + " REAL NOT NULL, "
				+ COL_ALT + " REAL NOT NULL, "
				+ COL_STOP + " INTEGER NOT NULL," 
				+ COL_SPEED + " FLOAT NOT NULL, "
				+ COL_ACCU + " FLOAT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_EVENT + ";");
		db.execSQL("DROP TABLE " + TABLE_POINT + ";");
		onCreate(db);
	}

}