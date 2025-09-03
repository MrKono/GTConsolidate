package kono.ceu.gtconsolidate.api.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.util.ModIncompatibilityException;

import kono.ceu.gtconsolidate.Tags;

public enum Mods {

    GregicalityMultiblocks(Names.GREGICALITY_MULTIBLOCKS),
    GregTech(Names.GREGTECH),
    GregTechFoodOption(Names.GREGTECH_FOOD_OPTION),
    GregTechExpertCore(Names.GREGTECH_EXPERT_CORE),
    GTConsolidate(Names.GTCONSOLIDATE),
    GTWoodProcessing(Names.GREGTECH_WOOD_PROCESSING),
    MixinBooter(Names.MIXINBOOTER),
    Vanilla(Names.VANILLA),

    // Special Optifine handler, but consolidated here for simplicity
    Optifine(null) {

        @Override
        public boolean isModLoaded() {
            if (this.modLoaded == null) {
                try {
                    Class<?> c = Class.forName("net.optifine.shaders.Shaders");
                    Field f = c.getDeclaredField("shaderPackLoaded");
                    f.setAccessible(true);
                    this.modLoaded = f.getBoolean(null);
                } catch (Exception ignored) {
                    this.modLoaded = false;
                }
            }
            return this.modLoaded;
        }
    };

    public static class Names {

        public static final String GREGICALITY_MULTIBLOCKS = "gcym";
        public static final String GREGTECH = "gregtech";
        public static final String GREGTECH_FOOD_OPTION = "gregtechfoodoption";
        public static final String GREGTECH_EXPERT_CORE = "gtexpert";
        public static final String GTCONSOLIDATE = Tags.MODID;
        public static final String GREGTECH_WOOD_PROCESSING = "gtwp";
        public static final String MIXINBOOTER = "mixinbooter";
        public static final String VANILLA = "minecraft";
    }

    private final String ID;
    private final Function<Mods, Boolean> extraCheck;
    protected Boolean modLoaded;

    Mods(String ID) {
        this.ID = ID;
        this.extraCheck = null;
    }

    /**
     * @param extraCheck A supplier that can be used to test additional factors, such as
     *                   checking if a mod is at a specific version, or a sub-mod is loaded.
     *                   Used in cases like NC vs NCO, where the mod id is the same
     *                   so the version has to be parsed to test which is loaded.
     *                   Another case is checking for specific Forestry modules, checking
     *                   if Forestry is loaded and if a specific module is enabled.
     */
    Mods(String ID, Function<Mods, Boolean> extraCheck) {
        this.ID = ID;
        this.extraCheck = extraCheck;
    }

    public boolean isModLoaded() {
        if (this.modLoaded == null) {
            this.modLoaded = Loader.isModLoaded(this.ID);
            if (this.modLoaded) {
                if (this.extraCheck != null && !this.extraCheck.apply(this)) {
                    this.modLoaded = false;
                }
            }
        }
        return this.modLoaded;
    }

    /**
     * Throw an exception if this mod is found to be loaded.
     * <strong>This must be called in or after
     * {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent}!</strong>
     */
    public void throwIncompatibilityIfLoaded(String... customMessages) {
        if (isModLoaded()) {
            String modName = TextFormatting.BOLD + ID + TextFormatting.RESET;
            List<String> messages = new ArrayList<>();
            messages.add(modName + " mod detected, this mod is incompatible with GregTech CE Unofficial.");
            messages.addAll(Arrays.asList(customMessages));
            if (FMLLaunchHandler.side() == Side.SERVER) {
                throw new RuntimeException(String.join(",", messages));
            } else {
                throwClientIncompatibility(messages);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void throwClientIncompatibility(List<String> messages) {
        throw new ModIncompatibilityException(messages);
    }

    public ItemStack getItem(@NotNull String name) {
        return getItem(name, 1, 0, null);
    }

    @NotNull
    public ItemStack getItem(@NotNull String name, int count) {
        return getItem(name, count, 0, null);
    }

    @NotNull
    public ItemStack getItem(@NotNull String name, int count, int meta) {
        return getItem(name, count, meta, null);
    }

    @NotNull
    public ItemStack getItem(@NotNull String name, int count, int meta, @Nullable String nbt) {
        // The following statement is intentional.
        return GameRegistry.makeItemStack(ID + ":" + name, meta, count, nbt);
    }

    @NotNull
    public ResourceLocation getResource(@NotNull String path) {
        return new ResourceLocation(ID, path);
    }

    // Helpers for the extra checker

    /** Test if the mod version string contains the passed value. */
    private static Function<Mods, Boolean> versionContains(String versionPart) {
        return mod -> {
            if (mod.ID == null) return false;
            if (!mod.isModLoaded()) return false;
            ModContainer container = Loader.instance().getIndexedModList().get(mod.ID);
            if (container == null) return false;
            return container.getVersion().contains(versionPart);
        };
    }

    /** Test if the mod version string does not contain the passed value. */
    private static Function<Mods, Boolean> versionExcludes(String versionPart) {
        return mod -> {
            if (mod.ID == null) return false;
            if (!mod.isModLoaded()) return false;
            ModContainer container = Loader.instance().getIndexedModList().get(mod.ID);
            if (container == null) return false;
            return !container.getVersion().contains(versionPart);
        };
    }
}
