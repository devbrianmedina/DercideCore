package com.dercide.core.custom.block;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTransparentMeta;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.block.customblock.data.*;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGrave extends BlockTransparentMeta implements CustomBlock {
    public final BooleanBlockProperty BRIDGE_TOP_SLOT_BIT = new BooleanBlockProperty("bridge:top_slot_bit", false);
    public final BooleanBlockProperty BRIDGE_IS_FULL_BIT = new BooleanBlockProperty("bridge:is_full_bit", false);

    @NotNull
    @Override
    public String getNamespaceId() {
        return "dercide:grave";
    }

    @Override
    public String getName() {
        return CustomBlock.super.getName();
    }

    @Override
    public int getId() {
        return CustomBlock.super.getId();
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return new BlockProperties(
                BRIDGE_TOP_SLOT_BIT, BRIDGE_IS_FULL_BIT
        );
    }

    @Override
    public CustomBlockDefinition getDefinition() {
        return CustomBlockDefinition
                .builder(this, Materials.builder().any(Materials.RenderMethod.OPAQUE, "grave_side").up(Materials.RenderMethod.OPAQUE, "grave"))
                .geometry("geometry.grave")
                .permutations(
                        new Permutation(Component.builder()
                                .collisionBox(new CollisionBox(-8, 0, -8, 16, 8, 16))
                                .selectionBox(new SelectionBox(-8, 0, -8, 16, 8, 16))
                                .build(),
                                "query.block_property('bridge:top_slot_bit') == false && query.block_property('bridge:is_full_bit') == false"),
                        new Permutation(Component.builder()
                                .collisionBox(new CollisionBox(-8, 8, -8, 16, 16, 16))
                                .selectionBox(new SelectionBox(-8, 8, -8, 16, 16, 16))
                                .build(),
                                "query.block_property('bridge:top_slot_bit') == true && query.block_property('bridge:is_full_bit') == false"),
                        new Permutation(Component.builder()
                                .collisionBox(new CollisionBox(-8, 0, -8, 16, 16, 16))
                                .selectionBox(new SelectionBox(-8, 0, -8, 16, 16, 16))
                                .build(),
                                "query.block_property('bridge:is_full_bit') == true")
                )
                .partVisibility(
                        new BoneCondition("lower", "lower", "!query.block_property('bridge:top_slot_bit') || query.block_property('bridge:is_full_bit')"),
                        new BoneCondition("upper", "upper", "query.block_property('bridge:top_slot_bit') || query.block_property('bridge:is_full_bit')")
                )
                .build();
    }

    @Override
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        return 3;
    }

    @Override
    public double getFrictionFactor() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getLightFilter() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getItemMaxStackSize() {
        return 64;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    public boolean isOnTop() {
        return getBooleanValue(BRIDGE_TOP_SLOT_BIT);
    }

    public boolean isFull() {
        return getBooleanValue(BRIDGE_IS_FULL_BIT);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBooleanValue(BRIDGE_IS_FULL_BIT, false);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockGrave) {
                if (target.getBooleanValue(BRIDGE_TOP_SLOT_BIT) && !target.getBooleanValue(BRIDGE_IS_FULL_BIT)) {//如果往上层半砖下方放置半砖，且不完整,则设为完整
                    setBooleanValue(BRIDGE_TOP_SLOT_BIT, false);
                    setBooleanValue(BRIDGE_IS_FULL_BIT, true);
                    this.getLevel().setBlock(target, this, true);
                    return false;
                } else if (!target.getBooleanValue(BRIDGE_TOP_SLOT_BIT) || target.getBooleanValue(BRIDGE_IS_FULL_BIT)) {//如果往下层半砖或者完整半砖下方放置半砖，则正常放置上层半砖
                    setBooleanValue(BRIDGE_TOP_SLOT_BIT, true);
                    this.getLevel().setBlock(this, this, true);
                    return true;
                } else return false;
            } else {
                setBooleanValue(BRIDGE_TOP_SLOT_BIT, true);
                this.getLevel().setBlock(this, this, true);
                return true;
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockGrave) {
                if (!target.getBooleanValue(BRIDGE_TOP_SLOT_BIT) && !target.getBooleanValue(BRIDGE_IS_FULL_BIT)) {//如果往下层半砖上方放置半砖，且不完整,则设为完整
                    setBooleanValue(BRIDGE_TOP_SLOT_BIT, false);
                    setBooleanValue(BRIDGE_IS_FULL_BIT, true);
                    this.getLevel().setBlock(target, this, true);
                    return false;
                } else if (target.getBooleanValue(BRIDGE_TOP_SLOT_BIT) || target.getBooleanValue(BRIDGE_IS_FULL_BIT)) {
                    setBooleanValue(BRIDGE_TOP_SLOT_BIT, false);
                    this.getLevel().setBlock(this, this, true);
                    return true;
                } else return false;
            } else {
                setBooleanValue(BRIDGE_TOP_SLOT_BIT, false);
                this.getLevel().setBlock(this, this, true);
            }
        } else {
            setBooleanValue(BRIDGE_TOP_SLOT_BIT, fy > 0.5);
            this.getLevel().setBlock(this, this, true);
            return true;
        }
        return false;
    }
}