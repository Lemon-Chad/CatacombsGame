package com.lemon.catacombs.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class BufferedImageLoader {
    private BufferedImage image;

    public BufferedImage loadImage(String path) {
        try {
            URL url = getClass().getResource(path);
            assert url != null;
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
