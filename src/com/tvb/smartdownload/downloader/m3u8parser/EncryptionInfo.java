package com.tvb.smartdownload.downloader.m3u8parser;

import java.net.URI;

/**
 * Contains information about media encryption.
 */
public interface EncryptionInfo {
    public URI getURI();

    public String getMethod();
}
