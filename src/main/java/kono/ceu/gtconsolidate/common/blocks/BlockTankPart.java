package kono.ceu.gtconsolidate.common.blocks;

import gregtech.api.GTValues;
import gregtech.api.block.VariantBlock;
import gregtech.api.items.toolitem.ToolClasses;
import kono.ceu.gtconsolidate.api.multiblock.ITankData;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockTankPart  extends VariantBlock<BlockTankPart.TankPartType> {

    public BlockTankPart() {
        super(Material.GLASS);
        setTranslationKey("tank_block");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.WRENCH, 3); // Diamond level, can be mined by a steel wrench or better
        setDefaultState(getState(TankPartType.EMPTY_TIER_I));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType placementType) {
        return false;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               @NotNull ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);

        TankPartType tankType = getState(stack);
        if (tankType.getCapacity() != 0) {
            tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", tankType.getCapacity()));
        } else {
            //tooltip.add(I18n.format("tile.tank_block.tooltip_empty.1"));
            tooltip.add(I18n.format("tile.tank_block.tooltip_empty.2"));
        }
    }

    @Override
    @NotNull
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canRenderInLayer(@NotNull IBlockState state, @NotNull BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(@NotNull IBlockState state, IBlockAccess world, BlockPos pos,
                                        @NotNull EnumFacing side) {
        IBlockState sideState = world.getBlockState(pos.offset(side));

        return sideState.getBlock() == this ?
                getState(sideState) != getState(state) :
                super.shouldSideBeRendered(state, world, pos, side);
    }

    public enum TankPartType implements IStringSerializable, ITankData {

        EMPTY_TIER_I,
        SUPER_I(GTValues.ULV, calCapacity(GTValues.ULV)),
        SUPER_II(GTValues.LV, calCapacity(GTValues.LV)),
        SUPER_III(GTValues.MV, calCapacity(GTValues.MV)),

        EMPTY_TIER_II,
        SUPER_IV(GTValues.HV, calCapacity(GTValues.HV)),
        SUPER_V(GTValues.EV, calCapacity(GTValues.EV)),
        QUANTUM_I(GTValues.IV, calCapacity(GTValues.IV)),

        EMPTY_TIER_III,
        QUANTUM_II(GTValues.LuV, calCapacity(GTValues.LuV)),
        QUANTUM_III(GTValues.ZPM, calCapacity(GTValues.ZPM)),
        QUANTUM_IV(GTValues.UV, calCapacity(GTValues.UV)),
        QUANTUM_V(GTValues.UHV, calCapacity(GTValues.UHV)),
        ;

        private final int tier;
        private final long capacity;

        TankPartType() {
            this.tier = -1;
            this.capacity = 0;
        }

        TankPartType(int tier, long capacity) {
            this.tier = tier;
            this.capacity = capacity;
        }

        private static long calCapacity(int tier) {
            return 4000000000L * (int) Math.pow(2, tier);
        }

        @Override
        public int getTier() {
            return tier;
        }

        @Override
        public long getCapacity() {
            return capacity;
        }


        // must be separately named because of reobf issue
        @NotNull
        @Override
        public String getTankName() {
            return name().toLowerCase();
        }

        @NotNull
        @Override
        public String getName() {
            return getTankName();
        }

        @NotNull
        public static TankPartType getTankPartTypeFromTier(int tier) {
            for (TankPartType type : values()) {
                if (type.tier == tier) {
                    return type;
                }
            }
            return EMPTY_TIER_I;
        }
    }
}
