package xyz.chlamydomonos.catridge.utils

import net.minecraft.world.entity.player.Player
import xyz.chlamydomonos.catridge.hollow.HollowEntity
import xyz.chlamydomonos.catridge.loaders.PlayerDataLoader
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