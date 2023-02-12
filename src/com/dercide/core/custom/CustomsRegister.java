package com.dercide.core.custom;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import com.dercide.core.custom.block.BlockGrave;
import com.dercide.core.custom.item.bendages.GoldBendage;
import com.dercide.core.custom.item.bendages.NormalBendage;
import com.dercide.core.custom.item.injections.AdrenalineInjection;
import com.dercide.core.custom.item.injections.AntidoteInjection;

import java.util.List;

public class CustomsRegister {

    public CustomsRegister(){
        try {
            //Entity.registerCustomEntity(new CustomClassEntityProvider(Grave.def, Grave.class));
            Block.registerCustomBlock(List.of(BlockGrave.class));
            Item.registerCustomItem(List.of(AdrenalineInjection.class, AntidoteInjection.class, NormalBendage.class, GoldBendage.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
