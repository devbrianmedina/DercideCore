package com.dercide.core.custom.item;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.Offset;
import cn.nukkit.item.customitem.data.RenderOffsets;

public abstract class InjectionBase extends ItemCustom {
    public InjectionBase(String id, String name, String textureName) {
        super(id, name, textureName);
    }

    @Override
    public CustomItemDefinition getDefinition() {
        Offset offset = Offset.builder()
                .scale(0.05f, 0.05f, 0.05f);
        RenderOffsets renderOffsets = new RenderOffsets(offset, offset, offset, offset);
        return CustomItemDefinition.simpleBuilder(this, ItemCreativeCategory.EQUIPMENT)
                .handEquipped(true)
                .allowOffHand(true)
                .renderOffsets(renderOffsets)
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
