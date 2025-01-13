package kono.ceu.gtconsolidate.common.machines;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.modId;

import gregtech.api.GTValues;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.common.machines.multi.MetaTileEntityAdvancedFusionReactor;

public class GTConsolidateMetaTileEntity {

    public static final MetaTileEntityAdvancedFusionReactor[] ADVANCED_FUSION_REACTOR = new MetaTileEntityAdvancedFusionReactor[3];

    public static void init() {
        registerMultiMachine();
    }

    public static void registerMultiMachine() {
        int id = GTConsolidateConfig.id.startMulti;

        ADVANCED_FUSION_REACTOR[0] = registerMetaTileEntity(id, new MetaTileEntityAdvancedFusionReactor(
                modId("advanced_fusion_reactor_luv"), GTValues.UV));
        ADVANCED_FUSION_REACTOR[1] = registerMetaTileEntity(id + 1, new MetaTileEntityAdvancedFusionReactor(
                modId("advanced_fusion_reactor_zpm"), GTValues.UHV));
        ADVANCED_FUSION_REACTOR[2] = registerMetaTileEntity(id + 2, new MetaTileEntityAdvancedFusionReactor(
                modId("advanced_fusion_reactor_uv"), GTValues.UEV));
    }
}
