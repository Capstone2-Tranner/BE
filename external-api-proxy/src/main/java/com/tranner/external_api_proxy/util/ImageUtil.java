package com.tranner.external_api_proxy.util;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static byte[] compressToWebP(byte[] originalImage) {
        try {
            ImmutableImage image = ImmutableImage.loader().fromBytes(originalImage);

            WebpWriter webpWriter = new WebpWriter(); // 손실 압축 (기본값)

            // ByteArrayOutputStream으로 압축 결과 받기
            return image.bytes(webpWriter); // 이게 핵심! writer로 webp 변환해서 byte[] 반환

        } catch (IOException e) {
            throw new RuntimeException("WebP 압축 실패", e);
        }
    }

}
