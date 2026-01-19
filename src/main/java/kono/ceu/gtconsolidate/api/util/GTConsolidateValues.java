package kono.ceu.gtconsolidate.api.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.Tags;
import kono.ceu.gtconsolidate.api.multiblock.ITankData;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class GTConsolidateValues {

    public static final String MODID = Tags.MODID;
    public static final String MODNAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;
    public static final Object2ObjectMap<IBlockState, ITankData> MULTIBLOCK_INTERNAL_TANKS = new Object2ObjectOpenHashMap();

    public static @NotNull ResourceLocation modId(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static @NotNull String mode() {
        return switch (GTConsolidateConfig.mode.mode) {
            case "NORMAL" -> "NORMAL";
            case "HARD" -> "HARD";
            default -> "EASY";
        };
    }

    public static @NotNull String timeUnit() {
        return switch (GTConsolidateConfig.feature.progressUnit) {
            case "min" -> "min";
            case "hr" -> "hr";
            default -> "sec";
        };
    }
}
