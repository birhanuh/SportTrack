package org.patricka.track;

import android.app.Application;
import android.content.Context;

public class SporTrack extends Application {
	
	private static Context app;
	
	@Override
	public void onCreate(){
		app = this;
	}
	
	public static Context getApp(){
		return app;
	}

}
