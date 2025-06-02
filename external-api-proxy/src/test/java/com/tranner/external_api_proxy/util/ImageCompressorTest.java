package com.tranner.external_api_proxy.util;

import com.tranner.external_api_proxy.common.util.ImageUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ImageCompressorTest {

    @Test
    void testCompressToWebP() throws IOException {
        // 테스트용 이미지 준비 (JPEG 또는 PNG)
        byte[] originalImage = Files.readAllBytes(Path.of("src/test/resources/sample.jpg"));

        // 압축 수행
        byte[] webpImage = ImageUtil.compressToWebP(originalImage);

        // 검증
        assertNotNull(webpImage);
        assertTrue(webpImage.length > 0);
        assertTrue(webpImage.length < originalImage.length); // 보통 webp가 더 작음

        // 출력 확인 (선택)
        Files.write(Path.of("src/test/resources/output.webp"), webpImage);
    }

}
