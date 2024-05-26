package com.zerozone.vintage.image;

import java.io.IOException;

public interface ImageStorageService {
    String saveImage(String imageData) throws IOException;
}