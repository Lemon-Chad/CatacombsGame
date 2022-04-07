package com.lemon.catacombs.engine;

import com.lemon.catacombs.engine.input.KeyInput;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.input.MouseInput;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.TileSet;
import com.lemon.catacombs.engine.render.Window;
import com.lemon.catacombs.engine.physics.Handler;
import com.lemon.catacombs.objects.Block;
import com.lemon.catacombs.objects.Enemy;
import com.lemon.catacombs.objects.Player;
import com.lemon.catacombs.objects.TileSets;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class Game extends Canvas implements Runnable {
    private static final long serialVersionUID = 1L;
    private static Game instance;

    private boolean isRunning = false;
    private Thread thread;
    private Timer actionTimer;
    private Rectangle bounds;

    private final Handler handler;
    private final KeyInput keyInput;
    private final MouseInput mouseInput;
    private final Camera camera;

    private final BufferedImageLoader loader;

    private double delta = 0.0;

    public Player player;

    public Game() {
        instance = this;
        new Window(1000, 563, "Catacombs", this);

        handler = new Handler();
        addKeyListener(keyInput = new KeyInput());
        addMouseListener(mouseInput = new MouseInput());

        loader = new BufferedImageLoader();
        BufferedImage level = loader.loadImage("/test_level.png");
        loadLevel(level);

        camera = new Camera(0, 0);

        start();
    }

    public static BufferedImage loadImage(String ref) {
        return getInstance().loader.loadImage(ref);
    }

    public static double delta() {
        return getInstance().delta / 1000;
    }

    private void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        isRunning = false;
        try {
            actionTimer.stop();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        requestFocus();
        actionTimer = new Timer(10, new ActionListener() {
            long lastTime = System.nanoTime();
            final double amountOfTicks = 60.0;
            final double ns = 1_000_000_000 / amountOfTicks;
            long timer = System.currentTimeMillis();
            long lastRender = System.currentTimeMillis();
            int frames = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    stop();
                    return;
                }

                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {
                    tick();
                    delta--;
                }
                render();
                lastRender = System.currentTimeMillis();
                frames++;

                if (System.currentTimeMillis() - timer > 1_000) {
                    timer += 1_000;
                    frames = 0;
                }
            }
        });
        actionTimer.setRepeats(true);
        actionTimer.setCoalesce(true);
        actionTimer.start();
    }

    public void tick() {
        handler.tick();
        handler.collisions();
        camera.tick(player);
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = null;
        do {
            try {
                g = bs.getDrawGraphics();

                g.setColor(new Color(50, 50, 50));
                g.fillRect(0, 0, getWidth(), getHeight());

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(-camera.getX(), -camera.getY());

                handler.render(g);

                g2d.translate(camera.getX(), camera.getY());
            } finally {
                assert g != null;
                g.dispose();
            }
            bs.show();
        } while (bs.contentsLost());
    }

    private void loadLevel(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        bounds = new Rectangle(32 * w, 32 * h);

        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                if (red == 0 && green == 0 && blue == 255) {
                    handler.addObject(new Player(xx * 32, yy * 32));
                } else if (red == 255 && green == 0 && blue == 0) {
                    TileSets.StoneBrick.placeTile(xx * 32, yy * 32);
                } else if (red == 255 && green == 255 && blue == 0) {
                    handler.addObject(new Enemy(xx * 32, yy * 32));
                }
            }
        }
    }

    public Rectangle getMap() {
        return bounds;
    }

    public static void main(String[] args) {
        new Game();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public static void onKeyPressed(int c, KeyInput.EventHandler handler) {
        getInstance().keyInput.onKeyPressed(c, handler);
    }

    public static void onKeyReleased(int c, KeyInput.EventHandler handler) {
        getInstance().keyInput.onKeyReleased(c, handler);
    }

    public static void onMouseEvent(MouseEvents event, MouseInput.EventHandler handler) {
        getInstance().mouseInput.addEventHandler(event, handler);
    }

    public Handler getWorld() {
        return handler;
    }

    public Camera getCamera() {
        return camera;
    }
}
