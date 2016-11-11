package com.ayakimenko.com.sprites.objects;

import com.ayakimenko.com.sprites.InteractiveTileObject;
import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.sprites.items.ItemDef;
import com.ayakimenko.com.sprites.items.Mushroom;
import com.ayakimenko.com.tools.AssetLoader;
import com.ayakimenko.com.tools.utils.Constants;
import com.ayakimenko.com.tools.utils.SpawnObject;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import static com.ayakimenko.com.scenes.MainStage.addScore;
import static com.ayakimenko.com.tools.AssetLoader.tiledMap;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLACK_COIN = 28;

    public Coin(World world, MapObject object) {
        super(world, object);
        tileSet = tiledMap.getTileSets().getTileSet("tileset_gutter");

        fixture.setUserData(this);
        setCategoryFilter(Constants.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLACK_COIN) {
            AssetLoader.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {

            if (object.getProperties().containsKey("mushroom")) {
                SpawnObject.addSpawnObject(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / Constants.PPM), Mushroom.class));
                AssetLoader.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                AssetLoader.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
        }
        getCell().setTile(tileSet.getTile(BLACK_COIN));
        addScore(100);
    }
}
