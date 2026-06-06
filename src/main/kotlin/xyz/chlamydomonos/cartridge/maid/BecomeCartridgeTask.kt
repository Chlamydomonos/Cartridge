package xyz.chlamydomonos.cartridge.maid

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid
import com.github.tartaricacid.touhoulittlemaid.init.InitBrains
import com.mojang.datafixers.util.Pair
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.behavior.BehaviorControl
import net.minecraft.world.entity.ai.behavior.BehaviorUtils
import net.minecraft.world.entity.ai.behavior.BlockPosTracker
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.item.ItemResource
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlockEntity
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableMenu
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.loaders.PoiLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.optionalName
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos
import java.util.*
import java.util.function.Predicate
import kotlin.jvm.optionals.getOrNull

object BecomeCartridgeTask : IMaidTask {
    val id = RLUtil.of("become_cartridge")
    override fun getUid() = id
    override fun getIcon() = ItemStack(ItemLoader.CARTRIDGE)
    override fun getAmbientSound(maid: EntityMaid) = null
    override fun isEnable(maid: EntityMaid) = maid.favorability >= 384
    override fun getEnableConditionDesc(maid: EntityMaid) = listOf(
        Pair(
            "need_favorability",
            Predicate<EntityMaid> { it.favorability >= 384 }
        )
    )

    const val SEARCH_RANGE = 10
    const val MOVEMENT_SPEED = 0.6f
    const val DISTANCE_THRESHOLD = 2

    fun findSurgeryTable(level: ServerLevel, maid: EntityMaid): BlockPos? {
        return level.poiManager
            .getInRange(
                { it.value() == PoiLoader.SURGERY_TABLE.value() },
                maid.brainSearchPos,
                SEARCH_RANGE,
                PoiManager.Occupancy.ANY
            )
            .map { it.pos }
            .filter {
                val blockEntity = level.getBlockEntity(it)
                if (blockEntity !is SurgeryTableBlockEntity) {
                    return@filter false
                }

                if (blockEntity.playerOn != null || blockEntity.overrideCreateCartridge != null) {
                    return@filter false
                }

                return@filter true
            }
            .min(compareBy { it.distSqr(maid.blockPosition()) })
            .getOrNull()
    }

    override fun createBrainTasks(entityMaid: EntityMaid): List<Pair<Int, BehaviorControl<in EntityMaid>>> = mutableListOf(
        Pair(
            1000,
            object : MaidCheckRateTask(
                mapOf(
                    MemoryModuleType.WALK_TARGET to MemoryStatus.VALUE_ABSENT,
                    InitBrains.TARGET_POS.get() to MemoryStatus.VALUE_ABSENT
                )
            ) {
                init {
                    setMaxCheckRate(10)
                }

                override fun checkExtraStartConditions(level: ServerLevel, maid: EntityMaid): Boolean {
                    if(super.checkExtraStartConditions(level, maid) && maid.canBrainMoving()) {
                        val surgeryTablePos = findSurgeryTable(level, maid)
                        if (surgeryTablePos != null) {
                            if (
                                surgeryTablePos.distToCenterSqr(maid.position()) <
                                DISTANCE_THRESHOLD * DISTANCE_THRESHOLD
                            ) {
                                maid.brain.setMemory(
                                    InitBrains.TARGET_POS.get(),
                                    BlockPosTracker(surgeryTablePos)
                                )
                                return true
                            }

                            BehaviorUtils.setWalkAndLookTargetMemories(
                                maid,
                                surgeryTablePos,
                                MOVEMENT_SPEED,
                                1
                            )
                            setNextCheckTickCount(5)
                        } else {
                            maid.brain.eraseMemory(InitBrains.TARGET_POS.get())
                        }
                    }

                    return false
                }

                override fun start(level: ServerLevel, maid: EntityMaid, timestamp: Long) {
                    maid.brain.getMemory(InitBrains.TARGET_POS.get()).ifPresent {
                        val pos = it.currentBlockPosition()
                        val blockEntity = level.getBlockEntity(pos)
                        if (blockEntity !is SurgeryTableBlockEntity) {
                            return@ifPresent
                        }

                        if (blockEntity.playerOn != null || blockEntity.overrideCreateCartridge != null) {
                            return@ifPresent
                        }

                        blockEntity.overrideCreateCartridge = {
                            val playerUsing = this.playerUsing
                            if (playerUsing != null) {
                                if (playerUsing != maid.owner) {
                                    val menu = playerUsing.containerMenu
                                    if (menu is SurgeryTableMenu) {
                                        menu.refused = true
                                    }
                                } else {
                                    inputItem.set(0, ItemResource.of(ItemStack.EMPTY), 0)
                                    val outputStack = ItemStack(ItemLoader.CARTRIDGE, 1)
                                    outputStack.optionalName = maid.plainTextName
                                    outputItem.set(0, ItemResource.of(outputStack), 1)

                                    val inventory = maid.allInv
                                    for (i in 0..<inventory.size()) {
                                        val item = inventory.getResource(i)
                                        maid.drop(item.toStack(), true, false)
                                    }

                                    maid.remove(Entity.RemovalReason.DISCARDED)
                                    overrideCreateCartridge = null
                                    overrideOnDestroy = null
                                    val advancement = level
                                        .server
                                        .advancements
                                        .get(RLUtil.of("maid")) ?: throw RuntimeException(
                                            "Cannot load advancements"
                                        )
                                    playerUsing.advancements.award(advancement, "code_trigger")
                                }
                            }
                        }

                        blockEntity.overrideOnDestroy = {
                            maid.surgeryTablePos = null
                            maid.stopSleeping()
                        }

                        maid.surgeryTablePos = pos
                        maid.startSleeping(pos)
                        maid.setPos(pos.x + 0.5, pos.y + 0.8, pos.z + 0.5)
                    }

                    maid.getBrain().eraseMemory(InitBrains.TARGET_POS.get())
                    maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET)
                    maid.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET)
                }
            }
        )
    )
}