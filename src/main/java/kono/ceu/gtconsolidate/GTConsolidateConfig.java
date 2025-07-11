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
    }
}
