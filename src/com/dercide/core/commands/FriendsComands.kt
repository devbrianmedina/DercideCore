package com.dercide.core.commands

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginIdentifiableCommand
import cn.nukkit.plugin.Plugin
import com.dercide.core.utils.FriendsUI

class FriendsComands(name: String?) : Command(name), PluginIdentifiableCommand {

    override fun execute(sender: CommandSender, p1: String, args: Array<out String>): Boolean {
        if(sender is Player){
            FriendsUI.mainUI(sender)
        }
        return false
    }

    override fun getPlugin(): Plugin? {
        return null
    }
}