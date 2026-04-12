package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

import kono.ceu.gtconsolidate.client.GTConsolidateTextures;
import kono.ceu.gtconsolidate.common.blocks.BlockMultiblockCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityParallelizedImplosionCompressor extends GCYMRecipeMapMultiblockController {

    public MetaTileEntityParallelizedImplosionCompressor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GCYMMetaTileEntities.ELECTRIC_IMPLOSION_COMPRESSOR.getRecipeMap());
        this.recipeMapWorkable = new ParallelizedICRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityParallelizedImplosionCompressor(metaTileEntityId);
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public boolean isTiered() {
        return false;
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        List<IEnergyContainer> inputEnergy = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputEnergy.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
        this.energyContainer = new EnergyContainerList(inputEnergy);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXXXX", "#######", "#######", "#######", "#######", "#######", "XXXXXXX")
                .aisle("XXXXXXX", "#FGPGF#", "#CGPGC#", "#CGPGC#", "#CGPGC#", "#FGPGF#", "XXXXXXX")
                .aisle("XXXXXXX", "#GGGGG#", "#GFGFG#", "#GGCGG#", "#GFGFG#", "#GGGGG#", "XXXXXXX")
                .aisle("XXXXXXX", "#P#C#P#", "#PGCGP#", "#PCACP#", "#PGCGP#", "#P#C#P#", "XXXMXXX")
                .aisle("XXXXXXX", "#GGGGG#", "#GFGFG#", "#GGCGG#", "#GFGFG#", "#GGGGG#", "XXXXXXX")
                .aisle("XXXXXXX", "#FGPGF#", "#CGPGC#", "#CGPGC#", "#CGPGC#", "#FGPGF#", "XXXXXXX")
                .aisle("XXXSXXX", "#######", "#######", "#######", "#######", "#######", "XXXXXXX")
                .where('S', selfPredicate())
                .where('X',
                        states(GTConsolidateMetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.NEUTRONIUM_STURDY))
                                        .setMinGlobalLimited(70)
                                        .or(autoAbilities(false, true, true, true, false, false, false))
                                        .or(energyHatchPredicate()))
                .where('F', frames(Materials.Neutronium))
                .where('P',
                        states(MetaBlocks.BOILER_CASING
                                .getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE)))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('C', states(MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3)))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('#', any())
                .where('A', air())
                .build();
    }

    private TraceabilityPredicate energyHatchPredicate() {
        List<MetaTileEntity> energyHatch = new ArrayList<>();
        energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH_4A));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH_16A));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.SUBSTATION_ENERGY_INPUT_HATCH));
        return metaTileEntities(energyHatch.toArray(new MetaTileEntity[0])).setMinGlobalLimited(1)
                .setMaxGlobalLimited(4).setPreviewCount(2);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTConsolidateTextures.NEUTRONIUM_STURDY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gtconsolidate.machine.quantum_compressor.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.quantum_compressor.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.multiblock.accept_64a"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    private class ParallelizedICRecipeLogic extends GCYMMultiblockRecipeLogic {

        public ParallelizedICRecipeLogic(MetaTileEntityParallelizedImplosionCompressor tileEntity) {
            super(tileEntity);
        }

        @Override
        public long getMaxVoltage() {
            IEnergyContainer energyContainer = getEnergyContainer();
            if (!consumesEnergy()) {
                // Generators; Is it needed??
                long voltage = energyContainer.getOutputVoltage();
                long amperage = energyContainer.getOutputAmperage();
                if (energyContainer instanceof EnergyContainerList && amperage == 1) {
                    return GTValues.V[GTUtility.getFloorTierByVoltage(voltage)];
                }
                return voltage;
            } else {
                List<IEnergyContainer> energyContainers = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
                energyContainers.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
                if (!energyContainers.isEmpty()) {
                    long maxVoltage;
                    int amp = 0;
                    int amount;
                    if (energyContainer instanceof EnergyContainerList energyContainerList) {
                        maxVoltage = energyContainerList.getHighestInputVoltage();
                        amount = energyContainerList.getNumHighestInputContainers();
                    } else {
                        maxVoltage = energyContainer.getInputVoltage();
                        amount = 1;
                    }

                    int tier = GTUtility.getFloorTierByVoltage(maxVoltage);
                    if (amount > 1) {
                        for (IEnergyContainer container : energyContainers) {
                            long voltage = container.getInputVoltage();
                            if (voltage >= maxVoltage) {
                                amp += (int) container.getInputAmperage();
                            }
                        }

                        int numMaxAmperage = Integer.numberOfTrailingZeros(Integer.highestOneBit(amp)) / 2;
                        return GTValues.V[tier + numMaxAmperage];
                    }
                }
                return energyContainer.getInputVoltage();
            }
        }
    }
}
