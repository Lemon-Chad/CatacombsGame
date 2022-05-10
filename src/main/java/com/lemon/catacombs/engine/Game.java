package com.lemon.catacombs.engine;

import com.lemon.catacombs.engine.input.KeyInput;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.input.MouseInput;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.Window;
import com.lemon.catacombs.items.*;
import com.lemon.catacombs.objects.Block;
import com.lemon.catacombs.objects.endless.CheckeredBackground;
import com.lemon.catacombs.objects.endless.InfinitySpawner;
import com.lemon.catacombs.objects.entities.enemies.Vessel;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.ui.FadeIn;
import com.lemon.catacombs.ui.MenuUI;
import com.lemon.catacombs.ui.PlayerHUD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

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
    private final AudioHandler audioHandler;
    private final DelayHandler delayHandler;

    private double delta = 0.0;

    private Player player;

    public Game() {
        instance = this;
        new Window(1000, 563, "Catacombs", this);

        handler = new Handler();
        addKeyListener(keyInput = new KeyInput());
        addMouseListener(mouseInput = new MouseInput());

        loader = new BufferedImageLoader();
        audioHandler = new AudioHandler();
        delayHandler = new DelayHandler();

        audioHandler.playSound("/sounds/item.wav", 0);
        loader.loadImage("/sprites/guns/pistol.png");

        menu();

        camera = new Camera(0, 0);

        start();
    }

    public static int playSound(String s) {
        return getInstance().audioHandler.playSound(s);
    }

    public static int playSound(String s, float volume) {
        return getInstance().audioHandler.playSound(s, volume);
    }

    public static int playSound(String s, float volume, boolean loop) {
        return getInstance().audioHandler.playSound(s, volume, loop);
    }

    public void reset() {
        handler.clear();
        keyInput.clear();
        mouseInput.clear();
        PlayerHUD.kills = 0;
        PlayerHUD.damage = 0;
    }

    public void menu() {
        reset();
        handler.addObject(new FadeIn(30));
        handler.addObject(new CheckeredBackground());
        handler.addObject(new MenuUI());
    }

    public void ui() {
        handler.addObject(new PlayerHUD(400, 30, 300, 15, 64));
    }

    public void endlessMode() {
        reset();
        ui();
        handler.addObject(new Player(0, 0));
        handler.addObject(new CheckeredBackground());
        handler.addObject(new InfinitySpawner());
        handler.addObject(Weapon.dropWeapon(Weapon.generateWeapon(), 0, 64));
        camera.setShake(3.0f);
        camera.setShakeDecayRate(0.99f);
        camera.setZoom(2.0f);
        camera.setZoomDecayRate(0.99f);
        Game.later(1000, () -> {
            camera.setShakeDecayRate(0.9f);
            camera.setZoomDecayRate(0.9f);
        });
    }

    public void testLevel() {
        BufferedImage level = loader.loadImage("/test_level.png");
        loadLevel(level);
        bounds = null;
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
                now = System.currentTimeMillis();
                delayHandler.tick(now - lastRender);
                lastRender = now;
                frames++;

                if (System.currentTimeMillis() - timer > 1_000) {
                    timer += 1_000;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }
        });
        actionTimer.setRepeats(true);
        actionTimer.setCoalesce(true);
        actionTimer.start();
    }

    public void tick() {
        keyInput.tick();
        mouseInput.tick();
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
            BufferedImage screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            try {
                g = screen.getGraphics();

                g.setColor(new Color(50, 50, 50));
                g.fillRect(0, 0, getWidth(), getHeight());

                Graphics2D g2d = (Graphics2D) g;
                double offsetX = getWidth() * (1 - camera.getZoom()) / 2;
                double offsetY = getHeight() * (1 - camera.getZoom()) / 2;
                g2d.scale(camera.getZoom(), camera.getZoom());
                g2d.translate(offsetX / camera.getZoom(), offsetY / camera.getZoom());
                g2d.translate(-camera.getX(), -camera.getY());

                handler.render(g);

                g2d.translate(camera.getX(), camera.getY());
                g2d.translate(-offsetX / camera.getZoom(), -offsetY / camera.getZoom());
                g2d.scale(1 / camera.getZoom(), 1 / camera.getZoom());

                handler.renderUI(g);

                camera.decayZoom();
                camera.decayShake();
            } finally {
                assert g != null;
                g.dispose();
            }

            try {
                g = bs.getDrawGraphics();

                BufferedImage shader = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = (Graphics2D) shader.getGraphics();
                g2d.drawImage(screen, 0, 0, null);

                g.drawImage(shader, 0, 0, null);
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
                    handler.addObject(new Block(xx * 32, yy * 32));
                } else if (red == 255 && green == 255 && blue == 0) {
                    handler.addObject(new Vessel(xx * 32, yy * 32));
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

    public Player getPlayer() {
        return player;
    }

    public AudioHandler getAudioHandler() {
        return audioHandler;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static void later(long delay, Runnable runnable) {
        getInstance().delayHandler.add(delay, runnable);
    }
}
