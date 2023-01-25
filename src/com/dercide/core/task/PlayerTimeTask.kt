package com.dercide.core.task

import cn.nukkit.Server
import cn.nukkit.scheduler.Task
import com.dercide.core.Main

class PlayerTimeTask : Task() {

    override fun onRun(p0: Int) {
        Main.time.forEach { (key, value) ->
            Main.time[key] = value - 1
            if(value <= 0){
                Server.getInstance().getPlayer(key).get().kick("§4Tiempo agotado vuelve mañana")
            }
        }
    }
}