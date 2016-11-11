package com.ayakimenko.com.screens;

import com.ayakimenko.com.MarioBros;
import com.ayakimenko.com.scenes.MainStage;
import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.sprites.enemis.Enemy;
import com.ayakimenko.com.sprites.items.Item;
import com.ayakimenko.com.sprites.items.ItemDef;
import com.ayakimenko.com.sprites.items.Mushroom;
import com.ayakimenko.com.tools.AssetLoader;
import com.ayakimenko.com.tools.B2WorldCreator;
import com.ayakimenko.com.tools.WorldContactListener;
import com.ayakimenko.com.tools.utils.Constants;
import com.ayakimenko.com.tools.utils.SpawnObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen extends ScreenAdapter {

    private MarioBros game;

    private OrthographicCamera gameCam;
    private Viewport gameViewport;
    private MainStage mainStage;

    private OrthogonalTiledMapRenderer renderer;

    //need for seen lines of objects.
    private Box2DDebugRenderer b2dr = new Box2DDebugRenderer();
    //Box2d variables
    private World world;
    private B2WorldCreator worldCreator;

    private Mario player;
    private Array<Item> items = new Array<Item>();

    public PlayScreen(MarioBros game) {
        this.game = game;

        gameCam = new OrthographicCamera();
        gameViewport = new FitViewport(Constants.V_WIDTH / Constants.PPM, Constants.V_HEIGHT / Constants.PPM, gameCam);
        gameCam.position.set(gameViewport.getWorldWidth() / 2, gameViewport.getWorldHeight() / 2, 0);

        mainStage = new MainStage(game.getBatch());
        renderer = new OrthogonalTiledMapRenderer(AssetLoader.tiledMap, 1 / Constants.PPM);

        world = new World(new Vector2(0, -10), true);
        worldCreator = new B2WorldCreator(this);

        player = new Mario(world);

        world.setContactListener(new WorldContactListener());

        initialiseMusic();
    }

    public void update(float dl) {
        handleInput();
        handleSpawningItems();
        world.step(1 / 60f, 6, 2);

        player.update(dl);
        for (Enemy enemy : worldCreator.getEnemies()) {
            enemy.update(dl);
        }

        for (Item item : items) {
            item.update(dl);
        }

        mainStage.update(dl);

        if (player.currentState != Mario.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;
        }

        gameCam.update();
        renderer.setView(gameCam);
    }


    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.getBatch().setProjectionMatrix(gameCam.combined);

        game.getBatch().begin();
        player.draw(game.getBatch());
        for (Enemy enemy : worldCreator.getEnemies()) {
            enemy.draw(game.getBatch());
            if (enemy.getX() < player.getX() + 224 / Constants.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for (Item item : items) {
            item.draw(game.getBatch());
        }

        game.getBatch().end();

        game.getBatch().setProjectionMatrix(mainStage.stage.getCamera().combined);
        mainStage.stage.draw();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public World getWorld() {
        return world;
    }

    private boolean gameOver() {
        return player.currentState == Mario.State.DEAD && player.getStateTimer() > 3;
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        mainStage.dispose();
    }

    private void handleSpawningItems() {
        LinkedBlockingQueue<ItemDef> itemToSpawn = SpawnObject.getItemToSpawn();
        if (!itemToSpawn.isEmpty()) {
            ItemDef itemDef = itemToSpawn.poll();
            if (itemDef.type == Mushroom.class) {
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    private void handleInput() {
        if (player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            }
        }
    }

    private void initialiseMusic() {
        Music music = AssetLoader.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }
}
