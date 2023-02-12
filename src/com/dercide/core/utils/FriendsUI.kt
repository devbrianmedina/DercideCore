package com.dercide.core.utils

import cn.nukkit.Player
import cn.nukkit.form.element.*
import cn.nukkit.form.window.FormWindowCustom
import cn.nukkit.form.window.FormWindowSimple

class FriendsUI {
    companion object{
        fun mainUI(player:Player){
            val form = FormWindowSimple("Amigos", "Selecciona una opción")
            form.addButton(ElementButton("Añadir amigo", ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/items/trident.png")))
            form.addButton(ElementButton("Eliminar amigo", ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/blocks/barrier.png")))
            form.addHandler { p, i ->
                if(form.response != null){
                    when(form.response.clickedButtonId){
                        0 -> addFriend(p)
                        1 -> removeFriend(p)
                    }
                }
            }
            player.showFormWindow(form)
        }

        fun addFriend(player:Player){
            val form = FormWindowCustom("Añadir amigo")
            val onlinePlayers = mutableListOf("Selecciona un jugador")
            player.server.onlinePlayers.values.forEach {
                if(it.name != player.name){
                    onlinePlayers.add(it.name)
                }
            }
            form.addElement(ElementLabel("Selecciona el jugador a añadir"))
            form.addElement(ElementDropdown("Jugador", onlinePlayers))
            form.addElement(ElementLabel("También puedes ingresar su nickname"))
            form.addElement(ElementInput("Nickname"))
            form.addHandler { p, i ->
                if(form.response != null){
                    val list = mutableListOf<String>()
                    list.addAll(PlayerUtil.getFriends(p))
                    val d1 = form.response.getDropdownResponse(1)
                    if(d1.elementID != 0){
                        if(!list.contains(d1.elementContent)){
                            list.add(d1.elementContent)
                        }
                    }
                    val d2 = form.response.getInputResponse(3)
                    if(!d2.isNullOrEmpty()){
                        if(!list.contains(d2)){
                            list.add(d2)
                        }
                    }
                    PlayerUtil.setFriends(p, list)
                }
            }
            player.showFormWindow(form)
        }

        fun removeFriend(player:Player){
            val form = FormWindowCustom("Eliminar amigo")
            val friends = mutableListOf("Selecciona un jugador")
            friends.addAll(PlayerUtil.getFriends(player))
            form.addElement(ElementLabel("Selecciona el jugador a eliminar"))
            form.addElement(ElementDropdown("Jugador", friends))
            form.addHandler { p, i ->
                if(form.response != null){
                    val list = mutableListOf<String>()
                    list.addAll(PlayerUtil.getFriends(p))
                    val d1 = form.response.getDropdownResponse(1)
                    if(d1.elementID != 0){
                        if(!list.contains(d1.elementContent)){
                            list.remove(d1.elementContent)
                        }
                    }
                    PlayerUtil.setFriends(p, list)
                }
            }
            player.showFormWindow(form)
        }
    }
}