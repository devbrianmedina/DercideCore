package com.dercide.core.utils

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.block.BlockAir
import cn.nukkit.block.BlockWater
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.math.Vector3
import cn.nukkit.network.protocol.PlaySoundPacket
import cn.nukkit.network.protocol.UpdateBlockPacket
import cn.nukkit.utils.Config
import com.dercide.core.Main
import me.iwareq.scoreboard.Scoreboard
import me.iwareq.scoreboard.packet.data.DisplaySlot
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.math.*
import kotlin.random.Random


class PlayerUtil {
    companion object {

        fun addRadiation(p:Player){
            playSound(p, "radiation")
            p.sendPopup("§4Estás en contacto con la radiacion", "§4Puedes entrar al agua")
            CompletableFuture.runAsync {
                Thread.sleep(3000)
                if(!isInWater(p)){
                    p.attack(1f)
                }
                Thread.sleep(3000)
                if(!isInWater(p)){
                    p.attack(1f)
                }
                Thread.sleep(3000)
                if(!isInWater(p)){
                    p.attack(1f)
                }
            }
            Main.scheduler.schedule(
                {
                    p.sendPopup("§4Ahora estás a salvo de la radiación")
                }, 10000, TimeUnit.MILLISECONDS
            )
        }

        fun playSound(p:Player, name:String){
            val pk = PlaySoundPacket()
            pk.name = name
            pk.pitch = 1f
            pk.volume = 1f
            pk.x = p.floorX
            pk.y = p.floorY
            pk.z = p.floorZ
            p.dataPacket(pk)
        }

        fun isInWater(p: Player): Boolean {
            for (b in p.getCollisionBlocks()) {
                if (b is BlockWater) {
                    return true
                }
            }
            return false
        }

        fun canSeeSky(p: Player): Boolean {
            for (i in p.y.toInt() + 1..255) {
                if (p.getLevel().getBlock(p.x.toInt(), i, p.z.toInt(), false) !is BlockAir) {
                    return false
                }
            }
            return true
        }

        fun isInACave(p: Player): Boolean {
            val y = p.y
            if(y < 63){
                for(i in y.toInt()..63){
                    if(p.getLevel().getBlock(p.x.toInt(), i, p.z.toInt(), false).id == Block.STONE){
                        return true
                    }
                }
            }
            return false
        }

        fun isInSpawn(location: Location): Boolean {
            val x1 = location.x
            val z1 = location.z
            val x2 = Main.lobby.x
            val z2 = Main.lobby.z
            val distance = sqrt((x2 - x1).pow(2.0) + (z2 - z1).pow(2.0))
            return distance <= Main.lobbyRadius
        }

        fun sendScoreBoard(p:Player){
            val score = Scoreboard(Main.prefix, DisplaySlot.SIDEBAR, 20)
            score.setHandler {
                score.addLine("§4§l> §fTe quedan")
                score.addLine("  §c§l${getLives(p)} vidas")
                score.addLine("§4§l> §fTiempo límite")
                score.addLine("  §5§l${getTime(p)}")
                score.addLine("§4§l> §fPvP")
                score.addLine("  §6§l${getPvP()}")
                score.addLine("§4§l> §fIp")
                score.addLine("  §6§lextr.dercide.com")
            }
            score.show(p)
        }

        private fun getLives(p:Player): Int{
            val config = Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML)
            return config.getInt("lives")
        }

