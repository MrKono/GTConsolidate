package kono.ceu.gtconsolidate.common.blocks;

import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.block.VariantBlock;
import gregtech.api.items.toolitem.ToolClasses;

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

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               @NotNull ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("tile.tank_wall.tooltip"));
    }

    public enum TankWallType implements IStringSerializable {

        MK_I("mk_i", 0),
        MK_II("mk_ii", 1),
        MK_III("mk_iii", 2),
        MK_IV("mk_iv", 3),
        MK_V("mk_v", 4),
        MK_VI("mk_vi", 5),
        MK_VII("mk_vii", 6),
        MK_VIII("mk_viii", 7),
        MK_IX("mk_ix", 8),
        MK_X("mk_x", 9);

        private final String name;
        private final int tier;

        TankWallType(String name, int tier) {
            this.name = name;
            this.tier = tier;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        public int getTier() {
            return this.tier;
        }

        @NotNull
        public static TankWallType getWallTypeFromTier(int tier) {
            for (TankWallType type : values()) {
                if (type.tier == tier) {
                    return type;
                }
            }
            return MK_I;
        }
    }
}
