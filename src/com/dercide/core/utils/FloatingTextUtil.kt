package com.dercide.core.utils

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Position
import cn.nukkit.level.particle.FloatingTextParticle
import cn.nukkit.utils.Config
import com.dercide.core.Main
import java.io.File
import kotlin.collections.LinkedHashMap


class FloatingTextUtil(val plugin: Main) {

	var floatingTextList: MutableList<FloatingTextParticle>

	init {
		val textListConfig = Config(File("${Main.getPluginPath(Main.name)}config.yml"), Config.YAML)
		floatingTextList = mutableListOf()
		(textListConfig.get("texts") as LinkedHashMap<String, Any>).forEach { (key: String, list) ->
			val lines = list as ArrayList<String>
			val pos = stringToPos(key)
			val ftp = FloatingTextParticle(pos, lines[0])
			var text = ""
			for((i, line) in lines.withIndex()){
				when(i){
					0 -> {}
					1 -> text += line
					else -> text += "\n$line"
				}
			}
			ftp.text = text
			floatingTextList.add(ftp)
		}
	}

	private fun stringToPos(pos: String): Position {
		val args = pos.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		return Position(
			args[0].toInt() + 0.5, args[1].toInt().toDouble(), args[2].toInt() + 0.5,
			Server.getInstance().getLevelByName(args[3])
		)
	}

	fun spawnAllText(player: Player) {
		floatingTextList.forEach {
			player.level.addParticle(it, player)
		}
	}
}