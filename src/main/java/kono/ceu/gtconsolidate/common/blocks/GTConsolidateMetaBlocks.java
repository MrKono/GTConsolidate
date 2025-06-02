package kono.ceu.gtconsolidate.common.blocks;

import static gregtech.common.blocks.MetaBlocks.statePropertiesToString;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GTConsolidateMetaBlocks {

    private GTConsolidateMetaBlocks() {}

    public static BlockParallelizedAssemblyLineCasing PARALLELIZED_ASSEMBLY_LINE_CASING;
    public static BlockCoolantCasing COOLANT_CASING;
    public static BlockCoACasing COA_CASING;

    public static void init() {
        PARALLELIZED_ASSEMBLY_LINE_CASING = new BlockParallelizedAssemblyLineCasing();
        PARALLELIZED_ASSEMBLY_LINE_CASING.setRegistryName("parallelized_assembly_line_casing");
        COOLANT_CASING = new BlockCoolantCasing();
        COOLANT_CASING.setRegistryName("coolant_casing");
        COA_CASING = new BlockCoACasing();
        COA_CASING.setRegistryName("component_assembly_line_casing");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(PARALLELIZED_ASSEMBLY_LINE_CASING);
        registerItemModel(COOLANT_CASING);
        registerItemModel(COA_CASING);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            statePropertiesToString(state.getProperties())));
        }
    }
}
