package kono.ceu.gtconsolidate.api.util;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.Tags;

public class GTConsolidateValues {

    public static final String MODID = Tags.MODID;
    public static final String MODNAME = Tags.MODNAME;
    public static final String VERSION = Tags.VERSION;

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
}
