package kono.ceu.gtconsolidate.api.util;

import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.metaTileEntities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.MetaTileEntities;

import kono.ceu.gtconsolidate.Tags;

public class GTConsolidateValues {

    public static final String MODID = Tags.MODID;
    public static final String MODNAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;

    public static @NotNull ResourceLocation modId(String path) {
        return new ResourceLocation(MODID, path);
    }

    // For Multiblocks
    public static TraceabilityPredicate manualMaintenance() {
        return ConfigHolder.machines.enableMaintenance ?
                metaTileEntities(MetaTileEntities.MAINTENANCE_HATCH, MetaTileEntities.CONFIGURABLE_MAINTENANCE_HATCH)
                        .setExactLimit(1) :
                new TraceabilityPredicate();
    }

    public static TraceabilityPredicate energyHatchLimit(boolean allow1A, boolean allow4A) {
        return energyHatchLimit(allow1A, allow4A, false);
    }

    public static TraceabilityPredicate energyHatchLimit(boolean allow1A, boolean allow4A, boolean allow16A) {
        return energyHatchLimit(allow1A, allow4A, allow16A, false);
    }

    public static TraceabilityPredicate energyHatchLimit(boolean allow1A, boolean allow4A, boolean allow16A, boolean allow64A) {
        List<MetaTileEntity> energyHatch = new ArrayList<>();
        if (allow1A) energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH));
        if (allow4A) energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH_4A));
        if (allow16A) energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH_16A));
        if (allow64A) energyHatch.addAll(Arrays.asList(MetaTileEntities.SUBSTATION_ENERGY_INPUT_HATCH));
        return metaTileEntities(energyHatch.toArray(new MetaTileEntity[0]));
    }
}
