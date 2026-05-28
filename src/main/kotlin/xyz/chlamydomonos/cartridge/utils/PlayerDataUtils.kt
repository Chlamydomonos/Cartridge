package xyz.chlamydomonos.cartridge.utils

import net.minecraft.world.entity.player.Player
import xyz.chlamydomonos.cartridge.hollow.HollowEntity
import xyz.chlamydomonos.cartridge.loaders.DataAttachmentLoader
import java.util.*
import kotlin.jvm.optionals.getOrNull

var Player.hollowUUID
    get() = this.getData(DataAttachmentLoader.HOLLOW_UUID).getOrNull()
    set(value) { this.setData(DataAttachmentLoader.HOLLOW_UUID, Optional.ofNullable(value)) }

val Player.hollowEntity: HollowEntity?
    get() {
        val uuid = hollowUUID ?: return null
        val entity = level().getEntity(uuid)
        if (entity is HollowEntity) {
            return entity
        }
        return null
    }

var Player.hollowPos
    get() = this.getData(DataAttachmentLoader.HOLLOW_POS).getOrNull()
    set(value) { this.setData(DataAttachmentLoader.HOLLOW_POS, Optional.ofNullable(value)) }

var Player.isDeadHollow
    get() = getData(DataAttachmentLoader.IS_DEAD_HOLLOW)
    set(value) { setData(DataAttachmentLoader.IS_DEAD_HOLLOW, value) }

var Player.lastPos
    get() = getData(DataAttachmentLoader.LAST_POS)
    set(value) { setData(DataAttachmentLoader.LAST_POS, value) }

var Player.maxDepth
    get() = getData(DataAttachmentLoader.MAX_DEPTH).getOrNull()
    set(value) { setData(DataAttachmentLoader.MAX_DEPTH, Optional.ofNullable(value)) }

var Player.maxAbyssLevel
    get() = getData(DataAttachmentLoader.MAX_ABYSS_LEVEL)
    set(value) { setData(DataAttachmentLoader.MAX_ABYSS_LEVEL, value) }

var Player.surgeryTablePos
    get() = getData(DataAttachmentLoader.SURGERY_TABLE_POS).getOrNull()
    set(value) { setData(DataAttachmentLoader.SURGERY_TABLE_POS, Optional.ofNullable(value)) }