package com.lemon.catacombs.engine.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AnimationSpace {
    private final Map<String, Animation> animations;
    private String current;
    private Animation currentAnimation;

    public AnimationSpace() {
        this.animations = new HashMap<>();
        this.current = "";
        this.currentAnimation = null;
    }

    public void addAnimation(String name, Animation animation) {
        this.animations.put(name, animation);
    }

    public void playAnimation(String name) {
        this.current = name;
        this.currentAnimation = this.animations.get(name);
    }

    public void update() {
        if (this.currentAnimation != null) {
            this.currentAnimation.update();
        }
    }

    public Sprite getFrame() {
        if (this.currentAnimation != null) {
            return this.currentAnimation.getFrame();
        }
        return null;
    }

    public void startAnimation(String name) {
        if (Objects.equals(name, current)) {
            return;
        }
        this.current = name;
        this.currentAnimation = this.animations.get(name);
        currentAnimation.start();
    }

    public void startAnimation(String name, int speed) {
        if (Objects.equals(name, current)) {
            return;
        }
        this.current = name;
        this.currentAnimation = this.animations.get(name);
        currentAnimation.start(speed);
    }

    public void reset() {
        for (Animation animation : this.animations.values()) {
            animation.reset();
        }
    }

    public Animation getAnimation() {
        return this.currentAnimation;
    }

}
