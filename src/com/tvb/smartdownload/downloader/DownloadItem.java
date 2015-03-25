package com.tvb.smartdownload.downloader;

import java.util.Date;

public class DownloadItem {

	public static final int STATE_IDLE = 0;
	public static final int STATE_WAITTING = 1;
	public static final int STATE_PREPAREING = 2;
	public static final int STATE_STARTED = 3;
	public static final int STATE_STOPPED = 4;
	public static final int STATE_FINISHED = 5;

	private int id;
	private String m3u8Url;
	private String tsUrl;
	private String storagePath;
	private int percentage;
	private int downloadedTs;
	private int totalTs;
	private int state;
	private Date createDate;
	private Date finishedDate;

	public DownloadItem() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getM3u8Url() {
		return m3u8Url;
	}

	public void setM3u8Url(String m3u8Url) {
		this.m3u8Url = m3u8Url;
	}

	public String getTsUrl() {
		return tsUrl;
	}

	public void setTsUrl(String tsUrl) {
		this.tsUrl = tsUrl;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public int getDownloadedTs() {
		return downloadedTs;
	}

	public void setDownloadedTs(int downloadedTs) {
		this.downloadedTs = downloadedTs;
	}

	public int getTotalTs() {
		return totalTs;
	}

	public void setTotalTs(int totalTs) {
		this.totalTs = totalTs;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

	@Override
	public String toString() {
		return "DownloadItem [id=" + id + ", m3u8Url=" + m3u8Url + ", tsUrl="
				+ tsUrl + ", storagePath=" + storagePath + ", percentage="
				+ percentage + ", downloadedTs=" + downloadedTs + ", totalTs="
				+ totalTs + ", state=" + state + ", createDate=" + createDate
				+ ", finishedDate=" + finishedDate + "]";
	}

}
