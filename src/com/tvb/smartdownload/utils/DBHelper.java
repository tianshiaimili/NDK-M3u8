package com.tvb.smartdownload.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "smartdownload.db";
	private static final int VERSION = 1;

	public static final String TABLE_DOWNLOAD_HISTORY = "download_history";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_M3U8_URL = "m3u8_url";
	public static final String COLUMN_TS_URL = "ts_url";
	public static final String COLUMN_STORAGE_PATH = "storage_path";
	public static final String COLUMN_PERCENTAGE = "percentage";
	public static final String COLUMN_DOWNLOADED_TS = "downloaded_ts";
	public static final String COLUMN_TOTAL_TS = "total_ts";
	public static final String COLUMN_STATE = "state";
	public static final String COLUMN_CREATE_DATE = "create_date";
	public static final String COLUMN_FINISHED_DATE = "finished_date";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_DOWNLOAD_HISTORY + "(" 
				+ COLUMN_ID + " integer primary key, " // autoincrement
				+ COLUMN_M3U8_URL + " varchar(200), " 
				+ COLUMN_TS_URL + " varchar(200), "
				+ COLUMN_STORAGE_PATH + " varchar(200), " 
				+ COLUMN_PERCENTAGE + " smallint, " 
				+ COLUMN_DOWNLOADED_TS + " smallint, "
				+ COLUMN_TOTAL_TS + " smallint, " 
				+ COLUMN_STATE + " smallint, " 
				+ COLUMN_CREATE_DATE + " timestamp), "
				+ COLUMN_FINISHED_DATE + " timestamp)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
