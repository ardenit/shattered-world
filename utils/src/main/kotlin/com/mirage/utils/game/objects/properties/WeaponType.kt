package com.mirage.utils.game.objects.properties


/**
 * Weapon type of a humanoid entity
 */
enum class WeaponType {
    UNARMED,
    ONE_HANDED,
    ONE_HANDED_AND_SHIELD,
    DUAL,
    TWO_HANDED,
    BOW,
    STAFF;
    companion object {

        fun fromString(str: String) : WeaponType {
            return when (str) {
                "UNARMED" -> UNARMED
                "ONE_HANDED" -> ONE_HANDED
                "ONE_HANDED_AND_SHIELD" -> ONE_HANDED_AND_SHIELD
                "DUAL" -> DUAL
                "TWO_HANDED" -> TWO_HANDED
                "BOW" -> BOW
                "STAFF" -> STAFF
                else -> throw Exception("Incorrect weapon type: $str")
            }
        }
    }
}