package com.dercide.core.utils

import cn.nukkit.Player
import cn.nukkit.item.Item
import cn.nukkit.level.Location
import cn.nukkit.math.Vector3
import cn.nukkit.utils.Binary
import cn.nukkit.utils.Config
import com.dercide.core.Main
import com.dercide.core.custom.block.BlockGrave
import com.dercide.core.custom.item.bendages.NormalBendage
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap

class GraveUtil {
    companion object {
        private fun listSaveGrave(item: Item): LinkedList<String> {
            val list = LinkedList<String>()
            list.add(item.namespaceId)
            list.add(item.damage.toString())
            list.add(item.getCount().toString())
            val tag = if (item.hasCompoundTag()) bytesToHexString(item.compoundTag) else "not"
            list.add(tag!!)
            return list
        }

        fun saveGrave(p: Player){
            val Inventory = LinkedHashMap<String, Any>()
            for(i in 0..p.inventory.size+4){
                val item: Item = p.inventory.getItem(i)
                if(item.id != 0){
                    Inventory["${Inventory.size}"] = listSaveGrave(item)
                }
            }
            val itemCursor = p.cursorInventory.getItem(0)
            if(itemCursor.id != 0) {
                Inventory["${Inventory.size}"] = listSaveGrave(itemCursor)
            }
            val itemOffHand = p.offhandInventory.getItem(0)
            if(itemOffHand.id != 0){
                Inventory["${Inventory.size}"] = listSaveGrave(itemOffHand)
            }
            Inventory["${Inventory.size}"] = listSaveGrave(NormalBendage())
            val loc = p.location
            var x = loc.x.toInt()
            var y = loc.y.toInt()
            var z = loc.z.toInt()
            var vector3 = Vector3(x.toDouble(), y.toDouble(), z.toDouble())
            var c1 = 0
            while(loc.level.getBlock(vector3).toItem().namespaceId.split(":")[0] != "minecraft"){
                if(c1 in 0..3){
                    when(c1){
                        0 -> x--
                        1 -> x+=2
                        2 -> z--
                        3 -> z+=2
                    }
                    c1++
                } else {
                    y++
                }
                vector3 = Vector3(x.toDouble(), y.toDouble(), z.toDouble())
            }
            val config = Config(File("${Main.getPluginPath(Main.name)}graves.yml"), Config.YAML)
            config.set("${x}${y}${z}", Inventory)
            config.save()
            loc.level.setBlock(vector3, BlockGrave())
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

        fun dropGrave(loc:Location, xyz:String){
            val config = Config(File("${Main.getPluginPath(Main.name)}graves.yml"), Config.YAML)
            if (config.exists(xyz)){
                val test: LinkedHashMap<String, Any> = config.get(xyz) as LinkedHashMap<String, Any>
                for(i in 0 until test.size){
                    val list:ArrayList<String> = test["$i"] as ArrayList<String>
                    val item = Item.fromString(list[0])
                    item.damage = list[1].toInt()
                    item.setCount(list[2].toInt())
                    if (list[3] != "not") {
                        val tag = Item.parseCompoundTag(Binary.hexStringToBytes(list[3]))
                        item.namedTag = tag
                    }
                    loc.level.dropItem(loc, item)
                }
            }
            config.remove(xyz)
            config.save()
        }
    }
}