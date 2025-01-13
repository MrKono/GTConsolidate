package kono.ceu.gtconsolidate.common;

import java.util.function.Function;

import kono.ceu.gtconsolidate.loader.MultiMachineLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import gregtech.loaders.recipe.RecyclingRecipes;

import kono.ceu.gtconsolidate.api.util.GTConsolidateValues;
import kono.ceu.gtconsolidate.api.util.Logs;
import kono.ceu.gtconsolidate.common.machines.GTConsolidateMetaTileEntity;

@Mod.EventBusSubscriber(modid = GTConsolidateValues.MODID)
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        GTConsolidateMetaTileEntity.init();
    }

    public void init(FMLInitializationEvent e) {}

    public void postInit(FMLPostInitializationEvent e) {}

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {}

    public static void registerItem(RegistryEvent.Register<Item> event) {}

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return itemBlock;
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Logs.logger.info("Registering recipes...");
        MultiMachineLoader.init();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerRecipesRemoval(RegistryEvent.Register<IRecipe> event) {
        Logs.logger.info("Removing recipes...");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipesLow(RegistryEvent.Register<IRecipe> event) {
        Logs.logger.info("Registering recipes...");
        RecyclingRecipes.init();
    }
}
