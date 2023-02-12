package com.dercide.core.listener

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.*
import cn.nukkit.event.entity.CreatureSpawnEvent
import cn.nukkit.event.entity.EntityExplodeEvent
import cn.nukkit.event.inventory.EnchantItemEvent
import cn.nukkit.event.inventory.FurnaceBurnEvent
import cn.nukkit.event.inventory.RepairItemEvent
import cn.nukkit.event.player.PlayerBedEnterEvent
import cn.nukkit.event.player.PlayerBucketEmptyEvent
import cn.nukkit.event.player.PlayerBucketFillEvent
import cn.nukkit.event.player.PlayerDropItemEvent
import cn.nukkit.event.player.PlayerInteractEvent
import com.dercide.core.utils.PlayerUtil


class SpawnListener : Listener {

    @EventHandler
    fun action(e: BlockBreakEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: BlockPlaceEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: EntityExplodeEvent) {
        if(PlayerUtil.isInSpawn(e.entity.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: PlayerBedEnterEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: BlockBurnEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: BlockExplodeEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: BlockExplosionPrimeEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: BlockFallEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: BlockGrowEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: BlockHarvestEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: BlockSpreadEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: CauldronFilledByDrippingLiquidEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: ComposterEmptyEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: ComposterFillEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: DoorToggleEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: FarmLandDecayEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: LeavesDecayEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: CreatureSpawnEvent) {
        if(PlayerUtil.isInSpawn(e.position.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: EnchantItemEvent) {
        if (PlayerUtil.isInSpawn(e.enchanter.location)) {
            e.isCancelled = true
            e.enchanter.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: FurnaceBurnEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun action(e: RepairItemEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: PlayerBucketFillEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: PlayerBucketEmptyEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: PlayerDropItemEvent) {
        if(PlayerUtil.isInSpawn(e.player.location)){
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }

    @EventHandler
    fun action(e: BlockIgniteEvent) {
        if(PlayerUtil.isInSpawn(e.block.location)){
            e.isCancelled = true
            if(e.entity is Player){
                (e.entity as Player).sendPopup("§4No se puede realizar esta acción en el spawn")
            }
        }
    }

    @EventHandler
    fun action(e: PlayerInteractEvent) {
        if (PlayerUtil.isInSpawn(e.player.location) && e.action == PlayerInteractEvent.Action.PHYSICAL && e.block!!.id == Block.FARMLAND) {
            e.isCancelled = true
            e.player.sendPopup("§4No se puede realizar esta acción en el spawn")
        }
    }
}