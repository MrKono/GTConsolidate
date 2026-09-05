package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart.hpca;

import net.minecraft.util.ResourceLocation;

import gregtech.api.GTValues;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityHPCA;
import gregtech.common.metatileentities.multi.multiblockpart.hpca.MetaTileEntityHPCAComponent;

import kono.ceu.gtconsolidate.client.GTConsolidateTextures;

public class MetaTileEntityHPCAPowerOptimized extends MetaTileEntityHPCAComponent {

    private final boolean isAdvanced;

    public MetaTileEntityHPCAPowerOptimized(ResourceLocation metaTileEntityId, boolean isAdvanced) {
        super(metaTileEntityId);
        this.isAdvanced = isAdvanced;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHPCAPowerOptimized(metaTileEntityId, isAdvanced);
    }

    @Override
    public boolean isAdvanced() {
        return isAdvanced;
    }

    @Override
    public SimpleOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_EMPTY_OVERLAY;
    }

    @Override
    public TextureArea getComponentIcon() {
        return isAdvanced() ? GTConsolidateTextures.POWER_OPTIMIZED_ADVANCED : GTConsolidateTextures.POWER_OPTIMIZED;
    }

    @Override
    public int getUpkeepEUt() {
        int energy = isAdvanced() ? GTValues.VA[GTValues.LuV] : GTValues.VA[GTValues.IV];
        MultiblockControllerBase controllerBase = getController();
        if (controllerBase instanceof MetaTileEntityHPCA && controllerBase.isStructureFormed()) {
            return ((MetaTileEntityHPCA) controllerBase).getMaxCWUt() > 0 ? -energy : 0;
        }
        return -energy;
    }

    @Override
    public boolean canBeDamaged() {
        return false;
    }
}
