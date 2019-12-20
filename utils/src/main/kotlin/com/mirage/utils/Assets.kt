package com.mirage.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.google.gson.Gson
import com.mirage.utils.extensions.fromJson
import com.mirage.utils.game.objects.properties.EquipmentData
import com.mirage.utils.preferences.EquipmentSlot
import java.io.File
import java.io.InputStream
import java.io.Reader

object Assets {

    private val assetsResolver : FileHandleResolver = when (PLATFORM) {
        "test" -> FileHandleResolver {
            if (File(File("").absoluteFile.parentFile.absolutePath + "/android/assets").exists()) {
                FileHandle(File(File("").absoluteFile.parentFile.absolutePath + "/android/assets/$it"))
            }
            else if (File(File("").absolutePath + "/android/assets").exists()) {
                FileHandle(File(File("").absolutePath + "/android/assets/$it"))
            }
            else {
                Log.e("ERROR: Assets directory not found.")
                null
            }
        }
        "desktop-test" -> FileHandleResolver {
            FileHandle(File(File("").absolutePath + "/android/assets/$it"))
        }
        "server" -> FileHandleResolver {
            FileHandle(File(File("").absolutePath + "/android/assets/$it"))
        }
        "desktop", "android", "ios" -> FileHandleResolver {
            Gdx.files.internal(it)
        }
        else -> {
            Log.e("Unknown platform: $PLATFORM")
            FileHandleResolver {
                Gdx.files.internal(it)
            }
        }
    }

    fun loadFile(path: String) : FileHandle? =
        try {
            val file = assetsResolver.resolve(path)
            if (file == null || !file.exists()) {
                Log.e("File not found: $path")
                null
            }
            else file
        }
        catch (ex: Exception) {
            Log.e("File not found: $path")
            null
        }


    fun loadReader(path: String) : Reader? =
            loadFile(path)?.reader()


    fun loadAnimation(name: String) : InputStream? =
        loadFile("animations/$name.xml")?.read()

    private val gson = Gson()

    private fun getEquipmentFolder(type: EquipmentSlot) = when (type) {
        EquipmentSlot.HELMET -> "head"
        EquipmentSlot.CHEST -> "body"
        EquipmentSlot.LEGGINGS -> "legs"
        EquipmentSlot.CLOAK -> "cloak"
        EquipmentSlot.MAIN_HAND, EquipmentSlot.OFF_HAND -> "weapon"
    }

    fun loadEquipmentData(itemType: EquipmentSlot, itemName: String): EquipmentData = try {
        gson.fromJson(loadReader("equipment/${getEquipmentFolder(itemType)}/$itemName/data.json")!!)!!
    }
    catch(ex: Exception) {
        Log.e("Error while loading equipment data $itemType $itemName")
        EquipmentData()
    }


}