package com.ayakimenko.com.sprites.objects;

import com.ayakimenko.com.screens.PlayScreen;
import com.ayakimenko.com.sprites.InteractiveTileObject;
import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.tools.AssetLoader;
import com.ayakimenko.com.tools.utils.Constants;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;

import static com.ayakimenko.com.scenes.MainStage.addScore;

public class Brick extends InteractiveTileObject {

    public Brick(World world, MapObject object) {
        super(world, object);
        fixture.setUserData(this);
        setCategoryFilter(Constants.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isMarioIsBig()) {
            setCategoryFilter(Constants.DESTROYED_BIT);
            getCell().setTile(null);
            addScore(200);
            AssetLoader.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else {
            AssetLoader.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }

    }
}
