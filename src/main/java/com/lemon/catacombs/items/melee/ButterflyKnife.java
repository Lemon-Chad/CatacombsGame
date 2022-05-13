package com.lemon.catacombs.items.melee;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.MeleeRange;

public class ButterflyKnife extends MeleeWeapon {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/melee/knife.png");
    private final int damage;

    public ButterflyKnife() {
        super((int) Utils.range(5, 10));
        damage = (int) Utils.range(90, 110);
    }

    @Override
    public int meleeDamage() {
        return damage;
    }

    @Override
    public MeleeRange meleeRange() {
        return new MeleeRange(0.5, 0.5);
    }

    @Override
    public boolean isDual() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public float getScale() {
        return 1f;
    }
}
