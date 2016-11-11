package com.ayakimenko.com.tools;

import com.ayakimenko.com.sprites.InteractiveTileObject;
import com.ayakimenko.com.sprites.Mario;
import com.ayakimenko.com.sprites.enemis.Enemy;
import com.ayakimenko.com.sprites.items.Item;
import com.ayakimenko.com.tools.utils.Constants;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fiA = contact.getFixtureA();
        Fixture fiB = contact.getFixtureB();

        int cDef = fiA.getFilterData().categoryBits | fiB.getFilterData().categoryBits;

        switch (cDef) {
            case Constants.MARIO_HEAD_BIT | Constants.BRICK_BIT:
            case Constants.MARIO_HEAD_BIT | Constants.COIN_BIT:
                if (fiA.getFilterData().categoryBits == Constants.MARIO_HEAD_BIT) {
                    ((InteractiveTileObject) fiB.getUserData()).onHeadHit((Mario) fiA.getUserData());
                } else {
                    ((InteractiveTileObject) fiA.getUserData()).onHeadHit((Mario) fiB.getUserData());
                }
                break;
            case Constants.ENEMY_HEAD_BIT | Constants.MARIO_BIT:
                if (fiA.getFilterData().categoryBits == Constants.ENEMY_HEAD_BIT) {
                    ((Enemy) fiA.getUserData()).hitOnHead((Mario) fiB.getUserData());
                } else {
                    ((Enemy) fiB.getUserData()).hitOnHead((Mario) fiA.getUserData());
                }
                break;
            case Constants.ENEMY_BIT | Constants.OBJECT_BIT:
                if (fiA.getFilterData().categoryBits == Constants.ENEMY_BIT) {
                    ((Enemy) fiA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fiB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case Constants.MARIO_BIT | Constants.ENEMY_BIT:
                if (fiA.getFilterData().categoryBits == Constants.MARIO_BIT) {
                    ((Mario) fiA.getUserData()).hit((Enemy) fiB.getUserData());
                } else {
                    ((Mario) fiB.getUserData()).hit((Enemy) fiA.getUserData());
                }
                break;
            case Constants.ENEMY_BIT:
                ((Enemy) fiA.getUserData()).onEnemyHit((Enemy) fiB.getUserData());
                ((Enemy) fiB.getUserData()).onEnemyHit((Enemy) fiB.getUserData());
                break;
            case Constants.ITEM_BIT | Constants.OBJECT_BIT:
                if (fiA.getFilterData().categoryBits == Constants.ITEM_BIT) {
                    ((Item) fiA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Item) fiB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case Constants.ITEM_BIT | Constants.MARIO_BIT:
                if (fiA.getFilterData().categoryBits == Constants.ITEM_BIT) {
                    ((Item) fiA.getUserData()).use((Mario) fiB.getUserData());
                } else {
                    ((Item) fiB.getUserData()).use((Mario) fiA.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
