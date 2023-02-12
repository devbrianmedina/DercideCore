package com.dercide.core.commands

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginIdentifiableCommand
import cn.nukkit.plugin.Plugin
import cn.nukkit.utils.Config
import com.dercide.core.Main
import java.io.File

class CoreComands(name: String?) : Command(name), PluginIdentifiableCommand {

    override fun execute(sender: CommandSender, p1: String, args: Array<out String>): Boolean {
        if(sender.isOp){
            if(sender is Player){
                if(args.isNotEmpty()){
                    val file = File(Main.getPluginPath(Main.name) + "config.yml")
                    val config = Config(file, Config.YAML)
                    when(args[0]){
                        "tp" -> {
                            if(args.size > 1){
                                val instance = Server.getInstance()
                                val worldName = args[1]
                                if(File("worlds/$worldName/").exists()){
                                    instance.loadLevel(worldName)
                                    sender.teleport(instance.getLevelByName(worldName).spawnLocation)
                                }
                            }
                        }
                        "setlobby" -> {
                            val loc = sender.location
                            Main.lobby = loc
                            config.set("spawn", listOf(loc.x, loc.y, loc.z, loc.yaw, loc.pitch, loc.level.folderName))
                            config.save()
                            sender.sendMessage("Se establecio el lobby")
                        }
                    }
                }
            }
        }
        return false
    }

    override fun getPlugin(): Plugin? {
        return null
    }
}