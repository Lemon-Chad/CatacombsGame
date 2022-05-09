package com.lemon.catacombs.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BufferedImageLoader {
    private static final Map<String, BufferedImage> loadedImages = new HashMap<>();
    private BufferedImage image;

    public BufferedImage loadImage(String path) {
        if (loadedImages.containsKey(path)) {
            return loadedImages.get(path);
        }
        try {
            URL url = getClass().getResource(path);
            assert url != null;
            image = ImageIO.read(url);
            loadedImages.put(path, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
