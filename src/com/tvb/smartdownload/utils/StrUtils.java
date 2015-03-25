package com.tvb.smartdownload.utils;

import java.io.File;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import com.tvb.smartdownload.Constants;

public class StrUtils {

	private static final String URL_UP_ONE_LEVEL = "../";
	private static final char URL_SEPARATOR = '/';

	private StrUtils() {
	}

	public static String makeAbsoluteUrl(String url, String uri) {
		if (url != null && uri != null) {
			if (URI.create(uri).isAbsolute()) {
				return uri;
			} else if (URI.create(url).isAbsolute()) {
				if (url.contains("?")) {
					url = url.substring(0, url.indexOf("?"));
				}
				if (url.charAt(url.length() - 1) != URL_SEPARATOR) {
					url = url.substring(0, url.lastIndexOf(URL_SEPARATOR) + 1);
				}
				if (uri.charAt(0) == URL_SEPARATOR) {
					return url + uri.substring(1, uri.length());
				} else if (uri.startsWith(URL_UP_ONE_LEVEL)) {
					while (uri.startsWith(URL_UP_ONE_LEVEL)) {
						url = url.substring(0, url.substring(0, url.length() - 1).lastIndexOf(URL_SEPARATOR) + 1);
						uri = uri.substring(URL_UP_ONE_LEVEL.length(), uri.length());
					}
					return url + uri;
				} else {
					return url + uri;
				}
			}
		}
		return null;
	}

	public static String getFilename(String url) {
		if (url != null) {
			int startIndex = url.lastIndexOf(URL_SEPARATOR) + 1;
			if (url.contains(".m3u8")) {
				int endIndex = url.toLowerCase(Locale.CHINA).lastIndexOf(".m3u8");
				if (startIndex > 0 && endIndex > 0 && startIndex < endIndex) {
					return url.substring(startIndex, endIndex + 5);
				}
			} else if (url.contains(".ts")) {
				int endIndex = url.toLowerCase(Locale.CHINA).lastIndexOf(".ts");
				if (startIndex > 0 && endIndex > 0 && startIndex < endIndex) {
					return url.substring(startIndex, endIndex + 3);
				}
			}
		}
		return null;
	}

	public static String md5(String message) {
		if (message != null) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.update(message.getBytes());
				StringBuilder builder = new StringBuilder();
				for (byte b : messageDigest.digest()) {
					builder.append(Integer.toHexString(b & 0xff));
				}
				return builder.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public static String getDownloadPath(String url,String downLoadPath) {
		if (url != null) {
			return downLoadPath + File.separator + md5(url);
		}
		return null;
	}
	
	
	public static String getDownloadM3U8File(String ip,String url,String fileName) {
		if (url != null) {
//			return Constants.DOWNLOAD_ROOT_PATH + File.separator + md5(url) + File.separator  + Constants.DOWNLOAD_INDEX_M3U8_FILENAME;
			return ip + File.separator + md5(url) +File.separator  + fileName;
		}
		return null;
	}
	
}
