package com.dercide.core.utils

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.item.Item
import cn.nukkit.math.Vector3
import cn.nukkit.utils.Binary
import cn.nukkit.utils.Config
import com.dercide.core.Main
import com.dercide.core.custom.entity.Grave
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap

class GraveUtil {
    companion object {
        fun saveGrave(p: Player){
            val Inventory = LinkedHashMap<String, Any>()
            for(i in 0..p.inventory.size+4){
                val list = LinkedList<String>()
                val item: Item = p.inventory.getItem(i)
                list.add(item.id.toString() + ":" + item.damage)
                list.add(item.getCount().toString() + "")
                val tag = if (item.hasCompoundTag()) bytesToHexString(item.compoundTag) else "not"
                list.add(tag!!)
                Inventory["$i"] = list
            }
            val loc = p.location
            val x = loc.x.toInt()
            val y = loc.y.toInt()
            val z = loc.z.toInt()
            val config = Config(File("${Main.getPluginPath(Main.name)}chests/${p.name}.yml"), Config.YAML)
            config.set("${x}${y}${z}", Inventory)
            config.save()
            val entity = Grave(p.getChunk(), Entity.getDefaultNBT(Vector3(x + 0.5, y.toDouble(), z + 0.5)))
            entity.nameTag = p.name
            entity.spawnToAll()
        }

        fun dropGrave(p:Player, entity:Grave, x:Int, y:Int, z:Int){
            val name = entity.nameTag
            val config = Config(File("${Main.getPluginPath(Main.name)}chests/${name}.yml"), Config.YAML)
            if (config.exists("${x}${y}${z}")){
                val test: LinkedHashMap<String, Any> = config.get("${x}${y}${z}") as LinkedHashMap<String, Any>
                for(i in 0..p.inventory.size+4){
                    val list:ArrayList<String> = test["$i"] as ArrayList<String>
                    val id = (list[0]).split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val item = Item(id[0].toInt(), id[1].toInt(), list[1].toInt())
                    if (list[2] != "not") {
                        val tag = Item.parseCompoundTag(Binary.hexStringToBytes(list[2]))
                        item.namedTag = tag
                    }
                    p.level.dropItem(p.location, item)
                }
                entity.kill()
            }
        }

        private fun bytesToHexString(src: ByteArray?): String? {
            val stringBuilder = StringBuilder("")
            if (src == null || src.isEmpty()) {
                return null
            }
            for (aSrc in src) {
                val v = aSrc.toInt() and 0xFF
                val hv = Integer.toHexString(v)
                if (hv.length < 2) {
                    stringBuilder.append(0)
                }
                stringBuilder.append(hv)
            }
            return stringBuilder.toString()
        }
    }
}