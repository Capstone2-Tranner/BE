package com.tranner.external_api_proxy.type;

public enum PhotoSize {
    MINI("dynamic/mini", 200, 60 * 60),        // 1시간
    MIDDLE("dynamic/middle", 800, 24 * 60 * 60), // 1일
    DETAIL("dynamic/detail", 1600, 7 * 24 * 60 * 60); // 7일

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

