package com.dercide.core.task

import cn.nukkit.Server
import cn.nukkit.scheduler.Task
import cn.nukkit.utils.Utils.rand
import com.dercide.core.Main
import com.dercide.core.utils.PlayerUtil

class PvPTask : Task() {

    override fun onRun(p0: Int) {
        if(rand(0, 1) == 1){
            if(!Main.pvp){
                Main.pvp = true

                Server.getInstance().onlinePlayers.values.forEach {
                    PlayerUtil.playSound(it, "alertapvp")
                }
                //Main.global.play(Main.global.playlist!![0]!!, Server.getInstance().onlinePlayers.values.toTypedArray())
            }
        } else {
            if(Main.pvp){
                Main.pvp = false
                Server.getInstance().onlinePlayers.values.forEach {
                    PlayerUtil.playSound(it, "alertapvp")
                }
                //Main.global.play(Main.global.playlist!![0]!!, Server.getInstance().onlinePlayers.values.toTypedArray())
            }
        }
    }
}