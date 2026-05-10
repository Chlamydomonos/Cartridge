package xyz.chlamydomonos.cartridge.utils

import net.minecraft.world.entity.player.Player
import xyz.chlamydomonos.cartridge.hollow.HollowEntity
import xyz.chlamydomonos.cartridge.loaders.EntityDataLoader
import java.util.*
import kotlin.jvm.optionals.getOrNull

var Player.hollowUUID
    get() = this.getData(EntityDataLoader.HOLLOW_UUID).getOrNull()
    set(value) { this.setData(EntityDataLoader.HOLLOW_UUID, Optional.ofNullable(value)) }

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
    get() = getData(EntityDataLoader.IS_DEAD_HOLLOW)
    set(value) { setData(EntityDataLoader.IS_DEAD_HOLLOW, value) }

var Player.lastPos
    get() = getData(EntityDataLoader.LAST_POS)
    set(value) { setData(EntityDataLoader.LAST_POS, value) }

var Player.maxDepth
    get() = getData(EntityDataLoader.MAX_DEPTH).getOrNull()
    set(value) { setData(EntityDataLoader.MAX_DEPTH, Optional.ofNullable(value)) }

var Player.maxAbyssLevel
    get() = getData(EntityDataLoader.MAX_ABYSS_LEVEL)
    set(value) { setData(EntityDataLoader.MAX_ABYSS_LEVEL, value) }

var Player.surgeryTablePos
    get() = getData(EntityDataLoader.SURGERY_TABLE_POS).getOrNull()
    set(value) { setData(EntityDataLoader.SURGERY_TABLE_POS, Optional.ofNullable(value)) }