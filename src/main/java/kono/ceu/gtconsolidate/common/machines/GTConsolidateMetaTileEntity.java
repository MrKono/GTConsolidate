package kono.ceu.gtconsolidate.common.machines;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.modId;

import gregtech.api.GTValues;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.common.machines.multi.*;
import kono.ceu.gtconsolidate.common.machines.multi.MetaTileEntityCompressedFusionReactor;

public class GTConsolidateMetaTileEntity {

    public static final MetaTileEntityCompressedFusionReactor[] COMPRESSED_FUSION_REACTOR = new MetaTileEntityCompressedFusionReactor[3];

    public static void init() {
        registerMultiMachine();
    }

    public static void registerMultiMachine() {
        int id = GTConsolidateConfig.id.startMulti;

        COMPRESSED_FUSION_REACTOR[0] = registerMetaTileEntity(id, new MetaTileEntityCompressedFusionReactor(
                modId("compressed_fusion_reactor_luv"), GTValues.UV));
        COMPRESSED_FUSION_REACTOR[1] = registerMetaTileEntity(id + 1, new MetaTileEntityCompressedFusionReactor(
                modId("compressed_fusion_reactor_zpm"), GTValues.UHV));
        COMPRESSED_FUSION_REACTOR[2] = registerMetaTileEntity(id + 2, new MetaTileEntityCompressedFusionReactor(
                modId("compressed_fusion_reactor_uv"), GTValues.UEV));
    }
}
