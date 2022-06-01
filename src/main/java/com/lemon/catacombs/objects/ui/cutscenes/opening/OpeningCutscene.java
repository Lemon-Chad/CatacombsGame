package com.lemon.catacombs.objects.ui.cutscenes.opening;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Animation;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.ID;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OpeningCutscene extends GameObject {
    private static final int planetSceneWidth = 2500;
    public static final int end = 4400;
    public static final List<Planet> planets = makePlanets(75);
    public static final List<Planet> backPlanets = makePlanets(-50);
    public static final Set<Point> stars = makeStars();
    public static final Font font = Game.getFont("/fonts/forward.TTF", Font.BOLD, 24);
    private final TextEvent[] textEvents;
    private final Animation sun;
    private final Animation computer;
    private final boolean menu;
    private boolean skipped;
    private int time;

    public OpeningCutscene(int t) {
        super(0, 0, ID.UI);
        time = t;
        x = Math.max(-time, -planetSceneWidth);
        menu = time > 0;
        Game.onKeyPressed(KeyEvent.VK_ENTER, e -> {
            if (!skipped) {
                skipped = true;
                time = Math.max(time, end);
            }
        });

        sun = Animation.LoadSpriteSheet("/sprites/cutscene/star.png", 2, 2).setSpeed(500);
        sun.play();

        computer = Animation.LoadSpriteSheet("/sprites/cutscene/computer.png", 7, 32, 32).setSpeed(250);
        computer.play();

        textEvents = new TextEvent[]{
                new TextEvent(50, 50, 50, "Thousands of years in the future,", 600),
                new TextEvent(50, 100, 250, "You exist as the only living human left.", 400),
                new TextEvent(50, 50, 700, "After inventing a godlike machine,", 800),
                new TextEvent(50, 100, 900, "All of humanity merged into a simulation.", 600),
                new TextEvent(50, 150, 1300, "Except for one.", 600),
                new TextEvent(50, 50, 2300, "The supercomputer is an impenetrable fortress.", 600),
                new TextEvent(50, 100, 2550, "However, you know another way in.", 600),
                new TextEvent(50, 150, 2800, "You have the power to destroy it.", 600),
                new TextEvent(150, 150, 3400, "Your journey begins here,", 600),
                new TextEvent(150, 200, 3600, "Fighting endless generated security systems", 600),
                new TextEvent(150, 250, 3800, "and trying to destroy the supercomputer.", 600),
        };
    }

    private static List<Planet> makePlanets(int start) {
        List<Planet> planets = new LinkedList<>();
        for (int x = start; x < planetSceneWidth; x += 500) {
            planets.add(new Planet(x, Utils.intRange(75, 250)));
        }
        return planets;
    }

    private static Set<Point> makeStars() {
        Set<Point> stars = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            int starX = Utils.intRange(0, planetSceneWidth + Game.getInstance().getWidth());
            int starY = Utils.intRange(0, Game.getInstance().getHeight());
            stars.add(new Point(starX, starY));
        }
        return stars;
    }

    @Override
    public void tick() {
        super.tick();
        time++;
        x = Math.max(-time, -planetSceneWidth);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        for (Point star : stars) {
            int offset = 31 * (23 * star.x + star.y);
            int alpha = (int) (Math.sin(offset + time / 80.0) * 127 + 127);
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillOval(x / 5 + star.x, star.y, 1, 1);
        }

        for (Planet planet : backPlanets) {
            planet.render(g, x / 2, y, 100);
        }

        for (Planet planet : planets) {
            planet.render(g, x, y, 0);
        }

        sun.update();
        computer.update();

        Sprite sprite = sun.getFrame();
        Graphics2D g2d = (Graphics2D) g.create();
        int ox = x + planetSceneWidth + Game.getInstance().getWidth() / 2 - 480;
        int oy = y + Game.getInstance().getHeight() / 2 - 480;
        g2d.rotate(-time / 10_000f, ox + 480, oy + 480);
        sprite.render(g2d, ox, oy, 960, 960);
        g2d.dispose();

        sprite = computer.getFrame();
        g2d = (Graphics2D) g.create();
        ox = x + planetSceneWidth + Game.getInstance().getWidth() / 2 - 160;
        oy = y + Game.getInstance().getHeight() / 2 - 160;
        g2d.rotate(time / 1000f, ox + 160, oy + 160);
        sprite.render(g2d, ox, oy, 320, 320);
        g2d.dispose();

        g.setFont(font);
        for (TextEvent textEvent : textEvents) {
            if (time > textEvent.triggerTime && time < textEvent.triggerTime + textEvent.duration) {
                int alpha = (int) (255 * Math.min(1, (textEvent.triggerTime + textEvent.duration - time) / 30f));
                String text = textEvent.text.substring(0, Math.min(textEvent.text.length(), (time - textEvent.triggerTime) / 5));
                g.setColor(new Color(0, 0, 0, alpha / 2));
                g.drawString(text, textEvent.x + 4, textEvent.y + 4);
                g.setColor(new Color(255, 255, 255, alpha));
                g.drawString(text, textEvent.x, textEvent.y);
            }
        }

        Game.getInstance().getCamera().setZoom(Math.min(Math.max(1, 1 + (time - 2500) / 10_000f), 1.15f));
        if (!menu && time >= end) {
            Game.getInstance().menu();
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public void collision(GameObject other) {

    }
}
