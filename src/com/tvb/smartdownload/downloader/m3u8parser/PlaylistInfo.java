package com.tvb.smartdownload.downloader.m3u8parser;

/**
 * Contains information about playlist element.
 */
public interface PlaylistInfo {
    public int getProgramId();

    public int getBandWitdh();

    public String getCodecs();

    public String getResolution();
}
