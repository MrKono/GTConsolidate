package kono.ceu.gtconsolidate.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.jetbrains.annotations.NotNull;

import gregtech.api.block.VariantBlock;
import gregtech.api.items.toolitem.ToolClasses;

public class BlockParallelizedAssemblyLineCasing extends
                                                 VariantBlock<BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType> {

    public BlockParallelizedAssemblyLineCasing() {
        super(Material.IRON);
        setTranslationKey("parallelized_assembly_line_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.WRENCH, 2);
        setDefaultState(getState(ParallelizedAssemblyLineCasingType.CASING));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum ParallelizedAssemblyLineCasingType implements IStringSerializable {

        CASING("assembly_line_casing"),
        CONTROL("assembly_control_casing");

        private final String name;

        ParallelizedAssemblyLineCasingType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }
    }
}
