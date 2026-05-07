package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.dispatch.Variant
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.util.random.WeightedList
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import xyz.chlamydomonos.cartridge.loaders.BlockLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

class SurgeryTableBlock(properties: Properties) : BaseEntityBlock(
    properties
        .noOcclusion()
) {
    companion object {
        private val CODEC = simpleCodec(::SurgeryTableBlock)
        val FACING = BlockStateProperties.HORIZONTAL_FACING
        val PART = BlockStateProperties.BED_PART
        val SHAPE = box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)

        fun getNeighbourDirection(part: BedPart, direction: Direction): Direction {
            return if (part == BedPart.FOOT) direction else direction.opposite
        }

        fun genModel(generators: BlockModelGenerators) {
            generators.blockStateOutput.accept(
                MultiVariantGenerator
                    .dispatch(BlockLoader.SURGERY_TABLE)
                    .with(
                        PropertyDispatch
                            .initial(PART)
                            .select(
                                BedPart.HEAD,
                                MultiVariant(
                                    WeightedList.of(
                                        Variant(RLUtil.of("block/surgery_table_head"))
                                    )
                                )
                            )
                            .select(
                                BedPart.FOOT,
                                MultiVariant(
                                    WeightedList.of(
                                        Variant(RLUtil.of("block/surgery_table"))
                                    )
                                )
                            )
                    )
                    .with(
                        PropertyDispatch
                            .modify(FACING)
                            .select(Direction.NORTH, BlockModelGenerators.NOP)
                            .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
                            .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                            .select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
                    )
            )
        }
    }

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BedPart.FOOT)
        )
    }

    override fun codec() = CODEC

    override fun newBlockEntity(
        worldPosition: BlockPos,
        blockState: BlockState
    ): BlockEntity? {
        return if (blockState.getValue(PART) == BedPart.FOOT) {
            null
        } else {
            SurgeryTableBlockEntity(worldPosition, blockState)
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, PART)
    }

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL

    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        ticks: ScheduledTickAccess,
        pos: BlockPos,
        directionToNeighbour: Direction,
        neighbourPos: BlockPos,
        neighbourState: BlockState,
        random: RandomSource
    ): BlockState {
        val part = state.getValue(PART)
        val direction = state.getValue(FACING)
        return if (directionToNeighbour == getNeighbourDirection(part, direction)) {
            if (neighbourState.`is`(this) && neighbourState.getValue(PART) != part) {
                state
            } else {
                Blocks.AIR.defaultBlockState()
            }
        } else {
            super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
        }
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, by: LivingEntity?, itemStack: ItemStack) {
        super.setPlacedBy(level, pos, state, by, itemStack)
        if (!level.isClientSide) {
            val blockPos = pos.relative(state.getValue(FACING))
            level.setBlock(blockPos, state.setValue(PART, BedPart.HEAD), 3)
            level.updateNeighborsAt(pos, Blocks.AIR)
            state.updateNeighbourShapes(level, pos, 3)
        }
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext) = SHAPE

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val direction = context.horizontalDirection
        val blockPos = context.clickedPos
        val blockPos1 = blockPos.relative(direction)
        val level = context.level
        return if (
            level.getBlockState(blockPos1).canBeReplaced(context) && level.worldBorder.isWithinBounds(blockPos1)
        ) {
            defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction)
        } else {
            null
        }
    }

    private fun getHeadPos(pos: BlockPos, state: BlockState): BlockPos {
        val part = state.getValue(PART)
        val facing = state.getValue(FACING)
        return if (part == BedPart.HEAD) pos else pos.offset(facing.unitVec3i)
    }

    private fun lieOn(player: Player, pos: BlockPos, blockEntity: SurgeryTableBlockEntity) {
        if (player !is ServerPlayer) {
            throw RuntimeException("Trying to lie on surgery table in client")
        }

        player.pose = Pose.SLEEPING
        player.setPos(pos.x + 0.5, pos.y + 0.6875, pos.z + 0.5)
        player.setSleepingPos(pos)
        player.deltaMovement = Vec3.ZERO
        player.surgeryTablePos = pos
        blockEntity.playerOn = player
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.CONSUME
        }

        val headPos = getHeadPos(pos, state)
        val headState = level.getBlockState(headPos)
        val be = level.getBlockEntity(headPos)
        if (be !is SurgeryTableBlockEntity) {
            throw RuntimeException("Trying to access SurgeryTableBlockEntity without surgery table")
        }

        if (be.playerOn == null) {
            lieOn(player, headPos, be)
            return InteractionResult.SUCCESS
        }

        player.openMenu(getMenuProvider(headState, level, headPos))
        return InteractionResult.SUCCESS
    }
}