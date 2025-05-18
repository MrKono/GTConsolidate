package kono.ceu.gtconsolidate.client;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.MODID;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;

@Mod.EventBusSubscriber(modid = MODID, value = Side.CLIENT)
public class GTConsolidateTextures {

    public static SimpleOverlayRenderer PARALLELIZED_ASSEMBLY_LINE_CASING;
    public static SimpleOverlayRenderer PARALLELIZED_ASSEMBLY_LINE_CONTROL;
    public static SimpleOverlayRenderer ASSEMBLY_LINE_CONTROL;

    public static SimpleOverlayRenderer FUSION_CASING;
    public static SimpleOverlayRenderer FUSION_CASING_MK2;
    public static SimpleOverlayRenderer FUSION_CASING_MK3;

    public static void preInit() {
        PARALLELIZED_ASSEMBLY_LINE_CASING = new SimpleOverlayRenderer(
                "casings/parallelized/assembly_line/parallelized_assembly_line_casing");
        PARALLELIZED_ASSEMBLY_LINE_CONTROL = new SimpleOverlayRenderer(
                "casings/parallelized/assembly_line/parallelized_assembly_line_control_casing");
        ASSEMBLY_LINE_CONTROL = new SimpleOverlayRenderer(
                "casings/mechanic/machine_casing_assembly_control");
        FUSION_CASING = new SimpleOverlayRenderer(
                "casings/fusion/machine_casing_fusion");
        FUSION_CASING_MK2 = new SimpleOverlayRenderer(
                "casings/fusion/machine_casing_fusion_2");
        FUSION_CASING_MK3 = new SimpleOverlayRenderer(
                "casings/fusion/machine_casing_fusion_3");
    }
}
