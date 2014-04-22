package org.patricka.track.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.patricka.track.Main;

public class EventData {
	
	public static SimpleDateFormat DATE_F = new SimpleDateFormat(
	"yyyy-MM-dd HH:mm:ss");
	private long time, totTime;
	private String name;
	private float top, topAc, top4, top4Ac, topG, totDist; //topGA, 
	private double topX, topY, top4X, top4Y;
	private ArrayList<PointData> pts;
	
	public EventData() {
		pts = new ArrayList<PointData>();
	}

	public EventData(long time) {
		this.time = time;
		totTime = 0;
		name = "NAME";
		top = topAc = top4 = top4Ac = topG = totDist = 0;//topGA = 
		topX = topY = top4X = top4Y = 0;
		pts = new ArrayList<PointData>();
		
	}

	public EventData(long time, long totTime, String name, float top,
			float topAc, float top4, float top4Ac, float topG, float totDist,
			double topX, double topY, double top4X, double top4Y) {
		//float topGA,
		this.time = time;
		this.totTime = totTime;
		this.name = name;
		this.top = top;
		this.topAc = topAc;
		this.top4 = top4;
		this.top4Ac = top4Ac;
		this.topG = topG;
//		this.topGA = topGA;
		this.totDist = totDist;
		this.topX = topX;
		this.topY = topY;
		this.top4X = top4X;
		this.top4Y = top4Y;
		pts = new ArrayList<PointData>();
	}
	
	public void setEventData(long totTime, float top, float topAc, float top4,
			float top4Ac, float topG, float totDist) {//float topGA, 
		this.totTime = totTime;
		this.top = top;
		this.topAc = topAc;
		this.top4 = top4;
		this.top4Ac = top4Ac;
		this.topG = topG;
//		this.topGA = topGA;
		this.totDist = totDist;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTotTime() {
		return totTime;
	}

	public void setTotTime(long totTime) {
		this.totTime = totTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getTopAc() {
		return topAc;
	}

	public void setTopAc(float topAc) {
		this.topAc = topAc;
	}

	public float getTop4() {
		return top4;
	}

	public void setTop4(float top4) {
		this.top4 = top4;
	}

	public float getTop4Ac() {
		return top4Ac;
	}

	public void setTop4Ac(float top4Ac) {
		this.top4Ac = top4Ac;
	}

	public float getTopG() {
		return topG;
	}

	public void setTopG(float topG) {
		this.topG = topG;
	}

//	public float getTopGA() {
//		return topGA;
//	}
//
//	public void setTopGA(float topGA) {
//		this.topGA = topGA;
//	}

	public float getTotDist() {
		return totDist;
	}

	public void setTotDist(float totDist) {
		this.totDist = totDist;
	}

	public double getTopX() {
		return topX;
	}

	public void setTopX(double topX) {
		this.topX = topX;
	}

	public double getTopY() {
		return topY;
	}

	public void setTopY(double topY) {
		this.topY = topY;
	}

	public double getTop4X() {
		return top4X;
	}

	public void setTop4X(double top4x) {
		top4X = top4x;
	}

	public double getTop4Y() {
		return top4Y;
	}

	public void setTop4Y(double top4y) {
		top4Y = top4y;
	}

	public ArrayList<PointData> getPts() {
		return pts;
	}

	public void setPts(ArrayList<PointData> pts) {
		this.pts = pts;
	}
	
	public void addPoint(PointData p) {
		if(pts != null)
			pts.add(p);
	}
	
	@Override
	public String toString(){
		return name + "\n" + DATE_F.format(time)
			+ "\ntop speed: " + Main.formatSpeed(top) + " \u00B1 " + Main.formatSpeed(topAc)
			+ "\ntop GPS speed: " + Main.formatSpeed(topG)
			+ "\ntop 4 points speed: " + Main.formatSpeed(top4) + " \u00B1 " + Main.formatSpeed(top4Ac)
			+ "\nduration: " + Main.formatTime(totTime) 
			+ "\ndistance: " + Main.formatDistance(totDist)
			+ "\naverage speed: " + Main.formatSpeed(totDist / (totTime / 1000));
	}
}
