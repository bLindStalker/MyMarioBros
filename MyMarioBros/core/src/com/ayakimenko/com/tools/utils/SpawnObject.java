package com.ayakimenko.com.tools.utils;

import com.ayakimenko.com.sprites.items.ItemDef;

import java.util.concurrent.LinkedBlockingQueue;


public class SpawnObject {
    private static LinkedBlockingQueue<ItemDef> itemToSpawn = new LinkedBlockingQueue<ItemDef>();

    public static LinkedBlockingQueue<ItemDef> getItemToSpawn() {
        return itemToSpawn;
    }

    public static void addSpawnObject(ItemDef itemDef) {
        itemToSpawn.add(itemDef);
    }
}
