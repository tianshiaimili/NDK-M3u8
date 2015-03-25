package com.tvb.smartdownload.utils;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class DevicesUtils {

	/**
	 * if has sdcard
	 * @return
	 */
	public static boolean hasSDCardMounted() {
		String state = Environment.getExternalStorageState();
		if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * 
	 */
	public static int getSDCardFreeSize() {
		// 取得sdcard文件路径
		File pathFile = android.os.Environment.getExternalStorageDirectory();
		android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
		// 获取SDCard上BLOCK总数
		long nTotalBlocks = statfs.getBlockCount();
		// 获取SDCard上每个block的SIZE
		long nBlocSize = statfs.getBlockSize();
		// 获取可供程序使用的Block的数量
		long nAvailaBlock = statfs.getAvailableBlocks();
		// 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
		long nFreeBlock = statfs.getFreeBlocks();
		// 计算SDCard 总容量大小MB
		long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
		// 计算 SDCard 剩余大小MB
		long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
		return (int) nSDFreeSize;
	}
	
	/**
	 * 
	 */

	public static  boolean isWiFiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo info : infos) {
					if (ConnectivityManager.TYPE_WIFI == info.getType() && info.isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
}
