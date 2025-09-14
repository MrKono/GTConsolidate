package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.InvalidBlockStateException;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtechfoodoption.GTFOConfig;
import gregtechfoodoption.block.GTFOGlassCasing;
import gregtechfoodoption.block.GTFOMetaBlocks;
import gregtechfoodoption.recipe.GTFORecipeMaps;
import gregtechfoodoption.utils.GTFOLog;

import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;

import kono.ceu.gtconsolidate.api.util.Logs;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;

public class MetaTileLargeGreenHouse extends GCYMRecipeMapMultiblockController {

    protected static IBlockState[] GRASSES;

    public MetaTileLargeGreenHouse(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTFORecipeMaps.GREENHOUSE_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileLargeGreenHouse(metaTileEntityId);
    }

    public static void addGrasses() {
        GRASSES = new IBlockState[GTFOConfig.gtfoMiscConfig.greenhouseDirts.length];
        boolean errorsFound = false;
        for (int i = 0; i < GTFOConfig.gtfoMiscConfig.greenhouseDirts.length; i++) {
            String blockStateString = GTFOConfig.gtfoMiscConfig.greenhouseDirts[i];
            try {
                IBlockState state;
                final String[] splitBlockStateString = StringUtils.split(blockStateString, "[");
                final String blockString = splitBlockStateString[0];
                final String stateString;
                if (splitBlockStateString.length == 1) {
                    stateString = "default";
                } else if (splitBlockStateString.length == 2) {
                    stateString = StringUtils.reverse(
                            StringUtils.reverse(StringUtils.split(blockStateString, "[")[1]).replaceFirst("]", ""));
                } else {
                    Logs.logger.error("Block/BlockState Parsing error for \"{}\"", blockStateString);
                    errorsFound = true;
                    continue;
                }

                final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockString));
                if (block == null) {
                    Logs.logger.error("Block Parsing error for \"{}\". Block does not exist!", blockString);
                    errorsFound = true;
                    continue;
                }
                try {
                    state = CommandBase.convertArgToBlockState(block, stateString);
                    GRASSES[i] = state;
                } catch (NumberInvalidException e) {
                    Logs.logger.error("BlockState Parsing error {} for \"{}\". Invalid Number!", e, stateString);
                    errorsFound = true;
                } catch (InvalidBlockStateException e) {
                    Logs.logger.error("BlockState Parsing error {} for \"{}\". Invalid BlockState!", e, stateString);
                    errorsFound = true;
                }
            } catch (Exception e) {
                GTFOLog.logger.error("Smoothable BlockState Parsing error " + e + " for \"" + blockStateString + "\"");
                errorsFound = true;
            }
        }
        if (errorsFound)
            throw new IllegalArgumentException(
                    "One or more errors were found with the Greenhouse Blocks configuration for GTFO. Check log above.");
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCCCCCC", "FGGGGGGGF", "FGGGGGGGF", "#FGGGGGF#", "#FGGGGGF#", "##FFPFF##", "####F####")
                .aisle("CDDDDDDDC", "G#######G", "GAAAAAAAG", "#GAAAAAG#", "#GAALAAG#", "##GGPGG##", "####F####")
                .setRepeatable(6)
                .aisle("CDDDDDDDC", "F#######F", "FAAAAAAAF", "#FAAAAAF#", "#FAALAAF#", "##FFTFF##", "####F####")
                .aisle("CDDDDDDDC", "G#######G", "GAAAAAAAG", "#GAAAAAG#", "#GAALAAG#", "##GGPGG##", "####F####")
                .setRepeatable(6)
                .aisle("CCCCSCCCC", "FGGGGGGGF", "FGGGGGGGF", "#FGGGGGF#", "#FGGGGGF#", "##FFPFF##", "####F####")
                .where('A', air())
                .where('C',
                        states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
                                .setMinGlobalLimited(20)
                                .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('D', states(Blocks.DIRT.getDefaultState(), Blocks.GRASS.getDefaultState()).or(states(GRASSES)))
                .where('F', frames(Materials.Steel))
                .where('G',
                        states(GTFOMetaBlocks.GTFO_GLASS_CASING.getState(GTFOGlassCasing.CasingType.GREENHOUSE_GLASS)))
                .where('L', lampPredicate())
                .where('S', selfPredicate())
                .where('T',
                        tieredCasing().or(states(
                                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE))))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
                .where('#', any())
                .build();
    }

    protected TraceabilityPredicate lampPredicate() {
        List<IBlockState> lamps = new ArrayList<>();
        for (int i = 0; i < Materials.CHEMICAL_DYES.length; i++) {
            EnumDyeColor color = EnumDyeColor.byMetadata(i);
            lamps.add(MetaBlocks.LAMPS.get(color).getStateFromMeta(0));
            lamps.add(MetaBlocks.LAMPS.get(color).getStateFromMeta(1));
            lamps.add(MetaBlocks.BORDERLESS_LAMPS.get(color).getStateFromMeta(0));
            lamps.add(MetaBlocks.BORDERLESS_LAMPS.get(color).getStateFromMeta(1));
        }
        return states(lamps.toArray(new IBlockState[0]));
    }

    @Override
    public boolean canBeDistinct() {
        return true;
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
    public boolean isMultiblockPartWeatherResistant(@NotNull IMultiblockPart part) {
        return true;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.large_greenhouse.tooltip.1"));
    }
}
