package com.lemon.catacombs.items.melee;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.MeleeRange;

import java.awt.*;

public class Daggers extends MeleeWeapon {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/melee/dagger.png").originFromUV();
    private final int damage;

    public Daggers() {
        super(Utils.intRange(30, 40));
        damage = Utils.intRange(25, 35);
    }

    @Override
    public int meleeDamage() {
        return damage;
    }

    @Override
    public MeleeRange meleeRange() {
        return new MeleeRange(1.5, 1);
    }

    @Override
    public boolean isDual() {
        return true;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public float getScale() {
        return 1.3f;
    }
}
