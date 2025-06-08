package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.unification.ore.OrePrefix.*;
import static kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps.COA_RECIPES;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaItems;

import kono.ceu.gtconsolidate.api.recipes.builder.CoARecipeBuilder;

public class CoALoaders {

    public static void register() {
        motors();
        pumps();
        conveyors();
        pistons();
        robotArms();
        fieldGenerators();
        emitters();
        sensors();
    }

    public static void motors() {
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            if (i < GTValues.LuV) {
                if (i < GTValues.HV) {
                    builder.input(cableGtDouble, cableMaterial(i), 48);
                } else {
                    builder.input(cableGtQuadruple, cableMaterial(i), 48);
                }
                builder.input(stickLong, partMaterial1(i), 48)
                        .input(stick, magneticMaterial(i), 48);
                if (i == GTValues.LV) {
                    builder.input(wireGtOctal, wireMaterial(i), 24);
                } else {
                    builder.input(wireGtHex, wireMaterial(i), 24);
                }
            } else {
                builder.input(cableGtDouble, cableMaterial(i), 48)
                        .input(stickLong, partMaterial1(i), 48)
                        .input(stickLong, magneticMaterial(i), 4 * 48)
                        .input(ring, partMaterial1(i), 4 * 48)
                        .input(round, partMaterial1(i), 8 * 48)
                        .input(ingot, wireMaterial(i), 12 * 48)
                        .notConsumable(MetaItems.SHAPE_EXTRUDER_WIRE);
            }
            builder.input(wireGtDouble, scMaterial(i), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .output(motor(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void pumps() {
        Material[] pipeMaterial = new Material[] {
                Materials.Steel, Materials.Bronze, Materials.StainlessSteel, Materials.Titanium,
                Materials.TungstenSteel, Materials.NiobiumTitanium, Materials.Polybenzimidazole, Materials.Naquadah };
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            builder.input(motor(i), 48);
            if (i < GTValues.LuV) {
                builder.input(cableGtSingle, cableMaterial(i), 48)
                        .input(pipeNormalFluid, pipeMaterial[i - 1], 48)
                        .input(screw, partMaterial3(i), 48)
                        .input(rotor, partMaterial3(i), 48);
            } else {
                builder.input(cableGtDouble, cableMaterial(i), 48)
                        .input(i == GTValues.LuV ? pipeSmallFluid :
                                i == GTValues.ZPM ? pipeNormalFluid : pipeLargeFluid,
                                pipeMaterial[i - 1], 48)
                        .input(plate, partMaterial1(i), 2 * 48)
                        .input(screw, partMaterial1(i), 8 * 48)
                        .input(rotor, partMaterial3(i), 48);
            }
            builder.input(ring, Materials.SiliconeRubber, 48)
                    .input(wireGtSingle, scMaterial(i), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(pump(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void conveyors() {
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            builder.input(motor(i), 2 * 48);
            if (i < GTValues.LuV) {
                builder.input(cableGtSingle, cableMaterial(i), 48);
            } else {
                builder.input(cableGtDouble, cableMaterial(i), 48)
                        .input(plate, partMaterial1(i), 2 * 48)
                        .input(ring, partMaterial1(i), 4 * 48)
                        .input(round, partMaterial1(i), 16 * 48)
                        .input(screw, partMaterial1(i), 4 * 48);
            }
            builder.input(wireGtSingle, scMaterial(i), 48)
                    .fluidInputs(Materials.StyreneButadieneRubber.getFluid(144 * 6 * 48))
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(conveyor(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void pistons() {
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            builder.input(motor(i), 48)
                    .input(cableGtDouble, cableMaterial(i), 48)
                    .input(gear, partMaterial2(i), 48);
            if (i < GTValues.LuV) {
                builder.input(plate, partMaterial2(i), 3 * 48);
            } else {
                builder.input(plate, partMaterial1(i), 4 * 48)
                        .input(ring, partMaterial1(i), 4 * 48)
                        .input(round, partMaterial1(i), 16 * 48)
                        .input(stickLong, partMaterial1(i), 2 * 48)
                        .input(gearSmall, partMaterial2(i), 2 * 48);
            }
            builder.input(wireGtSingle, scMaterial(i), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(piston(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void robotArms() {
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            builder.input(motor(i), 2 * 48)
                    .input(piston(i), 48);
            if (i < GTValues.LuV) {
                builder.input(cableGtSingle, cableMaterial(i), 3 * 48)
                        .input(stickLong, partMaterial1(i), 48)
                        .input(bestCircuit(i), 48);
            } else {
                builder.input(stickLong, partMaterial1(i), 4 * 48)
                        .input(gear, partMaterial2(i))
                        .input(gearSmall, partMaterial2(i), 3 * 48)
                        .input(bestCircuit(i), 48)
                        .input(bestCircuit(i - 1), 2 * 48)
                        .input(bestCircuit(i - 2), 4 * 48)
                        .input(cableGtQuadruple, cableMaterial(i), 48);
            }
            builder.input(wireGtSingle, scMaterial(i), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(robotArm(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void fieldGenerators() {
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            builder.input(bestCircuit(i), 2 * 48);
            if (i < GTValues.LuV) {
                builder.inputs(GTUtility.copy(48, partsStack1(i)))
                        .input(i == GTValues.IV ? plate : plateDouble, partMaterial2(i), 48)
                        .input(wireGtQuadruple, scMaterial(i), 4 * 48);
            } else {
                builder.input(frameGt, partMaterial4(i), 48)
                        .input(plate, partMaterial4(i), 6 * 48)
                        .inputs(GTUtility.copy(48, partsStack1(i)))
                        .input(emitter(i), 2 * 48)
                        .input(wireFine, scMaterial(i), 64 * 2 * 48)
                        .input(cableGtQuadruple, cableMaterial(i), 48);
            }
            builder.fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(fieldGenerator(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void emitters() {
        Material[] exMat = new Material[] {
                Materials.Brass, Materials.Electrum, Materials.Chrome, Materials.Platinum, Materials.Iridium,
                Materials.Palladium, Materials.Trinium, Materials.Naquadria };
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            if (i < GTValues.LuV) {
                builder.input(stickLong, exMat[i - 1], 2 * 48)
                        .input(cableGtDouble, cableMaterial(i), 48)
                        .input(bestCircuit(i), 2 * 48)
                        .inputs(GTUtility.copy(2, partsStack2(i)));
            } else {
                builder.input(frameGt, partMaterial4(i), 48)
                        .input(motor(i), 48)
                        .input(stickLong, i == GTValues.LuV ? Materials.Ruridit : partMaterial4(i), 4 * 48)
                        .inputs(GTUtility.copy(i == GTValues.ZPM ? 2 : 1, partsStack2(i)))
                        .input(bestCircuit(i), 2 * 48)
                        .input(foil, exMat[i - 1], (64 + 32) * 48)
                        .input(cableGtQuadruple, cableMaterial(i), 48);
            }
            builder.input(wireGtSingle, scMaterial(i), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(emitter(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }

    public static void sensors() {
        Material[] exMat = new Material[] {
                Materials.Brass, Materials.Electrum, Materials.Chrome, Materials.Platinum, Materials.Iridium,
                Materials.Palladium, Materials.Trinium, Materials.Naquadria };
        for (int i = 1; i < GTValues.UHV; i++) {
            CoARecipeBuilder builder = COA_RECIPES.recipeBuilder();
            if (i < GTValues.LuV) {
                builder.input(stick, exMat[i - 1], 48)
                        .input(plate, partMaterial2(i), 4 * 48)
                        .input(bestCircuit(i), 48)
                        .inputs(GTUtility.copy(48, partsStack2(i)));
            } else {
                builder.input(frameGt, partMaterial4(i), 48)
                        .input(motor(i), 48)
                        .input(plate, i == GTValues.LuV ? Materials.Ruridit : partMaterial4(i), 4 * 48)
                        .inputs(GTUtility.copy(i == GTValues.ZPM ? 2 : 1, partsStack2(i)))
                        .input(bestCircuit(i), 2 * 48)
                        .input(foil, exMat[i - 1], (64 + 32) * 48)
                        .input(cableGtQuadruple, cableMaterial(i), 48);
            }
            builder.input(wireGtSingle, scMaterial(i), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .fluidInputs(Materials.Lubricant.getFluid(36 * i))
                    .output(sensor(i), 64)
                    .casingTier(i).EUt(GTValues.VA[i + 2]).duration(20 * 60 * 2)
                    .buildAndRegister();
        }
    }
}
