package com.ayakimenko.com.sprites.objects;

import com.ayakimenko.com.MarioBros;
import com.ayakimenko.com.screens.PlayScreen;
import com.ayakimenko.com.sprites.InteractiveTileObject;
import com.ayakimenko.com.sprites.Mario;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

import static com.ayakimenko.com.scenes.Hud.addScore;

public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isMarioIsBig()){
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }else {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }

    }
}
