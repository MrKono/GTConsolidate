package kono.ceu.gtconsolidate;


import gregtech.GTInternalTags;
import kono.ceu.gtconsolidate.api.util.GTConsolidateValues;
import kono.ceu.gtconsolidate.api.util.Mods;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = GTConsolidateValues.MODID,
        name = GTConsolidateValues.MODNAME,
        version = GTConsolidateValues.VERSION,
        acceptedMinecraftVersions = "[1.12, 1.12.2]",
        dependencies = GTInternalTags.DEP_VERSION_STRING + "required-after:" + Mods.Names.GREGICALITY_MULTIBLOCKS + ";" +
                "after:" + Mods.Names.GREGTECH_FOOD_OPTION + ";")

public class GTConsolidate {

    @SidedProxy(modId = GTConsolidateValues.MODID,
            clientSide = "kono.ceu.materialreplication.client.ClientProxy",
            serverSide = "kono.ceu.materialreplication.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static GTConsolidate instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @SubscribeEvent
    public static void syncConfigValues(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equals(Tags.MODID)) {
            ConfigManager.sync(Tags.MODID, Config.Type.INSTANCE);
        }
    }
}
