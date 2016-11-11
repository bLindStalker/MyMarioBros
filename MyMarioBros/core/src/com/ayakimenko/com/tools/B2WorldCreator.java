package com.ayakimenko.com.tools;

import com.ayakimenko.com.screens.PlayScreen;
import com.ayakimenko.com.sprites.enemis.Enemy;
import com.ayakimenko.com.sprites.enemis.Goomba;
import com.ayakimenko.com.sprites.enemis.Turtle;
import com.ayakimenko.com.sprites.objects.Brick;
import com.ayakimenko.com.sprites.objects.Coin;
import com.ayakimenko.com.tools.utils.Constants;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static com.ayakimenko.com.tools.AssetLoader.tiledMap;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        //ground
        for (MapObject object : tiledMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = getRectangle(object);

            shape.setAsBox(rect.getWidth() / 2 / Constants.PPM, rect.getHeight() / 2 / Constants.PPM);
            fixtureDef.shape = shape;

            makeFixture(world, bodyDef, fixtureDef, rect);
        }

        // create pipe
        for (MapObject object : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = getRectangle(object);

            shape.setAsBox(rect.getWidth() / 2 / Constants.PPM, rect.getHeight() / 2 / Constants.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.OBJECT_BIT;

            makeFixture(world, bodyDef, fixtureDef, rect);
        }

        // create brigs
        for (MapObject object : tiledMap.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(world, object);
        }

        // create coins
        for (MapObject object : tiledMap.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(world, object);
        }

        // create goomgas
        goombas = new Array<Goomba>();
        for (MapObject object : tiledMap.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = getRectangle(object);
            goombas.add(new Goomba(screen, rect.getX() / Constants.PPM, rect.getY() / Constants.PPM));
        }

        // create turtles
        turtles = new Array<Turtle>();
        for (MapObject object : tiledMap.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = getRectangle(object);
            turtles.add(new Turtle(screen, rect.getX() / Constants.PPM, rect.getY() / Constants.PPM));
        }
    }

    private Rectangle getRectangle(MapObject object) {
        return ((RectangleMapObject) object).getRectangle();
    }

    private void makeFixture(World world, BodyDef bodyDef, FixtureDef fixtureDef, Rectangle rect) {
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / Constants.PPM, (rect.getY() + rect.getHeight() / 2) / Constants.PPM);
        world.createBody(bodyDef).createFixture(fixtureDef);
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
