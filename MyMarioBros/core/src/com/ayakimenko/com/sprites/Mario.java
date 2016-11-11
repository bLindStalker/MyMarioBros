package com.ayakimenko.com.sprites;

import com.ayakimenko.com.MarioBros;
import com.ayakimenko.com.screens.PlayScreen;
import com.ayakimenko.com.sprites.enemis.Enemy;
import com.ayakimenko.com.sprites.enemis.Turtle;
import com.ayakimenko.com.tools.AssetLoader;
import com.ayakimenko.com.tools.utils.Constants;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Mario extends Sprite {

    public State currentState;
    public Body b2body;
    private State previusState;
    private World word;
    private TextureRegion marioStand;
    private Animation marioRun;
    private Animation bigMarioRun;
    private Animation growMario;
    private TextureRegion marioJump;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion marioDead;
    private float stateTimer;
    private boolean timeToDefineMario;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runnGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean marioIsDead;
    public Mario(PlayScreen playScreen) {
        super(playScreen.getAtlas().findRegion("little_mario"));
        this.word = playScreen.getWorld();
        currentState = State.STANDING;
        previusState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(playScreen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);
        marioRun.setPlayMode(Animation.PlayMode.LOOP);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        bigMarioRun.setPlayMode(Animation.PlayMode.LOOP);
        frames.clear();

        frames.add(new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));

        growMario = new Animation(0.2f, frames);

        marioJump = new TextureRegion(playScreen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        defineMario();

        marioStand = new TextureRegion(playScreen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(playScreen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        marioDead = new TextureRegion(playScreen.getAtlas().findRegion("little_mario"), 97, 0, 16, 16);

        setBounds(0, 0, 16 / Constants.PPM, 16 / Constants.PPM);
        setRegion(marioStand);
    }

    public void update(float dt) {
        if (marioIsBig) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / Constants.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

        setRegion(getFrame(dt));

        if (timeToDefineBigMario) {
            defineBigMario();
        }

        if (timeToDefineMario) {
            redefineMario();
        }
    }

    private void defineBigMario() {
        Vector2 position = b2body.getPosition();
        word.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position.add(0, 10 / Constants.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = word.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Constants.PPM);
        fdef.filter.categoryBits = Constants.MARIO_BIT;
        fdef.filter.maskBits = Constants.GROUND_BIT
                | Constants.COIN_BIT
                | Constants.BRICK_BIT
                | Constants.ENEMY_BIT
                | Constants.OBJECT_BIT
                | Constants.ENEMY_HEAD_BIT
                | Constants.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / Constants.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Constants.PPM, 6 / Constants.PPM), new Vector2(2 / Constants.PPM, 6 / Constants.PPM));
        fdef.filter.categoryBits = Constants.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runnGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer) : marioRun.getKeyFrame(stateTimer);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previusState ? stateTimer + dt : 0;
        previusState = currentState;
        return region;
    }

    private State getState() {
        if (marioIsDead) {
            return State.DEAD;
        } else if (runnGrowAnimation) {
            return State.GROWING;
        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previusState == State.JUMPING)) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else return State.STANDING;
    }

    private void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(1 / Constants.PPM, 32 / Constants.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = word.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Constants.PPM);
        fdef.filter.categoryBits = Constants.MARIO_BIT;
        fdef.filter.maskBits = Constants.GROUND_BIT
                | Constants.COIN_BIT
                | Constants.BRICK_BIT
                | Constants.ENEMY_BIT
                | Constants.OBJECT_BIT
                | Constants.ENEMY_HEAD_BIT
                | Constants.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Constants.PPM, 6 / Constants.PPM), new Vector2(2 / Constants.PPM, 6 / Constants.PPM));
        fdef.filter.categoryBits = Constants.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void grow() {
        runnGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;

        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        AssetLoader.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void redefineMario() {
        Vector2 position = b2body.getPosition();
        word.destroyBody(b2body);


        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = word.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Constants.PPM);
        fdef.filter.categoryBits = Constants.MARIO_BIT;
        fdef.filter.maskBits = Constants.GROUND_BIT
                | Constants.COIN_BIT
                | Constants.BRICK_BIT
                | Constants.ENEMY_BIT
                | Constants.OBJECT_BIT
                | Constants.ENEMY_HEAD_BIT
                | Constants.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Constants.PPM, 6 / Constants.PPM), new Vector2(2 / Constants.PPM, 6 / Constants.PPM));
        fdef.filter.categoryBits = Constants.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToDefineMario = false;
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {

            if (marioIsBig) {
                marioIsBig = false;
                timeToDefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                AssetLoader.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                AssetLoader.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                AssetLoader.manager.get("audio/sounds/mariodie.wav", Sound.class).play();

                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = Constants.NOTHING_BIT;

                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }

                b2body.applyLinearImpulse(new Vector2(2, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public boolean isMarioIsDead() {
        return marioIsDead;
    }

    public boolean isMarioIsBig() {
        return marioIsBig;
    }

    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD;}
}
