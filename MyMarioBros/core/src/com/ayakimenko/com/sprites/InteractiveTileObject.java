package com.ayakimenko.com.sprites;

import com.ayakimenko.com.tools.utils.Constants;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public abstract class InteractiveTileObject {
    private TiledMap tiledMap;
    protected Body body;
    protected Fixture fixture;
    protected MapObject object;

    public InteractiveTileObject(World world, TiledMap tiledMap, MapObject object) {
        this.object = object;
        this.tiledMap = tiledMap;

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Rectangle bounds = ((RectangleMapObject) object).getRectangle();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / Constants.PPM, (bounds.getY() + bounds.getHeight() / 2) / Constants.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox(bounds.getWidth() / 2 / Constants.PPM, bounds.getHeight() / 2 / Constants.PPM);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
    }

    public abstract void onHeadHit(Mario mario);

    protected void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    protected TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(1);
        return layer.getCell((int) (body.getPosition().x * Constants.PPM / 16),
                (int) (body.getPosition().y * Constants.PPM / 16));
    }
}
