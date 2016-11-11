package com.ayakimenko.com.sprites.items;


import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.tools.AssetLoader;
import com.ayakimenko.com.tools.utils.Constants;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Mushroom extends Item {
    public Mushroom(World world, float x, float y) {
        super(world, x, y);
        setRegion(AssetLoader.atlas.findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Constants.PPM);

        fdef.shape = shape;
        fdef.filter.categoryBits = Constants.ITEM_BIT;
        fdef.filter.maskBits = Constants.MARIO_BIT
                | Constants.OBJECT_BIT
                | Constants.GROUND_BIT
                | Constants.COIN_BIT
                | Constants.ENEMY_BIT;


        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
