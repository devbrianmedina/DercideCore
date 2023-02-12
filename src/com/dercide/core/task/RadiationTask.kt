package com.dercide.core.task

import cn.nukkit.scheduler.Task
import com.dercide.core.Main
import com.dercide.core.utils.PlayerUtil
import java.util.*

class RadiationTask : Task() {

    override fun onRun(p0: Int) {
        for(p in Main.lobby.level.players.values){
            var probability = 0.1
            val inWater = PlayerUtil.isInWater(p)
            if(PlayerUtil.isInACave(p)){
                probability = 0.6
                if(inWater){
                    probability = 0.3
                }
            }
            if(randomBoolean(probability)){
                PlayerUtil.addRadiation(p)
            }
        }
    }

    fun randomBoolean(probability: Double): Boolean {
        val random = Random()
        val randomValue = random.nextDouble()
        return randomValue < probability
    }
}