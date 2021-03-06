package com.lemon.catacombs.items.melee;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.MeleeRange;

public class ButterflyKnife extends MeleeWeapon {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/melee/knife.png").originFromUV();
    private final int damage;

    public ButterflyKnife() {
        super(Utils.intRange(5, 10));
        damage = Utils.intRange(90, 110);
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
