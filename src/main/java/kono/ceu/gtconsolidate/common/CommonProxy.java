package kono.ceu.gtconsolidate.common;

import static kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks.*;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import gregtech.api.block.VariantItemBlock;
import gregtech.loaders.recipe.RecyclingRecipes;

import kono.ceu.gtconsolidate.api.util.GTConsolidateValues;
import kono.ceu.gtconsolidate.api.util.Logs;
import kono.ceu.gtconsolidate.api.util.Mods;
import kono.ceu.gtconsolidate.common.blocks.BlockTankPart;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;
import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;
import kono.ceu.gtconsolidate.common.metatileentities.multi.electric.MetaTileLargeGreenHouse;
import kono.ceu.gtconsolidate.loader.CasingLoader;
import kono.ceu.gtconsolidate.loader.GTConsolidateMiscLoader;
import kono.ceu.gtconsolidate.loader.MetaTileEntityLoader;
import kono.ceu.gtconsolidate.loader.MultiMachineLoader;
import kono.ceu.gtconsolidate.loader.handlers.HandlersLoader;
import kono.ceu.gtconsolidate.loader.handlers.TurboBlastFurnaceLoader;

@Mod.EventBusSubscriber(modid = GTConsolidateValues.MODID)
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        GTConsolidateMetaTileEntity.init();
        GTConsolidateMetaBlocks.init();
        RecipeMapModify.modifyRecipeMap();
        RecipeMapModify.modifyRecipeBuild();
        for (BlockTankPart.TankPartType type : BlockTankPart.TankPartType.values()) {
            GTConsolidateValues.MULTIBLOCK_INTERNAL_TANKS.put(TANK_PART.getState(type), type);
        }
    }

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {}

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(PARALLELIZED_ASSEMBLY_LINE_CASING);
        registry.register(COOLANT_CASING);
        registry.register(COA_CASING);
        registry.register(MULTIBLOCK_CASING);
        registry.register(GEARBOX_CASING);
        registry.register(PIPE_CASING);
        registry.register(TANK_PART);
        registry.register(TANK_WALL);
        if (Mods.GregTechFoodOption.isModLoaded()) {
            MetaTileLargeGreenHouse.addGrasses();
        }
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(createItemBlock(PARALLELIZED_ASSEMBLY_LINE_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(COOLANT_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(COA_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(MULTIBLOCK_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(GEARBOX_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(PIPE_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(TANK_PART, VariantItemBlock::new));
        registry.register(createItemBlock(TANK_WALL, VariantItemBlock::new));
    }

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        ResourceLocation name = block.getRegistryName();
        if (name != null) {
            itemBlock.setRegistryName(name);
        }
        return itemBlock;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerRecipesHigh(RegistryEvent.Register<IRecipe> event) {}

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Logs.logger.info("Registering recipes...");
        MultiMachineLoader.init();
        CasingLoader.init();
        MetaTileEntityLoader.init();
        HandlersLoader.init();
        GTConsolidateMiscLoader.init();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerRecipesRemoval(RegistryEvent.Register<IRecipe> event) {
        Logs.logger.info("Removing recipes...");
        TurboBlastFurnaceLoader.removeConfitRecipe();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipesLow(RegistryEvent.Register<IRecipe> event) {
        Logs.logger.info("Registering recipes...");
        RecyclingRecipes.init();
        TurboBlastFurnaceLoader.reAddRecipe();
        TurboBlastFurnaceLoader.generate();
        RecipeMapModify.modifyRecipeBuildLow();
        HandlersLoader.low();
    }
}
