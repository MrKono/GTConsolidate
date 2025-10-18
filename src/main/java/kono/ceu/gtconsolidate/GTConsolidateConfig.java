package kono.ceu.gtconsolidate;

import net.minecraftforge.common.config.Config;

import kono.ceu.gtconsolidate.api.util.GTConsolidateValues;

@Config(modid = GTConsolidateValues.MODID)
public class GTConsolidateConfig {

    @Config.Name("ID Setting")
    @Config.Comment({ "Setting for MetaTileEntityID", "Use in case of duplicates",
            "WARNING: Changing the value may cause the machine to disappear " })
    @Config.RequiresMcRestart
    public static IdSetting id = new IdSetting();

    @Config.Name("Mode Setting")
    @Config.RequiresMcRestart
    public static ModeSetting mode = new ModeSetting();

    @Config.Name("Feature Setting")
    @Config.RequiresMcRestart
    public static FeatureSetting feature = new FeatureSetting();

    public static class IdSetting {

        @Config.Comment({ "Start ID for Multiblock Machine", "Default: 21500" })
        public int startMulti = 21500;
    }

    public static class ModeSetting {

        @Config.Comment({ "Mode of this mod", "Valid: EASY, NORMAL, HARD", "Default: EASY" })
        public String mode = "EASY";

        @Config.Comment({ "If true, MufflerHatch can be used in the Cleanroom.", "Default: false" })
        public boolean modifyCR = false;

        @Config.Comment({ "Minimum tier of MufflerHatch available within Cleanroom.",
                "This does nothing if B:modifyCR is false.", "Default: 8 (UHV)" })
        @Config.RangeInt(min = 1, max = 8)
        public int tierMuffler = 8;

        @Config.Comment({ "Whether to generate T1 (Electronic) and T2 (Integrated) circuit recipe", "Default: false" })
        public boolean generateLowTierCircuitRecipe = false;
    }

    public static class FeatureSetting {

        @Config.Comment({ "Whether to add more Parallel Hatch.",
                "1024+ Parallel Hatches were added", "Default: false" })
        public boolean addMoreParallel = false;

        @Config.Comment({ "Whether to display the time in the \"Progress\" line of the Multiblock Controller.",
                "This change applies only to Multiblock in GTConsolidate.", "Default: false" })
        public boolean modifyProgressLine = false;

        @Config.Comment({ "Display settings for \"Progress\".", "Only effective when modifyProgressLine is true.",
                "Valid: sec, min, hr", "Default: sec" })
        public String progressUnit = "sec";

        @Config.Comment({
                "Whether to display the current parallel in the \"Max Parallel\" line of the Multiblock Controller.",
                "This change applies only to Multiblock in GTConsolidate.", "Default: false" })
        public boolean modifyParallelLine = false;

        @Config.Comment({ "Whether to add the \"Output\" line of the Multiblock Controller.",
                "This change applies only to Multiblock in GTConsolidate.", "Default: false" })
        public boolean addOutputLine = false;
    }
}