        fun getFriends(p:Player): List<String> {
            return Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML).getStringList("friends")
        }

        fun setFriends(p:Player, list:List<String>) {
            val config = Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML)
            config.set("friends", list)
            config.save()
        }
        
        private fun getTime(p:Player): String{
            val seconds = Main.time[p.name]!!
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = (seconds % 3600) % 60
            return "${hours}h ${minutes}m ${secs}s"
        }

        private fun getPvP(): String {
            return if(Main.pvp) { "Activado" } else { "Desactivado" }
        }

        fun playerRandomTeleport(p: Player){
            CompletableFuture.runAsync {
                val config = Config(File("${Main.getPluginPath(Main.name)}players/${p.uniqueId}.yml"), Config.YAML)
                var x = Random.nextInt(200000 - -200000) + -200000
                var z = Random.nextInt(200000 - -200000) + -200000
                if(config.getBoolean("lastdeath.active")){
                    val list = config.getList("lastdeath.location")
                    val pair = randomPointInRadius(list[0].toString().toDouble(), list[1].toString().toDouble())
                    x = pair.first.roundToInt()
                    z = pair.second.roundToInt()
                }
                var y = Random.nextInt(256 - 50) + 50
                val world = Main.lobby.level
                val base = Position(x.toDouble(), y.toDouble(), z.toDouble(), world)
                val cx = base.chunkX
                val cz = base.chunkZ
                while (!base.getLevel().isChunkGenerated(cx, cz) || !base.getLevel().isChunkLoaded(cx, cz)) {
                    base.getLevel().generateChunk(cx, cz, true)
                    base.getLevel().loadChunk(cx, cz, true)
                }
                while (world.getBlock(x, y, z).id == Block.AIR && world.getBlock(x, y + 1, z).id == Block.AIR){
                    y--
                }
                while (world.getBlockIdAt(x, y, z) != Block.AIR || world.getBlock(x, y + 1, z).id != Block.AIR){
                    y++
                }
                val randomLocation = Location(x + 0.5, y.toDouble(), z + 0.5, world)
                when(world.getBlockIdAt(x, y - 1, z)){
                    Block.FLOWING_WATER, Block.WATERLILY, Block.STILL_WATER, Block.FLOWING_LAVA, Block.STILL_LAVA, Block.LAVA_CAULDRON -> {
                        playerRandomTeleport(p)
                    }
                    else -> {
                        p.teleport(randomLocation)
                        p.sendMessage("Has sido teletransportado a $x $y $z")
                        if(config.getBoolean("lastdeath.active")) {
                            val list = config.getList("lastdeath.location")
                            p.sendMessage("§eRecuerda que la última vez donde moriste fue en x: ${list[0].toString().toInt()} z: ${list[1].toString().toInt()}")
                            config.set("lastdeath.active", false)
                            config.save()
                        }
                    }
                }
            }
        }

        fun randomPointInRadius(centerX: Double, centerZ: Double): Pair<Double, Double> {
            val radius:Double = (Random.nextInt(1000 - 500) + 500).toDouble()
            val angle = Math.random() * 2 * Math.PI
            val distance = Math.random() * radius
            val x = centerX + distance * cos(angle)
            val z = centerZ + distance * sin(angle)
            return Pair(x, z)
        }

        private const val borderBlock = 0
        private val lock = Any()
        fun showBorders(target: Player, pos1: Vector3, pos2: Vector3) {
            val minX = min(pos1.x, pos2.x).toInt()
            val minY = min(pos1.y, pos2.y).toInt()
            val minZ = min(pos1.z, pos2.z).toInt()
            val maxX = max(pos1.x, pos2.x).toInt()
            val maxY = max(pos1.y, pos2.y).toInt()
            val maxZ = max(pos1.z, pos2.z).toInt()
            val blocks: MutableSet<Vector3> = mutableSetOf()
            for (yt in minY..maxY) {
                var xt = minX
                while (true) {
                    var zt = minZ
                    while (true) {
                        val pk = UpdateBlockPacket()
                        pk.x = xt
                        pk.y = yt
                        pk.z = zt
                        pk.flags = UpdateBlockPacket.FLAG_ALL
                        pk.blockRuntimeId = this.borderBlock
                        target.dataPacket(pk)
                        blocks.add(Vector3(xt.toDouble(), yt.toDouble(), zt.toDouble()))
                        if (zt == maxZ) break
                        zt = maxZ
                    }
                    if (xt == maxX) break
                    xt = maxX
                }
            }
            var yd = minY
            while (true) {
                var zd = minZ
                while (true) {
                    for (zx in minX..maxX) {
                        val pk = UpdateBlockPacket()
                        pk.x = zx
                        pk.y = yd
                        pk.z = zd
                        pk.flags = UpdateBlockPacket.FLAG_ALL
                        pk.blockRuntimeId = this.borderBlock
                        target.dataPacket(pk)
                        blocks.add(Vector3(zx.toDouble(), yd.toDouble(), zd.toDouble()))
                    }
                    if (zd == maxZ) break
                    zd = maxZ
                }
                var xd = minX
                while (true) {
                    for (zx in minZ..maxZ) {
                        val pk = UpdateBlockPacket()
                        pk.x = xd
                        pk.y = yd
                        pk.z = zx
                        pk.flags = UpdateBlockPacket.FLAG_ALL
                        pk.blockRuntimeId = this.borderBlock
                        target.dataPacket(pk)
                        blocks.add(Vector3(xd.toDouble(), yd.toDouble(), zx.toDouble()))
                    }
                    if (xd == maxX) break
                    xd = maxX
                }
                if (yd == maxY) break
                yd = maxY
            }
            synchronized(this.lock) { Main.borders[target.loaderId] = blocks }
        }

        fun removeBorders(target: Player, send: Boolean) {
            if (send) {
                var blocks: Array<Vector3?>
                synchronized(lock) {
                    blocks = Main.borders[target.loaderId]!!.toTypedArray()
                }
                target.level.sendBlocks(arrayOf(target), blocks)
            }
            Main.borders.remove(target.loaderId)
        }
    }
}