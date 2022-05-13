package com.lemon.catacombs.items.melee;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.MeleeRange;

public class Sword extends MeleeWeapon {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/melee/sword.png");
    private final int damage;

    public Sword() {
        super((int) Utils.range(10, 20));
        damage = (int) Utils.range(45, 60);
    }

    @Override
    public int meleeDamage() {
        return damage;
    }

    @Override
    public MeleeRange meleeRange() {
        return new MeleeRange(2, 1);
    }

    @Override
    public boolean isDual() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }
}
