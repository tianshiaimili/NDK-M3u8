package com.tvb.smartdownload.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	private static final int TEMP_SIZE = 1024;

	private FileUtils() {
	}

	public static boolean writeToFile(String path, InputStream in) {
		OutputStream out = null;
		try {
			if (path != null && !"".equals(path) && in != null) {
				out = new FileOutputStream(new File(path));
				byte[] temp = new byte[TEMP_SIZE];
				int size = 0;
				while ((size = in.read(temp)) != -1) {
					out.write(temp, 0, size);
				}
				out.flush();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean writeToFile(String path, byte[] data) {
		return writeToFile(path, new ByteArrayInputStream(data));
	}

	public static byte[] getFile(InputStream in) {
		ByteArrayOutputStream out = null;
		try {
			if (in != null) {
				out = new ByteArrayOutputStream();
				byte[] temp = new byte[TEMP_SIZE];
				int size = 0;
				while ((size = in.read(temp)) != -1) {
					out.write(temp, 0, size);
				}
				out.flush();
				return out.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static byte[] getFile(String path) {
		if (path != null && !"".equals(path)) {
			try {
				return getFile(new FileInputStream(new File(path)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean deleteFile(String path) {
		if (path != null && !"".equals(path)) {
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				return file.delete();
			}
		}
		return false;
	}

	public static boolean deleteAllFile(String path) {
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}

		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteAllFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		return dirFile.delete();
	}

}
