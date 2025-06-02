package com.tranner.external_api_proxy.common.type;

public enum PhotoSize {
    MINI("dynamic/mini", 137, 60 * 60),        // 1시간
    MIDDLE("dynamic/middle", 227, 12 * 60 * 60), // 12시간
    DETAIL("dynamic/detail", 438,  6 * 60 * 60); // 6시간

    private final String s3Path;
    private final int maxWidth;
    private final long ttlSeconds;

    PhotoSize(String s3Path, int maxWidth, long ttlSeconds) {
        this.s3Path = s3Path;
        this.maxWidth = maxWidth;
        this.ttlSeconds = ttlSeconds;
    }

    public String getS3Path() { return s3Path; }
    public int getMaxWidth() { return maxWidth; }
    public long getTtlSeconds() { return ttlSeconds; }
}

