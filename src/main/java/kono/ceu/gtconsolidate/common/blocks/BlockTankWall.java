package kono.ceu.gtconsolidate.common.blocks;

import gregtech.api.block.VariantBlock;
import gregtech.api.items.toolitem.ToolClasses;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public class BlockTankWall extends
        VariantBlock<BlockTankWall.TankWallType> {

    public BlockTankWall() {
        super(Material.IRON);
        setTranslationKey("tank_wall");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.WRENCH, 2);
        setDefaultState(getState(TankWallType.MK_I));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum TankWallType implements IStringSerializable {

        MK_I("mk_i"),
        MK_II("mk_ii"),
        MK_III("mk_iii"),
        MK_IV("mk_iv"),
        MK_V("mk_v"),
        MK_VI("mk_vi"),
        MK_VII("mk_vii"),
        MK_VIII("mk_viii"),
        MK_IX("mk_ix"),
        MK_X("mk_x");

        private final String name;

        TankWallType(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }
    }
}