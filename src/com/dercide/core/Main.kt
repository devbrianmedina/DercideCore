package com.dercide.core

import cn.nukkit.entity.Entity
import cn.nukkit.entity.provider.CustomClassEntityProvider
import cn.nukkit.level.Level
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.math.Vector3
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import com.dercide.core.commands.CoreComands
import com.dercide.core.custom.entity.Grave
import com.dercide.core.listener.PlayerListener
import com.dercide.core.task.PlayerTimeTask
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.*
import cn.nukkit.utils.TextFormat as TF

class Main : PluginBase() {

    companion object {
        var prefix = ""
        fun getPluginPath(name:String): String{
            return "plugin_data/$name/"
        }
        var name = "DercideCore"
        lateinit var lobby: Location
        lateinit var world: Level
        var pos1 = hashMapOf<Int, Position>()
        var pos2 = hashMapOf<Int, Position>()
        var borders: HashMap<Int, Set<Vector3>> = hashMapOf()
        var time:HashMap<UUID, Long> = hashMapOf()
    }

    override fun onLoad() {
        registerCustoms()
    }

    override fun onEnable() {
        File(getPluginPath(Main.name) + "players/").mkdirs()
        File(getPluginPath(Main.name) + "chests/").mkdirs()
        val file = File(getPluginPath(Main.name) + "config.yml")
        val config = Config(file, Config.YAML)
        if(!config.exists("prefix")){
            val loc = server.defaultLevel.spawnLocation
            config.set("prefix", "${TF.WHITE}${TF.BOLD}MC ${TF.DARK_RED}EXTREMO")
            config.set("spawn", listOf(loc.x, loc.y, loc.z, 0.0, 0.0, loc.level.folderName))
            config.save()
        }
        if(config.exists("world")){
            server.loadLevel(config.getString("world"))
            world = server.getLevelByName(config.getString("world"))
        }
        val list = config.getList("spawn")
        server.loadLevel(list[5].toString())
        val level = server.getLevelByName(list[5].toString())
        lobby = Location(list[0] as Double, list[1] as Double, list[2] as Double, list[3] as Double, list[4] as Double, level)
        prefix = config.getString("prefix")
        enableListeners()
        registerCommands()
        registerTasks()
    }

    private fun enableListeners(){
        server.pluginManager.registerEvents(PlayerListener(), this)
    }

    private fun registerCommands(){
        server.commandMap.register("core", CoreComands("core"))
    }

    private fun registerTasks(){
        server.scheduler.scheduleRepeatingTask(PlayerTimeTask(), 20)
    }

    private fun registerCustoms(){
        try {
            Entity.registerCustomEntity(CustomClassEntityProvider(Grave.def, Grave::class.java))
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}