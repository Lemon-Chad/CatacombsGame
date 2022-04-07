package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.render.TileSet;

public class TileSets {
    public static final TileSet StoneBrick = TileSet.LoadTilemap(ID.Block, "/sprites/tiles/stonebrick.png", 32, 32, 0, new int[]{ Layers.BLOCKS });
}
