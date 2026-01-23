package kono.ceu.gtconsolidate.api.util;

import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.abilities;
import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.metaTileEntities;
import static kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks.COA_CASING;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.PatternStringError;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.BlockInfo;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.MetaTileEntities;

import kono.ceu.gtconsolidate.common.blocks.BlockCoACasing;

@SuppressWarnings("unused")
public class GTConsolidateTraceabilityPredicate {

    // TraceabilityPredicate
    @NotNull
    public static TraceabilityPredicate manualMaintenance() {
        return ConfigHolder.machines.enableMaintenance ?
                metaTileEntities(MetaTileEntities.MAINTENANCE_HATCH, MetaTileEntities.CONFIGURABLE_MAINTENANCE_HATCH)
                        .setExactLimit(1) :
                new TraceabilityPredicate();
    }

    @NotNull
    public static TraceabilityPredicate nonCleanMaintenance() {
        return ConfigHolder.machines.enableMaintenance ?
                metaTileEntities(MetaTileEntities.MAINTENANCE_HATCH, MetaTileEntities.CONFIGURABLE_MAINTENANCE_HATCH,
                        MetaTileEntities.AUTO_MAINTENANCE_HATCH).setExactLimit(1) :
                new TraceabilityPredicate();
    }

    @NotNull
    public static TraceabilityPredicate energyHatchLimit(boolean allow1A, boolean allow4A) {
        return energyHatchLimit(allow1A, allow4A, false);
    }

    @NotNull
    public static TraceabilityPredicate energyHatchLimit(boolean allow1A, boolean allow4A, boolean allow16A) {
        return energyHatchLimit(allow1A, allow4A, allow16A, false);
    }

    @NotNull
    public static TraceabilityPredicate energyHatchLimit(boolean allow1A, boolean allow4A, boolean allow16A,
                                                         boolean allow64A) {
        List<MetaTileEntity> energyHatch = new ArrayList<>();
        if (allow1A) energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH));
        if (allow4A) energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH_4A));
        if (allow16A) energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH_16A));
        if (allow64A) energyHatch.addAll(Arrays.asList(MetaTileEntities.SUBSTATION_ENERGY_INPUT_HATCH));
        return metaTileEntities(energyHatch.toArray(new MetaTileEntity[0])).setMinGlobalLimited(1)
                .setMaxGlobalLimited(3).setPreviewCount(2);
    }

    @NotNull
    public static TraceabilityPredicate primitiveItemInput() {
        return abilities(MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.STEAM_IMPORT_ITEMS);
    }

    @NotNull
    public static TraceabilityPredicate primitiveItemOutput() {
        return abilities(MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.STEAM_EXPORT_ITEMS);
    }

    @NotNull
    public static TraceabilityPredicate CoATieredCasing() {
        return new TraceabilityPredicate(blockWorldState -> {
            IBlockState blockState = blockWorldState.getBlockState();
            Block block = blockState.getBlock();
            if (block instanceof BlockCoACasing) {
                BlockCoACasing.CoACasingType casingType = ((BlockCoACasing) blockState.getBlock())
                        .getState(blockState);
                Object currentCasing = blockWorldState.getMatchContext().getOrPut("CasingTier", casingType);
                if (!currentCasing.equals(casingType)) {
                    blockWorldState
                            .setError(new PatternStringError("gtconsolidate.multiblock.pattern.error.coa_casings"));
                    return false;
                }
                blockWorldState.getMatchContext().getOrPut("VABlock", new LinkedList<>()).add(blockWorldState.getPos());
                return true;
            }
            return false;
        }, () -> ArrayUtils.addAll(
                Arrays.stream(BlockCoACasing.CoACasingType.values())
                        .map(type -> new BlockInfo(COA_CASING.getState(type), null))
                        .toArray(BlockInfo[]::new)))
                                .addTooltips("gtconsolidate.multiblock.pattern.error.coa_casings");
    }
}
