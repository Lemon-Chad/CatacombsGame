package com.lemon.catacombs.items.melee;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.MeleeRange;

public class Screwdriver extends MeleeWeapon {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/melee/screwdriver.png");
    private final int damage;

    public Screwdriver() {
        super((int) Utils.range(1, 3));
        this.damage = (int) Utils.range(1, 3);
    }

    @Override
    public int meleeDamage() {
        return damage;
    }

    @Override
    public MeleeRange meleeRange() {
        return new MeleeRange(1.5, 0.5);
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
    public int throwDamage() {
        return 750;
    }

    @Override
    public boolean breaksOnThrow() {
        return false;
    }
}
