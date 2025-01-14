package kono.ceu.gtconsolidate.common.machines;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.modId;

import gregtech.api.GTValues;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.common.machines.multi.MetaTileEntityParallelizedEBF;
import kono.ceu.gtconsolidate.common.machines.multi.MetaTileEntityParallelizedFusionReactor;

public class GTConsolidateMetaTileEntity {

    public static final MetaTileEntityParallelizedFusionReactor[] PARALLELIZED_FUSION_REACTOR = new MetaTileEntityParallelizedFusionReactor[3];
    public static MetaTileEntityParallelizedEBF[] PARALLELIZED_EBF = new MetaTileEntityParallelizedEBF[2];

    public static void init() {
        registerMultiMachine();
    }

    public static void registerMultiMachine() {
        int id = GTConsolidateConfig.id.startMulti;

        PARALLELIZED_FUSION_REACTOR[0] = registerMetaTileEntity(id, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_luv"), GTValues.UV));
        PARALLELIZED_FUSION_REACTOR[1] = registerMetaTileEntity(id + 1, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_zpm"), GTValues.UHV));
        PARALLELIZED_FUSION_REACTOR[2] = registerMetaTileEntity(id + 2, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_uv"), GTValues.UEV));

        PARALLELIZED_EBF[0] = registerMetaTileEntity(id + 3, new MetaTileEntityParallelizedEBF(
                modId("advanced_electric_blast_furnace"), 4));
        PARALLELIZED_EBF[1] = registerMetaTileEntity(id + 4, new MetaTileEntityParallelizedEBF(
                modId("elite_electric_blast_furnace"), 16));
    }
}
