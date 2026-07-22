package kono.ceu.gtconsolidate.integration.top;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.ILaserContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.integration.theoneprobe.provider.LaserContainerInfoProvider;

import kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart.MetaTileEntityPassthroughHatchLaser;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;

public class LaserPassthroughHatchProvider extends LaserContainerInfoProvider {

    @Override
    public String getID() {
        return "gtconsolidate:laser_passthrough_hatch_info_provider";
    }

    @Override
    protected void addProbeInfo(@NotNull ILaserContainer capability, @NotNull IProbeInfo probeInfo,
                                EntityPlayer player, @NotNull TileEntity tileEntity, @NotNull IProbeHitData data) {
        if (tileEntity instanceof IGregTechTileEntity) {
            MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tileEntity).getMetaTileEntity();
            if (metaTileEntity instanceof MetaTileEntityPassthroughHatchLaser) {
                if (capability.inputsEnergy(data.getSideHit())) {
                    probeInfo.text(
                            TextStyleClass.INFO + TextFormatting.GOLD.toString() + "{*gregtech.top.transform_input*} " +
                                    TextFormatting.RESET + capability.getInputAmperage() + " A");
                } else if (capability.outputsEnergy(data.getSideHit())) {
                    probeInfo.text(TextStyleClass.INFO + TextFormatting.BLUE.toString() +
                            "{*gregtech.top.transform_output*} " + TextFormatting.RESET +
                            capability.getOutputAmperage() + " A");
                }
            }
        }
    }
}
