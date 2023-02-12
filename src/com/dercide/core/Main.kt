package com.dercide.core

import cn.nukkit.Server
import cn.nukkit.inventory.CraftingManager
import cn.nukkit.inventory.ShapedRecipe
import cn.nukkit.item.Item
import cn.nukkit.item.ItemIngotGold
import cn.nukkit.item.ItemString
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.math.Vector3
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import com.dercide.core.commands.CoreComands
import com.dercide.core.commands.FriendsComands
import com.dercide.core.custom.CustomsRegister
import com.dercide.core.custom.item.bendages.GoldBendage
import com.dercide.core.custom.item.bendages.NormalBendage
import com.dercide.core.listener.PlayerListener
import com.dercide.core.listener.SpawnListener
import com.dercide.core.task.PlayerTimeTask
import com.dercide.core.task.PvPTask
import com.dercide.core.task.RadiationTask
import com.dercide.core.utils.FloatingTextUtil
import java.io.File
import java.util.concurrent.Executors
import cn.nukkit.utils.TextFormat as TF


class Main : PluginBase() {

    companion object {
        var prefix = ""
        fun getPluginPath(name:String): String{
            return "plugin_data/$name/"
        }
        var name = "DercideCore"
        lateinit var lobby: Location
        var lobbyRadius: Int = 500
        var pos1 = hashMapOf<Int, Position>()
        var pos2 = hashMapOf<Int, Position>()
        var borders: HashMap<Int, Set<Vector3>> = hashMapOf()
        var time:HashMap<String, Long> = hashMapOf()
        var pvp = false
        val scheduler = Executors.newSingleThreadScheduledExecutor { task: Runnable? ->
            val thread = Thread(task, "Radiation")
            thread.isDaemon = true
            thread
        }
        lateinit var floatingTextUtil:FloatingTextUtil
    }

    private val RECIPE1 = arrayOf(
        "aaa",
        "aba"
    )

    private val RECIPE2 = arrayOf(
        "aaa",
        "aba",
        "aaa"
    )

    override fun onLoad() {
        CustomsRegister()
    }

    override fun onEnable() {
        File(getPluginPath(Main.name) + "players/").mkdirs()
        File(getPluginPath(Main.name) + "graves/").mkdirs()
        val file = File(getPluginPath(Main.name) + "config.yml")
        val config = Config(file, Config.YAML)
        if(!config.exists("prefix")){
            val loc = server.defaultLevel.spawnLocation
            config.set("prefix", "${TF.WHITE}${TF.BOLD}MC ${TF.DARK_RED}EXTREMO")
            config.set("spawn", listOf(loc.x, loc.y, loc.z, 0.0, 0.0, loc.level.folderName))
            config.set("spawnRadius", 500)
            config.set("texts", linkedMapOf("${loc.z.toInt()}:${loc.y.toInt() + 3}:${loc.z.toInt()}:${loc.levelName}" to listOf("Titulo", "Linea1", "Linea2")))
            config.save()
        }
        val list = config.getList("spawn")
        server.loadLevel(list[5].toString())
        val level = server.getLevelByName(list[5].toString())
        lobby = Location(list[0] as Double, list[1] as Double, list[2] as Double, list[3] as Double, list[4] as Double, level)
        lobbyRadius = config.getInt("spawnRadius")
        floatingTextUtil = FloatingTextUtil(this)
        prefix = config.getString("prefix")
        enableListeners()
        registerCommands()
        registerTasks()
        val craftingManager: CraftingManager = Server.getInstance().craftingManager
        craftingManager.registerRecipe(
            ShapedRecipe(
                NormalBendage(),
                RECIPE1,
                getRecipeMap(ItemString(), ItemString()),
                ArrayList()
            )
        )
        craftingManager.registerRecipe(
            ShapedRecipe(
                GoldBendage(),
                RECIPE2,
                getRecipeMap(ItemIngotGold(), NormalBendage()),
                ArrayList()
            )
        )
        craftingManager.rebuildPacket()
    }

    fun getRecipeMap(a: Item? = null, b: Item? = null): Map<Char, Item> {
        val map: MutableMap<Char, Item> = HashMap()
        if(a != null){
            map['a'] = a
        }
        if(b != null){
            map['b'] = b
        }
        return map
    }


    private fun enableListeners(){
        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(SpawnListener(), this)
    }

    private fun registerCommands(){
        server.commandMap.register("core", CoreComands("core"))
        server.commandMap.register("friends", FriendsComands("friends"))
    }

    private fun registerTasks(){
        server.scheduler.scheduleRepeatingTask(PlayerTimeTask(), 20)
        server.scheduler.scheduleRepeatingTask(PvPTask(), 6000)
        server.scheduler.scheduleRepeatingTask(RadiationTask(), 12000)
    }
}