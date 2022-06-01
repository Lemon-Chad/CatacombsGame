package com.lemon.catacombs.engine;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.input.KeyInput;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.input.MouseInput;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.Window;
import com.lemon.catacombs.generation.Dungeon;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.items.effects.*;
import com.lemon.catacombs.items.guns.TestGun;
import com.lemon.catacombs.items.guns.rifles.FrostbiteRifle;
import com.lemon.catacombs.objects.rooms.Crate;
import com.lemon.catacombs.objects.rooms.Pit;
import com.lemon.catacombs.objects.rooms.Wall;
import com.lemon.catacombs.objects.endless.CheckeredBackground;
import com.lemon.catacombs.objects.endless.InfinitySpawner;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.entities.enemies.Vessel;
import com.lemon.catacombs.objects.ui.FadeIn;
import com.lemon.catacombs.objects.ui.cutscenes.opening.OpeningCutscene;
import com.lemon.catacombs.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final AudioHandler audioHandler;
    private final DelayHandler delayHandler;

    private double delta = 0.0;

    private final float gameSpeed = 60;
    private float physicsSpeed = 1f;
    private float physicsSpeedDecay = 0.90f;

    private Player player;

    public Game() {
        hideCursor();

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

        camera = new Camera(0, 0);

//        menu();
        playground();
//        generationTest();
//        openingCutscene();

        start();
    }

    public void hideCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);
    }

    public void showCursor() {
        Cursor cursor = Cursor.getDefaultCursor();
        setCursor(cursor);
    }

    public void openingCutscene() {
        handler.addObject(new FadeIn(30));
        handler.addObject(new OpeningCutscene(0));
    }

    public void generationTest() {
        reset();
        ui();
        Dungeon dungeon = new Dungeon(10, 128, 8, 12);
        dungeon.generate();
        dungeon.build(0.15f);
        Point playerSpawn = dungeon.getStart();
        player = new Player(playerSpawn.x, playerSpawn.y);
        handler.addObject(player);
        handler.addObject(Weapon.dropWeapon(Weapon.generateShotgun(), playerSpawn.x, playerSpawn.y + 32));
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
        Stats.getStats().reset();
        handler.addObject(new Reticle());
    }

    public void menu() {
        reset();
        camera.setX(0);
        camera.setY(0);
        handler.addObject(new OpeningCutscene(OpeningCutscene.end));
        handler.addObject(new MenuUI());
    }

    public void ui() {
        handler.addObject(new PlayerHUD(400, 30, 300, 15, 64));
        handler.addObject(new Ribbon(15, 96) {
            private int count = 0;

            @Override
            public boolean isTriggered() {
                int newCount = Stats.getStats().getEffectSlots();
                if (newCount != count) {
                    count = newCount;
                    return true;
                }
                return false;
            }

            @Override
            public String getText() {
                return "NEW EFFECT SLOT";
            }

            @Override
            public Color getColor() {
                return new Color(0, 149, 255);
            }

            @Override
            public int getDuration() {
                return 120;
            }

            @Override
            public int getTransition() {
                return 20;
            }

            @Override
            public Font getFont() {
                int boldItalic = Font.BOLD | Font.ITALIC;
                return new Font("Arial", boldItalic, 48);
            }

            @Override
            public float interpolate(float x) {
                return 1 / (1 + (float) Math.exp(5 - 10 * x));
            }
        });
    }

    public void endlessMode() {
        reset();
        ui();
        handler.addObject(new Player(0, 0));
        handler.addObject(new CheckeredBackground());
        handler.addObject(new InfinitySpawner());
        handler.addObject(Weapon.dropWeapon(new FrostbiteRifle(), 0, 64));
        camera.setShake(3.0f);
        camera.setShakeDecayRate(0.99f);
        camera.setZoom(2.0f);
        camera.setZoomDecayRate(0.99f);
        Game.later(1000, () -> {
            camera.setShakeDecayRate(0.9f);
            camera.setZoomDecayRate(0.9f);
        });
    }

    private void playground() {
        reset();
        ui();
        handler.addObject(new Player(0, 0));
        handler.addObject(new CheckeredBackground());
        handler.addObject(new Wall(128, 0, 16, 128));
        Vessel vessel = new Vessel(64, 0);
        handler.addObject(vessel);
    }

    private void weaponSpawn() {
        handler.addObject(Weapon.dropWeapon(Weapon.generateWeapon(), 0, 64));
        Game.later(10_000, this::weaponSpawn);
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
            final double ns = 1_000_000_000 / gameSpeed;
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
        Stats.getStats().tick();
        decayPhysicsSpeed();
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

                g.setColor(new Color(0, 0, 0));
                g.fillRect(0, 0, getWidth(), getHeight());

                Graphics2D g2d = (Graphics2D) g;
                Utils.scale(g2d, camera.getZoom(), getWidth() / 2f, getHeight() / 2f);
                g2d.translate(-camera.getX(), -camera.getY());

                handler.render(g);

                g2d.translate(camera.getX(), camera.getY());
                Utils.unscale(g2d, camera.getZoom(), getWidth() / 2f, getHeight() / 2f);

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
                    handler.addObject(new Wall(xx * 32, yy * 32, 32, 32));
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

    public static Set<GameObject> objectsIn(Shape polygon, Set<Integer> mask) {
        return getInstance().handler.objectsIn(polygon, mask);
    }

    public float getPhysicsSpeed() {
        return physicsSpeed;
    }

    public void setPhysicsSpeed(float physicsSpeed) {
        this.physicsSpeed = physicsSpeed;
    }

    public void setPhysicsSpeedDecay(float physicsSpeedDecay) {
        this.physicsSpeedDecay = physicsSpeedDecay;
    }

    public void decayPhysicsSpeed() {
        physicsSpeed = 1 + (physicsSpeed - 1) * physicsSpeedDecay;
    }

    public String[] listFiles(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            return new String[0];
        }
        try {
            Path p = Paths.get(url.toURI());
            // Get only file names
            return Files.list(p).map(Path::getFileName).map(Path::toString).toArray(String[]::new);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static Font getFont(String path) {
        Font font = loadFont(path);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font;
    }

    public static Font getFont(String s, int bold, int i) {
        Font font = getFont(s);
        return font.deriveFont(bold, i);
    }

    public static Font loadFont(String path) {
        try {
            InputStream is = Game.class.getResourceAsStream(path);
            assert is != null;
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
