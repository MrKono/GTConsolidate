package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.*;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class MetaTileEntityComponentAssemblyLine extends RecipeMapMultiblockController {

    private int workTier;

    public MetaTileEntityComponentAssemblyLine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.COA_RECIPES);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity mte) {
        return new MetaTileEntityComponentAssemblyLine(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("iAAAIAAAi", "A##F#F##A", "A##OOO##A", "A##OOO##A", "A#######A", "A#######A", "AA#AAA#AA",
                        "$AAAAAAA$", "$$$AAA$$$", "$$$$$$$$$")
                .aisle("iAAAIAAAi", "G#######G", "G#######G", "G#######G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(3)
                .aisle("iAAAIAAAi", "GP#####PG", "GP#####PG", "GP#####PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("iAAAIAAAi", "G#######G", "G#######G", "G#######G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("iAAAIAAAi", "AP#####PA", "AP#####PA", "AP#####PA", "AP#####PA", "AP#####PA", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("iAAAIAAAi", "G#######G", "G#######G", "G#######G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("iAAAIAAAi", "GP#####PG", "GP#####PG", "GP#####PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("iAAAIAAAi", "G#######G", "G#######G", "G#######G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(3)
                .aisle("iAAAIAAAi", "A##F#F##A", "A##AAA##A", "A##AAA##A", "A#######A", "A#######A", "AA#AAA#AA",
                        "$AAASAAA$", "$$$AAA$$$", "$$$$$$$$$")
                .where('A', states(getCasingState1()))
                .where('B',
                        states(MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING)))
                .where('C', states(MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING)))
                .where('E',
                        abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(3).or(states(getCasingState1())))
                .where('F', frames(Materials.TungstenSteel))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS).or(states(getCasingState1())))
                .where('i',
                        abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(8)
                                .or(states(getCasingState1())))
                .where('O',
                        abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).or(states(getCasingState1())))
                .where('P',
                        states(MetaBlocks.BOILER_CASING
                                .getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE)))
                .where('#', air())
                .build();
    }

    public IBlockState getCasingState1() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING);
    }
}
