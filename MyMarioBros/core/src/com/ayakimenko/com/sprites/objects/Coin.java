package com.ayakimenko.com.sprites.objects;

import com.ayakimenko.com.MarioBros;
import com.ayakimenko.com.screens.PlayScreen;
import com.ayakimenko.com.sprites.InteractiveTileObject;
import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.sprites.items.ItemDef;
import com.ayakimenko.com.sprites.items.Mushroom;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;

import static com.ayakimenko.com.scenes.Hud.addScore;

public class Coin extends InteractiveTileObject {
    private final int BLACK_COIN = 28;
    private static TiledMapTileSet tileSet;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");

        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLACK_COIN) {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {

            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
        }
        getCell().setTile(tileSet.getTile(BLACK_COIN));
        addScore(100);
    }
}
