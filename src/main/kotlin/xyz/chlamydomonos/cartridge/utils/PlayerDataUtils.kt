package xyz.chlamydomonos.cartridge.utils

import net.minecraft.world.entity.player.Player
import xyz.chlamydomonos.cartridge.hollow.HollowEntity
import xyz.chlamydomonos.cartridge.loaders.PlayerDataLoader
import java.util.*
import kotlin.jvm.optionals.getOrNull

var Player.hollowUUID
    get() = this.getData(PlayerDataLoader.HOLLOW_UUID).getOrNull()
    set(value) { this.setData(PlayerDataLoader.HOLLOW_UUID, Optional.ofNullable(value)) }

val Player.hollowEntity: HollowEntity?
    get() {
        val uuid = hollowUUID ?: return null
        val entity = level().getEntity(uuid)
        if (entity is HollowEntity) {
            return entity
        }
        return null
    }

var Player.isDeadHollow
    get() = getData(PlayerDataLoader.IS_DEAD_HOLLOW)
    set(value) { setData(PlayerDataLoader.IS_DEAD_HOLLOW, value) }

var Player.lastPos
    get() = getData(PlayerDataLoader.LAST_POS)
    set(value) { setData(PlayerDataLoader.LAST_POS, value) }

var Player.maxDepth
    get() = getData(PlayerDataLoader.MAX_DEPTH).getOrNull()
    set(value) { setData(PlayerDataLoader.MAX_DEPTH, Optional.ofNullable(value)) }

var Player.maxAbyssLevel
    get() = getData(PlayerDataLoader.MAX_ABYSS_LEVEL)
    set(value) { setData(PlayerDataLoader.MAX_ABYSS_LEVEL, value) }