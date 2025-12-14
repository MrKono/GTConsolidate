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

public class BlockMultiblockCasing extends
                                   VariantBlock<BlockMultiblockCasing.MultiblockCasingType> {

    public BlockMultiblockCasing() {
        super(Material.IRON);
        setTranslationKey("multiblock_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.WRENCH, 2);
        setDefaultState(getState(MultiblockCasingType.OSMIRIDIUM_STURDY));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum MultiblockCasingType implements IStringSerializable {

        OSMIRIDIUM_STURDY("osmiridium_sturdy"),
        DARMSTADTIUM_STURDY("darmstadtium_sturdy"),
        TRITANIUM_STURDY("tritanium_sturdy"),
        IRIDIUM_PLATED("iridium_plated"),
        AMERICIUM_PLATED("americium_plated");

        private final String name;

        MultiblockCasingType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }
    }
}
