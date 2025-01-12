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

    public static class IdSetting {

        @Config.Comment({ "Start ID for Multiblock Machine", "Dafault: 26000" })
        public int startMulti = 24000;
    }
}
