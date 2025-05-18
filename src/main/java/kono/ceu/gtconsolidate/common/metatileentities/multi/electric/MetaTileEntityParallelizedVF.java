package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.*;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;

import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class MetaTileEntityParallelizedVF extends RecipeMapMultiblockController implements IParallelMultiblock {

    private final int maxParallel;

    public MetaTileEntityParallelizedVF(ResourceLocation metaTileEntityId, int maxParallel) {
        super(metaTileEntityId, RecipeMaps.VACUUM_RECIPES);
        this.maxParallel = maxParallel;
        this.recipeMapWorkable = new GCYMMultiblockRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityParallelizedVF(metaTileEntityId, maxParallel);
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int getMaxParallel() {
        return maxParallel;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "X#X", "XXX")
                .aisle("XXX", "XSX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(14)
                        .or(autoAbilities(false, false, true, true, true, true, false))
                        .or(manualMaintenance())
                        .or(energyHatchLimit(false, maxParallel == 4, true).setMinGlobalLimited(1)
                                .setMaxGlobalLimited(2)))
                .where('#', modePredicate())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.FROST_PROOF_CASING;
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
    }

    public TraceabilityPredicate modePredicate() {
        TraceabilityPredicate predicate;
        IBlockState heliumBasic = GTConsolidateMetaBlocks.COOLANT_CASING
                .getState(BlockCoolantCasing.CasingType.HELIUM_BASIC);
        IBlockState heliumAdvanced = GTConsolidateMetaBlocks.COOLANT_CASING
                .getState(BlockCoolantCasing.CasingType.HELIUM_ADVANCED);
        IBlockState heliumElite = GTConsolidateMetaBlocks.COOLANT_CASING
                .getState(BlockCoolantCasing.CasingType.HELIUM_ELITE);
        predicate = switch (mode()) {
            case "NORMAL" -> maxParallel == 4 ? states(heliumBasic) : states(heliumAdvanced);
            case "HARD" -> maxParallel == 4 ? states(heliumAdvanced) : states(heliumElite);
            default -> maxParallel == 4 ? air() : states(heliumBasic);
        };
        return predicate;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.VACUUM_FREEZER_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.parallelized_vf.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.parallel", maxParallel));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                maxParallel == 4 ? I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.energy_in.4and16") :
                        I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.energy_in.16")));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.manual_maintenance")));
    }
}
