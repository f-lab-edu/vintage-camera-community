package com.zerozone.vintage.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

public class ImageUtils {

    private static final Path rootLocation = Paths.get("uploaded-profile-images"); // 이미지를 저장할 서버 내 폴더 경로 지정

    public static String saveImage(String imageData) {
        try {
            ensureDirectoryExists(rootLocation);

            String[] parts = imageData.split(",");
            String mediaType = parts[0].split(";")[0].split(":")[1]; // MIME 타입 추출
            String extension = getExtensionFromMediaType(mediaType); // MIME 타입에 기반한 파일 확장자
            byte[] imageBytes = Base64.getDecoder().decode(parts[1]); // base64 인코딩된 이미지 데이터를 디코딩
            String imageName = UUID.randomUUID().toString() + extension; // 랜덤 UUID와 결정된 확장자를 사용하여 파일 이름 생성

            // 이미지 파일을 저장할 최종 경로를 설정
            Path destinationFile = rootLocation.resolve(Paths.get(imageName))
                    .normalize().toAbsolutePath(); // 절대 경로

            // 생성된 경로에 이미지 데이터를 파일로 쓰기
            Files.write(destinationFile, imageBytes);

            return imageName; // 저장된 파일의 이름을 반환
        } catch (IOException e) {
            throw new RuntimeException("이미지 폴더 저장에 실패 했습니다. ", e);
        }
    }

    private static String getExtensionFromMediaType(String mediaType) {
        switch (mediaType) {
            case "image/jpeg":
                return ".jpeg";
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            default:
                throw new IllegalArgumentException("허용하지 않는 타입입니다. : " + mediaType);
        }
    }

    private static void ensureDirectoryExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}