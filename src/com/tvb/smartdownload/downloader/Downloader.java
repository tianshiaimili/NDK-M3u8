package com.tvb.smartdownload.downloader;

public class Downloader {

	public static final int INFO_DOWNLOAD_COMPLETED = 101;
	public static final int INFO_DOWNLOAD_PROGRESS = 102;
	public static final int INFO_LOWSPACE = 103;
	public static final int INFO_NETWORK_BACK = 104;
	public static final int INFO_NETWORK_DOWN = 105;
	public static final int INFO_OFFLINE_KEYS_RENEWAL_COMPLETED = 106;
	public static final int INFO_OFFLINE_KEYS_RENEWAL_STARTED = 107;
	public static final int INFO_OFFLINE_NOT_ALLOWED = 108;
	public static final int INFO_UNKNOWN = 109;

	public static final int ERROR_BAD_URL = 110;
	public static final int ERROR_LIVE_UNSUPPORTTED = 111;
	public static final int ERROR_NETWORK_BROKEN = 112;
	public static final int ERROR_NO_MORE_SPACE = 113;
	public static final int ERROR_OFFLINE_NOT_ALLOWED = 114;
	public static final int ERROR_UNKNOWN = 1;
	public static final int ERROR_VERSION_UNSUPPORTTED = 115;

	public Downloader() {
	}

}
