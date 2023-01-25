package com.dercide.core.custom.entity;


import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Grave extends Entity implements CustomEntity {
    public final static CustomEntityDefinition def = CustomEntityDefinition.builder().identifier("powernukkitx:grave")
            .summonable(true)
            .spawnEgg(false)
            .build();

    public Grave(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return Entity.NETWORK_ID;
    }

    @Override
    public CustomEntityDefinition getDefinition() {
        return def;
    }

    @Override
    public String getOriginalName() {
        return "grave";
    }
}