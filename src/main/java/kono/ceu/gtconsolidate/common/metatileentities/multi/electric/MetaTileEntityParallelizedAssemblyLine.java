package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static gregtech.api.util.RelativeDirection.*;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.energyHatchLimit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IDataAccessHatch;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.recipes.recipeproperties.ResearchProperty;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.client.particle.GTLaserBeamParticle;
import gregtech.client.particle.GTParticleManager;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiFluidHatch;
import gregtech.core.sound.GTSoundEvents;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;

import kono.ceu.gtconsolidate.client.GTConsolidateTextures;
import kono.ceu.gtconsolidate.common.blocks.*;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;

public class MetaTileEntityParallelizedAssemblyLine extends RecipeMapMultiblockController
                                                    implements IParallelMultiblock {

    private static final ResourceLocation LASER_LOCATION = GTUtility.gregtechId("textures/fx/laser/laser.png");
    private static final ResourceLocation LASER_HEAD_LOCATION = GTUtility
            .gregtechId("textures/fx/laser/laser_start.png");

    @SideOnly(Side.CLIENT)
    private GTLaserBeamParticle[][] beamParticles;
    private int beamCount;
    private int beamTime;
    private final int maxParallel;

    public MetaTileEntityParallelizedAssemblyLine(ResourceLocation metaTileEntityId, int maxParallel) {
        super(metaTileEntityId, RecipeMaps.ASSEMBLY_LINE_RECIPES);
        this.maxParallel = maxParallel;
        this.recipeMapWorkable = new GCYMMultiblockRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityParallelizedAssemblyLine(metaTileEntityId, maxParallel);
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int getMaxParallel() {
        return maxParallel;
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        FactoryBlockPattern pattern = FactoryBlockPattern.start(FRONT, UP, RIGHT)
                .aisle("FIF", "RTR", "SAG", " Y ")
                .aisle("FIF", "RTR", "DAG", " Y ").setRepeatable(3, 15)
                .aisle("FOF", "RTR", "DAG", " Y ")
                .where('S', selfPredicate())
                .where('F', states(getCasingState())
                        .or(autoAbilities(false, true, false, false, false, false, false))
                        .or(fluidInputPredicate()))
                .where('O', abilities(MultiblockAbility.EXPORT_ITEMS)
                        .addTooltips("gregtech.multiblock.pattern.location_end"))
                .where('Y', states(getCasingState())
                        .or(energyHatchLimit(false, maxParallel == 4, maxParallel != 64, maxParallel == 64)))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS))
                .where('G', states(getGrateState()))
                .where('A', states(getControlCasingState()))
                .where('R', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('T', states(getBaseCasingState()))
                .where('D', extraHatchPredicate())
                .where(' ', any());
        return pattern.build();
    }

    @NotNull
    protected static IBlockState getCasingState() {
        return MetaBlocks.MULTIBLOCK_CASING
                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_CONTROL);
    }

    @NotNull
    protected static IBlockState getBaseCasingState() {
        return GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                .getState(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CASING);
    }

    @NotNull
    protected static IBlockState getGrateState() {
        return MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING);
    }

    @NotNull
    protected static IBlockState getControlCasingState() {
        return GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                .getState(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CONTROL);
    }

    @NotNull
    protected static TraceabilityPredicate fluidInputPredicate() {
        // block multi-fluid hatches if ordered fluids is enabled
        if (ConfigHolder.machines.orderedFluidAssembly) {
            return metaTileEntities(MultiblockAbility.REGISTRY.get(MultiblockAbility.IMPORT_FLUIDS).stream()
                    .filter(mte -> !(mte instanceof MetaTileEntityMultiFluidHatch))
                    .toArray(MetaTileEntity[]::new))
                            .setMaxGlobalLimited(4);
        }
        return abilities(MultiblockAbility.IMPORT_FLUIDS);
    }

    @NotNull
    protected static TraceabilityPredicate extraHatchPredicate() {
        // if research is enabled, require the data hatch, otherwise use a grate instead
        if (ConfigHolder.machines.enableResearch) {
            return abilities(MultiblockAbility.DATA_ACCESS_HATCH, MultiblockAbility.OPTICAL_DATA_RECEPTION)
                    .setExactLimit(1)
                    .or(states(getGrateState()));
        }
        return states(getGrateState());
    }

    @Override
    protected Function<BlockPos, Integer> multiblockPartSorter() {
        // player's right when looking at the controller, but the controller's left
        return RelativeDirection.LEFT.getSorter(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null) {
            // part rendering
            if (sourcePart instanceof IDataAccessHatch) {
                return Textures.GRATE_CASING_STEEL_FRONT;
            } else {
                return GTConsolidateTextures.ASSEMBLY_LINE_CONTROL;
            }
        } else {
            // controller rendering
            if (isStructureFormed()) {
                return Textures.GRATE_CASING_STEEL_FRONT;
            } else {
                return GTConsolidateTextures.PARALLELIZED_ASSEMBLY_LINE_CONTROL;
            }
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        if (maxParallel == 64) {
            List<IEnergyContainer> inputEnergy = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
            inputEnergy.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
            this.energyContainer = new EnergyContainerList(inputEnergy);
        }
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_MECHANICAL;
    }

    @Override
    public void update() {
        super.update();
        if (ConfigHolder.client.shader.assemblyLineParticles) {
            if (getRecipeMapWorkable().isWorking()) {
                int maxBeams = getAbilities(MultiblockAbility.IMPORT_ITEMS).size() + 1;
                int maxProgress = getRecipeMapWorkable().getMaxProgress();

                // Each beam should be visible for an equal amount of time, which is derived from the maximum number of
                // beams and the maximum progress in the recipe.
                int beamTime = Math.max(1, maxProgress / maxBeams);

                int beamCount = Math.min(maxBeams, getRecipeMapWorkable().getProgress() / beamTime + 1);

                if (beamCount != this.beamCount) {
                    if (beamCount < this.beamCount) {
                        // if beam count decreases, the last beam in the queue needs to be removed for the sake of fade
                        // time.
                        this.beamCount = Math.max(0, beamCount - 1);
                        writeCustomData(GregtechDataCodes.UPDATE_PARTICLE, this::writeParticles);
                    }
                    this.beamTime = beamTime;
                    this.beamCount = beamCount;
                    writeCustomData(GregtechDataCodes.UPDATE_PARTICLE, this::writeParticles);
                }
            } else if (beamCount != 0) {
                this.beamTime = 0;
                this.beamCount = 0;
                writeCustomData(GregtechDataCodes.UPDATE_PARTICLE, this::writeParticles);
            }
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        writeParticles(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        readParticles(buf);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        if (dataId == GregtechDataCodes.UPDATE_PARTICLE) {
            readParticles(buf);
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if (getWorld().isRemote && beamParticles != null) {
            for (GTLaserBeamParticle[] particle : beamParticles) {
                if (particle[0] != null) {
                    particle[0].setExpired();
                    particle[1].setExpired();
                }
            }
            beamParticles = null;
        }
    }

    private void writeParticles(@NotNull PacketBuffer buf) {
        buf.writeVarInt(beamCount);
        buf.writeVarInt(beamTime);
    }

    @SideOnly(Side.CLIENT)
    private void readParticles(@NotNull PacketBuffer buf) {
        beamCount = buf.readVarInt();
        beamTime = buf.readVarInt();
        if (beamParticles == null) {
            beamParticles = new GTLaserBeamParticle[17][2];
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(getPos());

        EnumFacing relativeUp = RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());
        EnumFacing relativeLeft = RelativeDirection.LEFT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());
        boolean negativeUp = relativeUp.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;

        for (int i = 0; i < beamParticles.length; i++) {
            GTLaserBeamParticle particle = beamParticles[i][0];
            if (i < beamCount && particle == null) {
                pos.setPos(getPos());
                if (negativeUp) {
                    // correct for the position of the block corresponding to its negative side
                    pos.move(relativeUp.getOpposite());
                }
                Vector3 startPos = new Vector3()
                        .add(pos.move(relativeLeft, i))
                        .add( // offset by 0.5 in both non-"upwards" directions
                                relativeUp.getAxis() == EnumFacing.Axis.X ? 0 : 0.5,
                                relativeUp.getAxis() == EnumFacing.Axis.Y ? 0 : 0.5,
                                relativeUp.getAxis() == EnumFacing.Axis.Z ? 0 : 0.5);
                Vector3 endPos = startPos.copy()
                        .subtract(relativeUp.getXOffset(), relativeUp.getYOffset(), relativeUp.getZOffset());

                beamParticles[i][0] = createALParticles(startPos, endPos);

                pos.setPos(getPos());
                if (negativeUp) {
                    pos.move(relativeUp.getOpposite());
                }
                startPos = new Vector3()
                        .add(pos.move(relativeLeft, i)
                                .move(getFrontFacing().getOpposite(), 2))
                        .add( // offset by 0.5 in both non-"upwards" directions
                                relativeUp.getAxis() == EnumFacing.Axis.X ? 0 : 0.5,
                                relativeUp.getAxis() == EnumFacing.Axis.Y ? 0 : 0.5,
                                relativeUp.getAxis() == EnumFacing.Axis.Z ? 0 : 0.5);
                endPos = startPos.copy()
                        .subtract(relativeUp.getXOffset(), relativeUp.getYOffset(), relativeUp.getZOffset());

                beamParticles[i][1] = createALParticles(startPos, endPos);

                // Don't forget to add particles
                GTParticleManager.INSTANCE.addEffect(beamParticles[i][0]);
                GTParticleManager.INSTANCE.addEffect(beamParticles[i][1]);

            } else if (i >= beamCount && particle != null) {
                particle.setExpired();
                beamParticles[i][0] = null;
                beamParticles[i][1].setExpired();
                beamParticles[i][1] = null;
            }
        }
    }

    @NotNull
    @SideOnly(Side.CLIENT)
    private GTLaserBeamParticle createALParticles(Vector3 startPos, Vector3 endPos) {
        return new GTLaserBeamParticle(this, startPos, endPos, beamTime)
                .setBody(LASER_LOCATION)
                .setBeamHeight(0.125f)
                // Try commenting or adjusting on the next four lines to see what happens
                .setDoubleVertical(true)
                .setHead(LASER_HEAD_LOCATION)
                .setHeadWidth(0.1f)
                .setEmit(0.2f);
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        if (consumeIfSuccess) return true; // don't check twice
        // check ordered items
        if (ConfigHolder.machines.orderedAssembly) {
            List<GTRecipeInput> inputs = recipe.getInputs();
            List<IItemHandlerModifiable> itemInputInventory = getAbilities(MultiblockAbility.IMPORT_ITEMS);

            // slot count is not enough, so don't try to match it
            if (itemInputInventory.size() < inputs.size()) return false;

            for (int i = 0; i < inputs.size(); i++) {
                ItemStack topStack = itemInputInventory.get(i).getStackInSlot(0);
                if (!inputs.get(i).acceptsStack(topStack)) {
                    return false;
                }
                // Check the items in the remaining slots are the same as the item in the top slot
                for (int j = 1; j < itemInputInventory.get(i).getSlots(); j++) {
                    ItemStack remainStack = itemInputInventory.get(i).getStackInSlot(j);
                    if (!remainStack.isEmpty() && !remainStack.isItemEqual(topStack)) {
                        return false;
                    }
                }
            }

            // check ordered fluids
            if (ConfigHolder.machines.orderedFluidAssembly) {
                inputs = recipe.getFluidInputs();
                List<IFluidTank> fluidInputInventory = getAbilities(MultiblockAbility.IMPORT_FLUIDS);

                // slot count is not enough, so don't try to match it
                if (fluidInputInventory.size() < inputs.size()) return false;

                for (int i = 0; i < inputs.size(); i++) {
                    if (!inputs.get(i).acceptsFluid(fluidInputInventory.get(i).getFluid())) {
                        return false;
                    }
                }
            }
        }

        if (!ConfigHolder.machines.enableResearch || !recipe.hasProperty(ResearchProperty.getInstance())) {
            return super.checkRecipe(recipe, consumeIfSuccess);
        }

        return isRecipeAvailable(getAbilities(MultiblockAbility.DATA_ACCESS_HATCH), recipe) ||
                isRecipeAvailable(getAbilities(MultiblockAbility.OPTICAL_DATA_RECEPTION), recipe);
    }

    private static boolean isRecipeAvailable(@NotNull Iterable<? extends IDataAccessHatch> hatches,
                                             @NotNull Recipe recipe) {
        for (IDataAccessHatch hatch : hatches) {
            // creative hatches do not need to check, they always have the recipe
            if (hatch.isCreative()) return true;

            // hatches need to have the recipe available
            if (hatch.isRecipeAvailable(recipe)) return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("gtconsolidate.machine.parallelized_vf.tooltip.1"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.parallel", maxParallel));
        tooltip.add(I18n.format("gtconsolidate.machine.parallelized_vf.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                I18n.format(maxParallel == 4 ? "gtconsolidate.multiblock.tooltip.universal.limit.energy_in.4and16" :
                        maxParallel == 16 ? "gtconsolidate.multiblock.tooltip.universal.limit.energy_in.16" :
                                "gtconsolidate.multiblock.tooltip.universal.limit.energy_in.64")));
        if (ConfigHolder.machines.orderedAssembly && ConfigHolder.machines.orderedFluidAssembly) {
            tooltip.add(I18n.format("gregtech.machine.assembly_line.tooltip_ordered_both"));
            tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                    I18n.format("gtconsolidate.machine.parallelized_vf.limit")));
        } else if (ConfigHolder.machines.orderedAssembly) {
            tooltip.add(I18n.format("gregtech.machine.assembly_line.tooltip_ordered_items"));
            tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                    I18n.format("gtconsolidate.machine.parallelized_vf.limit")));
        } else if (ConfigHolder.machines.orderedFluidAssembly) {
            tooltip.add(I18n.format("gregtech.machine.assembly_line.tooltip_ordered_fluids"));
        }
    }
}
