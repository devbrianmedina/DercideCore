package com.dercide.core.listener

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.entity.Entity
import cn.nukkit.entity.EntityLiving
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.entity.EntityDamageByBlockEvent
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.entity.ProjectileHitEvent
import cn.nukkit.event.player.*
import cn.nukkit.item.ItemID
import cn.nukkit.lang.TranslationContainer
import cn.nukkit.level.ParticleEffect
import cn.nukkit.math.NukkitRandom
import cn.nukkit.network.protocol.EntityEventPacket
import cn.nukkit.network.protocol.LevelEventPacket
import cn.nukkit.potion.Effect
import cn.nukkit.utils.Config
import com.dercide.core.Main
import com.dercide.core.custom.entity.Grave
import com.dercide.core.utils.GraveUtil
import com.dercide.core.utils.PlayerUtil
import idk.plugin.npc.entities.NPC_Human
import java.io.File
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList


class PlayerListener : Listener {

    @EventHandler
    fun onPlayerPreLogin(e:PlayerPreLoginEvent){
        val config = Config(File("${Main.getPluginPath(Main.name)}players/${e.player.uniqueId}.yml"), Config.YAML)
        if(config.exists("UUID")){
            if(config.getInt("lives") <= 0){
                e.player.kick("§4No te quedan mas vidas vuelve el dia primero del proximo mes o visita https://mc.dercide.com")
            } else {
                val date = LocalDate.now()
                val dateCf = config.getString("date")
                val splitDateCf = dateCf.split("-")
                val lastDate = LocalDate.of(splitDateCf[0].toInt(), splitDateCf[1].toInt(), splitDateCf[2].toInt())
                if(lastDate == date){
                    val time = config.getLong("time")
                    if(time <= 0){
                        e.player.kick("§4Tiempo agotado vuelve mañana")
                    }
                } else if(lastDate < date){
                    config.set("date", date.toString())
                    config.set("time", 21600)
                    config.save()
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent){
        val p = e.player
        val config = Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML)
        if(!config.exists("UUID")){
            val date = LocalDate.now()
            config.set("UUID", p.uniqueId.toString())
            config.set("name", p.name)
            config.set("lives", 3)
            config.set("date", date.toString())
            config.set("time", (21600).toLong())
            config.set("lastdeath.active", false)
            config.set("lastdeath.location", null)
            config.save()
        }
        val time = config.getLong("time")
        if(time > 0){
            Main.time[p.name] = time
        } else {
            p.kick("§4Tiempo agotado vuelve mañana")
        }
        PlayerUtil.sendScoreBoard(p)
        if(p.level.folderName != Main.lobby.level.folderName){
            p.isFoodEnabled = true
        } else {
            CompletableFuture.runAsync {
                Thread.sleep(4000)
                p.teleport(Main.lobby)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val p = e.player
        val config = Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML)
        config.set("time", Main.time[p.name])
        config.save()
        Main.time.remove(p.name)
    }

    @EventHandler
    fun onProjectileHitEvent(e: ProjectileHitEvent){
        if(!Main.pvp){
            e.setCancelled()
        }
    }

    @EventHandler
    fun handleDeath(e: EntityDamageEvent) {
        val entity = e.entity
        if(entity is NPC_Human){
            if(e is EntityDamageByEntityEvent){
                if(e.damager is Player){
                    PlayerUtil.playerRandomTeleport((e.damager as Player).player)
                    return
                }
            }
        }
        if(entity is Grave){
            if(e is EntityDamageByEntityEvent){
                if(e.damager is Player){
                    GraveUtil.dropGrave((e.damager as Player).player, entity, entity.x.toInt(), entity.y.toInt(), entity.z.toInt())
                    return
                }
            }
        }
        if(entity.level.folderName == Main.lobby.level.folderName){
            e.setCancelled()
            if(entity is Player){
                if(e.cause == EntityDamageEvent.DamageCause.VOID){
                    entity.teleport(Main.lobby)
                }
            }
            return
        }

        if (e.entity is Player) {
            val p = e.entity as Player
            if(!Main.pvp){
                if(e is EntityDamageByEntityEvent){
                    if(e.damager is Player){
                        e.setCancelled()
                        (e.damager as Player).sendPopup("§4PvP Desactivado")
                        return
                    }
                }
            }

            if (p.health - e.finalDamage < 1.0f) {
                if (e.cause != EntityDamageEvent.DamageCause.VOID && e.cause != EntityDamageEvent.DamageCause.SUICIDE) {
                    var totem = false
                    if (p.offhandInventory.getItem(0).id == ItemID.TOTEM) {
                        p.offhandInventory.clear(0)
                        totem = true
                    } else if (p.inventory.itemInHand.id == ItemID.TOTEM) {
                        p.inventory.clear(p.inventory.heldItemIndex)
                        totem = true
                    }
                    if (totem) {
                        p.getLevel().addLevelEvent(p, LevelEventPacket.EVENT_SOUND_TOTEM)
                        p.getLevel().addParticleEffect(p, ParticleEffect.TOTEM)
                        p.extinguish()
                        p.removeAllEffects()
                        p.health = 1f
                        p.addEffect(Effect.getEffect(Effect.REGENERATION).setDuration(800).setAmplifier(1))
                        p.addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(800))
                        p.addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(100).setAmplifier(1))
                        val pk = EntityEventPacket()
                        pk.eid = p.id
                        pk.event = EntityEventPacket.CONSUME_TOTEM
                        p.dataPacket(pk)
                        e.isCancelled = true
                        return
                    }
                }
                val dmsg = true
                var msg = ""
                val params: MutableList<String> = ArrayList()
                if (dmsg) {
                    params.add(p.displayName)
                    when (e.cause) {
                        EntityDamageEvent.DamageCause.ENTITY_ATTACK -> if (e is EntityDamageByEntityEvent) {
                            val ent: Entity = e.damager
                            if (ent is Player) {
                                msg = "death.attack.player"
                                params.add(ent.displayName)
                            } else if (ent is EntityLiving) {
                                msg = "death.attack.mob"
                                params.add(
                                    if (!Objects.equals(
                                            ent.getNameTag(),
                                            ""
                                        )
                                    ) ent.getNameTag() else ent.getName()
                                )
                            } else {
                                params.add("Unknown")
                            }
                        }

                        EntityDamageEvent.DamageCause.PROJECTILE -> if (e is EntityDamageByEntityEvent) {
                            val ent: Entity = e.damager
                            when (ent) {
                                is Player -> {
                                    msg = "death.attack.arrow"
                                    params.add(ent.displayName)
                                }

                                is EntityLiving -> {
                                    msg = "death.attack.arrow"
                                    params.add(
                                        if (!Objects.equals(
                                                ent.getNameTag(),
                                                ""
                                            )
                                        ) ent.getNameTag() else ent.getName()
                                    )
                                }

                                else -> {
                                    params.add("Unknown")
                                }
                            }
                        }

                        EntityDamageEvent.DamageCause.VOID -> msg = "death.attack.outOfWorld"
                        EntityDamageEvent.DamageCause.FALL -> {
                            if (e.finalDamage > 2) {
                                msg = "death.fell.accident.generic"
                            }
                            msg = "death.attack.fall"
                        }

                        EntityDamageEvent.DamageCause.SUFFOCATION -> msg = "death.attack.inWall"
                        EntityDamageEvent.DamageCause.LAVA -> msg = "death.attack.lava"
                        EntityDamageEvent.DamageCause.FIRE -> msg = "death.attack.onFire"
                        EntityDamageEvent.DamageCause.FIRE_TICK -> msg = "death.attack.inFire"
                        EntityDamageEvent.DamageCause.DROWNING -> msg = "death.attack.drown"
                        EntityDamageEvent.DamageCause.CONTACT -> if (e is EntityDamageByBlockEvent) {
                            if (e.damager.id == Block.CACTUS) {
                                msg = "death.attack.cactus"
                            }
                        }

                        EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> if (e is EntityDamageByEntityEvent) {
                            val ent: Entity = e.damager
                            if (ent is Player) {
                                msg = "death.attack.explosion.player"
                                params.add(ent.displayName)
                            } else if (ent is EntityLiving) {
                                msg = "death.attack.explosion.player"
                                params.add(
                                    if (!Objects.equals(
                                            ent.getNameTag(),
                                            ""
                                        )
                                    ) ent.getNameTag() else ent.getName()
                                )
                            } else {
                                msg = "death.attack.explosion"
                            }
                        } else {
                            msg = "death.attack.explosion"
                        }

                        EntityDamageEvent.DamageCause.MAGIC -> msg = "death.attack.magic"
                        EntityDamageEvent.DamageCause.HUNGER -> msg = "death.attack.starve"
                        else -> msg = "death.attack.generic"
                    }
                }
                val ev =
                    PlayerDeathEvent(p, p.drops, TranslationContainer(msg, *params.toTypedArray()), p.experienceLevel)
                Server.getInstance().getPluginManager().callEvent(ev)
                p.health = p.maxHealth.toFloat()
                p.foodData.level = p.foodData.maxLevel
                p.removeAllEffects()
                p.extinguish()
                val loc = p.location
                loc.getLevel().setBlockAt(loc.x.toInt(), (loc.y - 1).toInt(), loc.z.toInt(), Block.TNT)
                GraveUtil.saveGrave(p)
                p.inventory.clearAll()
                p.cursorInventory.clearAll()
                if (p.isSurvival || p.isAdventure) {
                    val rand = NukkitRandom()
                    var exp = p.experience * 7
                    if (exp > 100) exp = 100
                    var add = 1
                    var i = 1
                    while (i < exp) {
                        p.getLevel().dropExpOrb(p, add)
                        add = rand.nextRange(1, 3)
                        i += add
                    }
                }
                p.setExperience(0, 0)
                if (dmsg) {
                    Server.getInstance().broadcast(ev.deathMessage, Server.BROADCAST_CHANNEL_USERS)
                }
                val config = Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML)
                val lives = config.getInt("lives") - 1
                config.set("lives", lives)
                config.set("lastdeath.active", true)
                config.set("lastdeath.location", listOf(loc.x, loc.z, loc.level.folderName))
                config.save()
                if(lives <= 0){
                    p.setGamemode(Player.SPECTATOR)
                    CompletableFuture.runAsync {
                        Thread.sleep(8000)
                        p.teleport(Main.lobby, null)
                        p.kick("§4No te quedan mas vidas vuelve el dia primero del proximo mes o visita https://mc.dercide.com")
                    }
                } else {
                    p.teleport(Main.lobby, null)
                }
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onTeleport(e: PlayerTeleportEvent){
        val p = e.player
        p.isFoodEnabled = (e.to.level.folderName != Main.lobby.level.folderName)
    }
}