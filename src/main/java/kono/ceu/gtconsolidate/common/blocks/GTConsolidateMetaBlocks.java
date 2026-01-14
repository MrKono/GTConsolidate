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
    public static BlockMultiblockCasing MULTIBLOCK_CASING;
    public static BlockGearBoxCasing GEARBOX_CASING;
    public static BlockPipeCasing PIPE_CASING;
    public static BlockTankPart TANK_PART;

    public static void init() {
        PARALLELIZED_ASSEMBLY_LINE_CASING = new BlockParallelizedAssemblyLineCasing();
        PARALLELIZED_ASSEMBLY_LINE_CASING.setRegistryName("parallelized_assembly_line_casing");
        COOLANT_CASING = new BlockCoolantCasing();
        COOLANT_CASING.setRegistryName("coolant_casing");
        COA_CASING = new BlockCoACasing();
        COA_CASING.setRegistryName("component_assembly_line_casing");
        MULTIBLOCK_CASING = new BlockMultiblockCasing();
        MULTIBLOCK_CASING.setRegistryName("multiblock_casing");
        GEARBOX_CASING = new BlockGearBoxCasing();
        GEARBOX_CASING.setRegistryName("gearbox_casing");
        PIPE_CASING = new BlockPipeCasing();
        PIPE_CASING.setRegistryName("pipe_casing");
        TANK_PART = new BlockTankPart();
        TANK_PART.setRegistryName("tank_block");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(PARALLELIZED_ASSEMBLY_LINE_CASING);
        registerItemModel(COOLANT_CASING);
        registerItemModel(COA_CASING);
        registerItemModel(MULTIBLOCK_CASING);
        registerItemModel(GEARBOX_CASING);
        registerItemModel(PIPE_CASING);
        registerItemModel(TANK_PART);
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
