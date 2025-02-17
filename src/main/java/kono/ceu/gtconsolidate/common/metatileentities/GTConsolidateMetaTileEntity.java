package kono.ceu.gtconsolidate.common.metatileentities;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.modId;

import gregtech.api.GTValues;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.common.metatileentities.multi.electric.MetaTileEntityParallelizedAssemblyLine;
import kono.ceu.gtconsolidate.common.metatileentities.multi.electric.MetaTileEntityParallelizedEBF;
import kono.ceu.gtconsolidate.common.metatileentities.multi.electric.MetaTileEntityParallelizedFusionReactor;
import kono.ceu.gtconsolidate.common.metatileentities.multi.electric.MetaTileEntityParallelizedVF;

public class GTConsolidateMetaTileEntity {

    // Multiblock
    public static final MetaTileEntityParallelizedFusionReactor[] PARALLELIZED_FUSION_REACTOR = new MetaTileEntityParallelizedFusionReactor[3];
    public static final MetaTileEntityParallelizedEBF[] PARALLELIZED_EBF = new MetaTileEntityParallelizedEBF[2];
    public static final MetaTileEntityParallelizedVF[] PARALLELIZED_VF = new MetaTileEntityParallelizedVF[2];
    public static final MetaTileEntityParallelizedAssemblyLine[] PARALLELIZED_ASSEMBLY_LINE = new MetaTileEntityParallelizedAssemblyLine[3];

    public static void init() {
        registerMultiMachine();
        registerMultiblockPart();
    }

    public static void registerMultiMachine() {
        int id = GTConsolidateConfig.id.startMulti;

        // Parallelized Fusion Reactor
        PARALLELIZED_FUSION_REACTOR[0] = registerMetaTileEntity(id, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_luv"), GTValues.UV));
        PARALLELIZED_FUSION_REACTOR[1] = registerMetaTileEntity(id + 1, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_zpm"), GTValues.UHV));
        PARALLELIZED_FUSION_REACTOR[2] = registerMetaTileEntity(id + 2, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_uv"), GTValues.UEV));
        // Parallelized Electric Blast Furnace
        PARALLELIZED_EBF[0] = registerMetaTileEntity(id + 3, new MetaTileEntityParallelizedEBF(
                modId("advanced_electric_blast_furnace"), 4));
        PARALLELIZED_EBF[1] = registerMetaTileEntity(id + 4, new MetaTileEntityParallelizedEBF(
                modId("elite_electric_blast_furnace"), 16));
        // Parallelized Vacuum Freezer
        PARALLELIZED_VF[0] = registerMetaTileEntity(id + 5, new MetaTileEntityParallelizedVF(
                modId("advanced_vacuum_freezer"), 4));
        PARALLELIZED_VF[1] = registerMetaTileEntity(id + 6, new MetaTileEntityParallelizedVF(
                modId("elite_vacuum_freezer"), 16));

        // Parallelized Assembly Line
        PARALLELIZED_ASSEMBLY_LINE[0] = registerMetaTileEntity(id + 7, new MetaTileEntityParallelizedAssemblyLine(
                modId("parallelized_assembly_line_mk1"), 4));
        PARALLELIZED_ASSEMBLY_LINE[1] = registerMetaTileEntity(id + 8, new MetaTileEntityParallelizedAssemblyLine(
                modId("parallelized_assembly_line_mk2"), 16));
        PARALLELIZED_ASSEMBLY_LINE[2] = registerMetaTileEntity(id + 9, new MetaTileEntityParallelizedAssemblyLine(
                modId("parallelized_assembly_line_mk3"), 64));
    }

    public static void registerMultiblockPart() {}
}
