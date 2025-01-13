package kono.ceu.gtconsolidate.api.util;

import static gregtech.api.metatileentity.multiblock.MultiblockControllerBase.metaTileEntities;

import java.util.Arrays;
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

    public static TraceabilityPredicate limit4A() {
        return metaTileEntities(Stream
                .concat(Arrays.stream(MetaTileEntities.ENERGY_INPUT_HATCH_4A),
                        Arrays.stream(MetaTileEntities.ENERGY_INPUT_HATCH_16A))
                .toArray(MetaTileEntity[]::new));
    }
}
