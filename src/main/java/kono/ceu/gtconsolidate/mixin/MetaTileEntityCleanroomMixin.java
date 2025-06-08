package kono.ceu.gtconsolidate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gregtech.api.capability.IMufflerHatch;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.multiblock.FuelMultiblockController;
import gregtech.common.metatileentities.multi.MetaTileEntityCokeOven;
import gregtech.common.metatileentities.multi.MetaTileEntityPrimitiveBlastFurnace;
import gregtech.common.metatileentities.multi.MetaTileEntityPrimitiveWaterPump;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityCleanroom;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityFluidDrill;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityLargeMiner;
import gregtech.common.metatileentities.multi.electric.centralmonitor.MetaTileEntityCentralMonitor;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;

import kono.ceu.gtconsolidate.GTConsolidateConfig;

@Mixin(value = MetaTileEntityCleanroom.class, remap = false)
public class MetaTileEntityCleanroomMixin {

    /**
     * @author ko_no
     * @reason
     */
    @Overwrite
    protected boolean isMachineBanned(MetaTileEntity metaTileEntity) {
        if (metaTileEntity instanceof IMufflerHatch) {
            if (GTConsolidateConfig.mode.modifyCR) {
                if (metaTileEntity instanceof MetaTileEntityMufflerHatch) {
                    int tier = ((MetaTileEntityMufflerHatch) metaTileEntity).getTier();
                    return tier < GTConsolidateConfig.mode.tierMuffler;
                }
            } else {
                return true;
            }
        }
        if (metaTileEntity instanceof SimpleGeneratorMetaTileEntity) return true;
        if (metaTileEntity instanceof FuelMultiblockController) return true;
        if (metaTileEntity instanceof MetaTileEntityLargeMiner) return true;
        if (metaTileEntity instanceof MetaTileEntityFluidDrill) return true;
        if (metaTileEntity instanceof MetaTileEntityCentralMonitor) return true;
        if (metaTileEntity instanceof MetaTileEntityCleanroom) return true;
        if (metaTileEntity instanceof MetaTileEntityCokeOven) return true;
        if (metaTileEntity instanceof MetaTileEntityPrimitiveBlastFurnace) return true;
        return metaTileEntity instanceof MetaTileEntityPrimitiveWaterPump;
    }
}
