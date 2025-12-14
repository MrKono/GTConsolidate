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

public class BlockGearBoxCasing extends VariantBlock<BlockGearBoxCasing.CasingType> {

    public BlockGearBoxCasing() {
        super(Material.IRON);
        setTranslationKey("gearbox_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.WRENCH, 3);
        setDefaultState(getState(BlockGearBoxCasing.CasingType.IRIDIUM));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world,
                                    @NotNull BlockPos pos, @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public IBlockState getState(BlockGearBoxCasing.CasingType variant) {
        return super.getState(variant);
    }

    public enum CasingType implements IStringSerializable {

        IRIDIUM("iridium_gearbox"),
        AMERICIUM("americium_gearbox");

        private final String name;

        CasingType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }
    }
}
