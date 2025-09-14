package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import com.github.gtexpert.gtwp.api.recipes.GTWPRecipeMaps;
import com.github.gtexpert.gtwp.client.GTWPTextures;
import com.github.gtexpert.gtwp.common.blocks.GTWPBlockMetalCasing;
import com.github.gtexpert.gtwp.common.blocks.GTWPMetaBlocks;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;

import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;

public class MetaTileEntityParallelizedSawmill extends GCYMRecipeMapMultiblockController {

    public MetaTileEntityParallelizedSawmill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTWPRecipeMaps.SAWMILL_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity entity) {
        return new MetaTileEntityParallelizedSawmill(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        TraceabilityPredicate casing = states(getCasingState());
        return FactoryBlockPattern.start()
                .aisle("ABBBA", "ABBBA", "DDDDD", "DDDDD")
                .aisle("ACCCA", "G###G", "DEEED", "DDDDD").setRepeatable(7)
                .aisle("ABBBA", "SBBBA", "DDDDD", "DDDDD")
                .where('A', casing)
                .where('B',
                        casing.or(abilities(MultiblockAbility.IMPORT_ITEMS))
                                .or(abilities(MultiblockAbility.IMPORT_FLUIDS)))
                .where('C', blocks(GTWPMetaBlocks.BLOCK_SAWMILL_CONVEYOR))
                .where('D', casing
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(6, 1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(6, 0))
                        .or(autoAbilities(true, true, false, false, false, false, false)))
                .where('E',
                        states(GCYMMetaBlocks.UNIQUE_CASING
                                .getState(BlockUniqueCasing.UniqueCasingType.SLICING_BLADES)))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS)))
                .where('S', selfPredicate())
                .where('#', air())
                .build();
    }

    @Override
    public boolean isTiered() {
        return false;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTWPTextures.SAWMILL_CASING;
    }

    protected IBlockState getCasingState() {
        return GTWPMetaBlocks.GTWP_METAL_CASING.getState(GTWPBlockMetalCasing.MetalCasingType.SAWMill);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()));
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        builder.addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return GTWPTextures.SAWMILL_OVERLAY;
    }
}
