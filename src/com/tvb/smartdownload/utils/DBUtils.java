package com.tvb.smartdownload.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tvb.smartdownload.downloader.DownloadItem;

public class DBUtils {

	private static SQLiteDatabase DB;
	private static DBHelper HELPER;
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	private DBUtils() {
	}

	private static synchronized DBHelper getHelper(Context context) {
		if (HELPER == null) {
			HELPER = new DBHelper(context);
		}
		return HELPER;
	}

	public static synchronized void saveDownloadHistory(Context context, DownloadItem item) {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.COLUMN_ID, item.getId());
		cv.put(DBHelper.COLUMN_M3U8_URL, item.getM3u8Url());
		cv.put(DBHelper.COLUMN_TS_URL, item.getTsUrl());
		cv.put(DBHelper.COLUMN_STORAGE_PATH, item.getStoragePath());
		cv.put(DBHelper.COLUMN_PERCENTAGE, item.getPercentage());
		cv.put(DBHelper.COLUMN_DOWNLOADED_TS, item.getDownloadedTs());
		cv.put(DBHelper.COLUMN_TOTAL_TS, item.getTotalTs());
		cv.put(DBHelper.COLUMN_STATE, item.getState());
		cv.put(DBHelper.COLUMN_CREATE_DATE, SDF.format(item.getCreateDate()));
		if (item.getFinishedDate() != null) {
			cv.put(DBHelper.COLUMN_FINISHED_DATE, SDF.format(item.getFinishedDate()));
		}
		DB = getHelper(context).getWritableDatabase();
		DB.insert(DBHelper.TABLE_DOWNLOAD_HISTORY, null, cv);
		DB.close();
	}

	public static synchronized int updateDownloadHistory(Context context, DownloadItem item) {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.COLUMN_PERCENTAGE, item.getPercentage());
		cv.put(DBHelper.COLUMN_DOWNLOADED_TS, item.getDownloadedTs());
		cv.put(DBHelper.COLUMN_TOTAL_TS, item.getTotalTs());
		cv.put(DBHelper.COLUMN_STATE, item.getState());
		if (item.getFinishedDate() != null) {
			cv.put(DBHelper.COLUMN_FINISHED_DATE, SDF.format(item.getFinishedDate()));
		}
		DB = getHelper(context).getWritableDatabase();
		int affected = DB.update(DBHelper.TABLE_DOWNLOAD_HISTORY, cv, DBHelper.COLUMN_ID + "=?", new String[] { String.valueOf(item.getId()) });
		DB.close();
		return affected;
	}

	public static synchronized DownloadItem findDownloadHistoryById(Context context, int id) {
		DB = getHelper(context).getReadableDatabase();
		Cursor c = DB.query(DBHelper.TABLE_DOWNLOAD_HISTORY, new String[] {
				DBHelper.COLUMN_ID, DBHelper.COLUMN_M3U8_URL,
				DBHelper.COLUMN_TS_URL, DBHelper.COLUMN_STORAGE_PATH,
				DBHelper.COLUMN_PERCENTAGE, DBHelper.COLUMN_DOWNLOADED_TS,
				DBHelper.COLUMN_TOTAL_TS, DBHelper.COLUMN_STATE,
				DBHelper.COLUMN_CREATE_DATE, DBHelper.COLUMN_FINISHED_DATE },
				DBHelper.COLUMN_ID + "=?", new String[] { String.valueOf(id) },
				null, null, null);

		DownloadItem item = null;
		if (c.moveToFirst()) {
			item = new DownloadItem();
			item.setId(c.getInt(c.getColumnIndex(DBHelper.COLUMN_ID)));
			item.setM3u8Url(c.getString(c.getColumnIndex(DBHelper.COLUMN_M3U8_URL)));
			item.setTsUrl(c.getString(c.getColumnIndex(DBHelper.COLUMN_TS_URL)));
			item.setStoragePath(c.getString(c.getColumnIndex(DBHelper.COLUMN_STORAGE_PATH)));
			item.setPercentage(c.getInt(c.getColumnIndex(DBHelper.COLUMN_PERCENTAGE)));
			item.setDownloadedTs(c.getInt(c.getColumnIndex(DBHelper.COLUMN_DOWNLOADED_TS)));
			item.setTotalTs(c.getInt(c.getColumnIndex(DBHelper.COLUMN_TOTAL_TS)));
			item.setState(c.getInt(c.getColumnIndex(DBHelper.COLUMN_STATE)));
			try {
				item.setCreateDate(SDF.parse(c.getString(c.getColumnIndex(DBHelper.COLUMN_CREATE_DATE))));
				item.setFinishedDate(SDF.parse(c.getString(c.getColumnIndex(DBHelper.COLUMN_FINISHED_DATE))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		c.close();
		DB.close();
		return item;
	}

	public static synchronized void saveOrUpdateDownloadHistory(Context context, DownloadItem item) {
		DownloadItem downloadItem = findDownloadHistoryById(context, item.getId());
		if (downloadItem == null) {
			saveDownloadHistory(context, item);
		} else {
			updateDownloadHistory(context, item);
		}
	}

	public static synchronized int deleteDownloadHistory(Context context, int id) {
		DB = getHelper(context).getWritableDatabase();
		int affected = DB.delete(DBHelper.TABLE_DOWNLOAD_HISTORY, DBHelper.COLUMN_ID + "=?", new String[] { String.valueOf(id) });
		DB.close();
		return affected;
	}

	public static synchronized int deleteAllDownloadHistory(Context context) {
		DB = getHelper(context).getWritableDatabase();
		int affected = DB.delete(DBHelper.TABLE_DOWNLOAD_HISTORY, null, null);
		DB.close();
		return affected;
	}

}
