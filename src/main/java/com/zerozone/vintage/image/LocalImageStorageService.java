package com.zerozone.vintage.image;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private static final Path rootLocation = Paths.get("uploaded-profile-images");

    @PostConstruct
    public void init() {
        try {
            ensureDirectoryExists(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create image upload directory", e);
        }
    }

    @Override
    public String saveImage(String imageData) throws IOException {
        String[] parts = imageData.split(",");
        String mediaType = parts[0].split(";")[0].split(":")[1];
        String extension = getExtensionFromMediaType(mediaType);
        byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
        String imageName = UUID.randomUUID().toString() + extension;

        Path destinationFile = rootLocation.resolve(Paths.get(imageName))
                .normalize().toAbsolutePath();

        Files.write(destinationFile, imageBytes);

        return imageName;
    }

    private String getExtensionFromMediaType(String mediaType) {
        return switch (mediaType) {
            case "image/jpeg" -> ".jpeg";
            case "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            default -> throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        };
    }

    private void ensureDirectoryExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}