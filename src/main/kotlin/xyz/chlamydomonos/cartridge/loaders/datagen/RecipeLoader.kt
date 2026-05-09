package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.Items
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.loaders.BlockLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader

@EventBusSubscriber
object RecipeLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            object : RecipeProvider.Runner(it, event.lookupProvider) {
                override fun getName() = "Cartridge Recipes"

                override fun createRecipeProvider(
                    lp: HolderLookup.Provider,
                    output: RecipeOutput
                ) = object : RecipeProvider(lp, output) {
                    override fun buildRecipes() {
                        shaped(RecipeCategory.MISC, ItemLoader.CARTRIDGE)
                            .pattern("ABA")
                            .pattern("A A")
                            .pattern("AAA")
                            .define('A', Items.NETHERITE_INGOT)
                            .define('B', Items.IRON_INGOT)
                            .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                            .save(output)

                        shaped(RecipeCategory.MISC, ItemLoader.BACKPACK)
                            .pattern("ABA")
                            .pattern("CDC")
                            .pattern("CEC")
                            .define('A', Items.LEATHER)
                            .define('B', Items.HOPPER)
                            .define('C', Items.IRON_INGOT)
                            .define('D', Items.NETHERITE_BLOCK)
                            .define('E', Items.IRON_BLOCK)
                            .unlockedBy("has_netherite_block", has(Items.NETHERITE_BLOCK))
                            .save(output)

                        shaped(RecipeCategory.MISC, BlockLoader.SURGERY_TABLE.asItem())
                            .pattern("AB ")
                            .pattern("CCC")
                            .pattern("D D")
                            .define('A', Items.NETHERITE_SWORD)
                            .define('B', Items.SHEARS)
                            .define('C', Items.IRON_BLOCK)
                            .define('D', Items.IRON_INGOT)
                            .unlockedBy("has_netherite_sword", has(Items.NETHERITE_SWORD))
                            .save(output)
                    }
                }
            }
        }
    }
}