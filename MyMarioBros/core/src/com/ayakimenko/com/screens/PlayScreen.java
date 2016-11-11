package com.ayakimenko.com.screens;

import com.ayakimenko.com.MarioBros;
import com.ayakimenko.com.scenes.Hud;
import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.sprites.enemis.Enemy;
import com.ayakimenko.com.sprites.items.Item;
import com.ayakimenko.com.sprites.items.ItemDef;
import com.ayakimenko.com.sprites.items.Mushroom;
import com.ayakimenko.com.tools.AssetLoader;
import com.ayakimenko.com.tools.B2WorldCreator;
import com.ayakimenko.com.tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private MarioBros game;
    private TextureAtlas atlas;


    private OrthographicCamera gameCam;
    private Viewport gameViewport;
    private Hud hud;

    // tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemToSpawn;

    public PlayScreen(MarioBros game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();
        gameViewport = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gameCam.position.set(gameViewport.getWorldWidth() / 2, gameViewport.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);

        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());
        music = AssetLoader.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        items = new Array<Item>();
        itemToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef itemDef) {
        itemToSpawn.add(itemDef);
    }

    public void handleSpawningItems() {
        if (!itemToSpawn.isEmpty()) {
            ItemDef itemDef = itemToSpawn.poll();
            if (itemDef.type == Mushroom.class) {
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void update(float dl) {
        handleInput(dl);
        handleSpawningItems();
        world.step(1 / 60f, 6, 2);

        player.update(dl);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dl);
        }

        for (Item item : items) {
            item.update(dl);
        }

        hud.update(dl);

        if (player.currentState != Mario.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;
        }

        gameCam.update();
        renderer.setView(gameCam);
    }

    private void handleInput(float dl) {
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

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for (Item item : items) {
            item.draw(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public boolean gameOver() {
        return player.currentState == Mario.State.DEAD && player.getStateTimer() > 3;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
