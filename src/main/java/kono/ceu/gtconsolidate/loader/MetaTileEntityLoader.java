package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;
import static kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity.*;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

import kono.ceu.gtconsolidate.GTConsolidateConfig;

public class MetaTileEntityLoader {

    private static final boolean addLowTier = GTConsolidateConfig.feature.addLowTierRotorHolders;
    private static final boolean addHighTier = GTConsolidateConfig.feature.addHighTierRotorHolders;

    public static void init() {
        // Filtered Input Bus
        for (int i = 0; i < FILTERED_ITEM_INPUT.length; i++) {
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(MetaTileEntities.ITEM_IMPORT_BUS[i])
                    .input(MetaItems.ITEM_FILTER)
                    .input(OrePrefix.circuit, MarkerMaterials.Tier.HV, 2)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144))
                    .output(FILTERED_ITEM_INPUT[i])
                    .duration(200).EUt(VA[EV]).buildAndRegister();
        }
        // More Parallel Hatch
        if (GTConsolidateConfig.feature.addMoreParallel) {
            RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                    .input(MetaItems.ROBOT_ARM_LV, 64)
                    .input(MetaItems.EMITTER_LV, 64)
                    .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 64)
                    .input(MetaTileEntities.HULL[UHV])
                    .input(OrePrefix.wireGtHex, Materials.MagnesiumDiboride, 64)
                    .input(OrePrefix.wireGtHex, Materials.MagnesiumDiboride, 64)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(9 * L))
                    .stationResearch(b -> b
                            .researchStack(GCYMMetaTileEntities.PARALLEL_HATCH[UV - IV].getStackForm())
                            .CWUt(32, 6400)
                            .EUt(VA[UEV]))
                    .output(MORE_PARALLEL_HATCHES[0])
                    .duration(20 * 120).EUt(VA[UEV]).buildAndRegister();
            for (int i = 1; i < MORE_PARALLEL_HATCHES.length; i++) {
                int j = i;
                RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                        .input(robotArm(i + 1), 64)
                        .input(emitter(i + 1), 64)
                        .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 64)
                        .input(MetaTileEntities.HULL[UHV])
                        .input(OrePrefix.wireGtHex, scMaterial(i + 2), 64)
                        .input(OrePrefix.wireGtHex, scMaterial(i + 2), 64)
                        .fluidInputs(Materials.SolderingAlloy.getFluid((i + 1) * 9 * L))
                        .stationResearch(b -> b
                                .researchStack(MORE_PARALLEL_HATCHES[j - 1].getStackForm())
                                .CWUt(32 * (j + 1), (int) Math.min(V[j + 1] * 200L, V[MAX]))
                                .EUt(VA[UEV]))
                        .output(MORE_PARALLEL_HATCHES[i])
                        .duration(20 * 120).EUt(VA[UEV]).buildAndRegister();
            }
        }
        // Rotor Holders
        if (addLowTier) {
            ModHandler.addShapedRecipe(true, "rotor_holder_lv",
                    ROTOR_HOLDERS_LOW[0].getStackForm(), "gGg", "GHG", "gGg",
                    'g', new UnificationEntry(OrePrefix.gearSmall, Materials.Steel),
                    'G', new UnificationEntry(OrePrefix.gear, Materials.Steel),
                    'H', MetaTileEntities.HULL[1].getStackForm());
            ModHandler.addShapedRecipe(true, "rotor_holder_mv",
                    ROTOR_HOLDERS_LOW[1].getStackForm(), "gGg", "GHG", "gGg",
                    'g', new UnificationEntry(OrePrefix.gearSmall, Materials.Aluminium),
                    'G', new UnificationEntry(OrePrefix.gear, Materials.Aluminium),
                    'H', MetaTileEntities.HULL[2].getStackForm());
        }
        if (addHighTier) {
            ModHandler.addShapedRecipe(true, "rotor_holder_uhv",
                    ROTOR_HOLDERS_HI[0].getStackForm(), "gGg", "GHG", "gGg",
                    'g', new UnificationEntry(OrePrefix.gearSmall, Materials.Neutronium),
                    'G', new UnificationEntry(OrePrefix.gear, Materials.Americium),
                    'H', MetaTileEntities.HULL[GTValues.UHV].getStackForm());
        }
        int start = addLowTier ? GTValues.LV - 1 : GTValues.HV - 1;
        int end = addHighTier ? GTValues.UHV : GTValues.UV;
        Material[] small = new Material[] { Materials.Steel, Materials.Aluminium, Materials.StainlessSteel,
                Materials.Titanium, Materials.TungstenSteel, Materials.RhodiumPlatedPalladium, Materials.NaquadahAlloy,
                Materials.Darmstadtium, Materials.Neutronium };
        Material[] normal = new Material[] { Materials.Steel, Materials.Aluminium, Materials.BlackSteel,
                Materials.Ultimet, Materials.HSSG, Materials.Ruthenium, Materials.Trinium, Materials.Tritanium,
                Materials.Americium };
        MetaItem<?>.MetaValueItem[] powerCircuit = new MetaItem<?>.MetaValueItem[] {
                MetaItems.ULTRA_LOW_POWER_INTEGRATED_CIRCUIT, MetaItems.ULTRA_LOW_POWER_INTEGRATED_CIRCUIT,
                MetaItems.LOW_POWER_INTEGRATED_CIRCUIT, MetaItems.POWER_INTEGRATED_CIRCUIT,
                MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT, MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT,
                MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT,
                MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT };
        for (int i = start; i < end; i++) {
            if (GTConsolidateConfig.feature.addPowerEnhancedRotorHolders) {
                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                        .input(OrePrefix.gear, small[i], 4)
                        .input(OrePrefix.gear, normal[i], 16)
                        .input(powerCircuit[i], 8)
                        .input(MetaTileEntities.HULL[i + 1])
                        .fluidInputs(Materials.Lubricant.getFluid(250 * (i + 1)))
                        .output(ROTOR_HOLDER_POWERED[i])
                        .EUt(VA[GTValues.LuV]).duration(20 * 30).buildAndRegister();
            }
            if (GTConsolidateConfig.feature.addSpeedEnhancedRotorHolders) {
                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                        .input(OrePrefix.gearSmall, small[i], 8)
                        .input(OrePrefix.gearSmall, normal[i], 32)
                        .input(OrePrefix.circuit, markerMaterial(i + 1), 2)
                        .input(MetaTileEntities.HULL[i + 1])
                        .fluidInputs(Materials.Lubricant.getFluid(2000 * (i + 1)))
                        .output(ROTOR_HOLDER_SPEEDED[i])
                        .EUt(VA[GTValues.IV]).duration(20 * 30).buildAndRegister();
            }
        }
    }
}
