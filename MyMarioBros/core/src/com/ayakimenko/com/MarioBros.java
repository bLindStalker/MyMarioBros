package com.ayakimenko.com;

import com.ayakimenko.com.screens.PlayScreen;
import com.ayakimenko.com.tools.AssetLoader;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MarioBros extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        AssetLoader.initialize();

        setScreen(new PlayScreen(this));
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
        batch.dispose();
    }
}
