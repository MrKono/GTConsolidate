package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.loaders.recipe.CraftingComponent.CIRCUIT;
import static kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps.COA_RECIPES;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.GTValues;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

import kono.ceu.gtconsolidate.api.recipes.builder.CoARecipeBuilder;

public class CoALoaders {

    public static void register() {
        // motors
        registerMotor(Materials.Tin, false, Materials.Iron, Materials.IronMagnetic, Materials.Copper, false, LV);
        registerMotor(Materials.Tin, false, Materials.Steel, Materials.SteelMagnetic, Materials.Copper, false, LV);
        registerMotor(Materials.Copper, false, Materials.Aluminium, Materials.SteelMagnetic, Materials.Cupronickel,
                true, MV);
        registerMotor(Materials.Silver, true, Materials.StainlessSteel, Materials.IronMagnetic, Materials.Electrum,
                true, HV);
        registerMotor(Materials.Aluminium, true, Materials.Titanium, Materials.NeodymiumMagnetic, Materials.Kanthal,
                true, EV);
        registerMotor(Materials.Tungsten, true, Materials.TungstenSteel, Materials.NeodymiumMagnetic,
                Materials.Graphene, true, IV);
        registerMotorAL(Materials.SamariumMagnetic, 1, Materials.HSSS, 2, Materials.HSSS, 2, Materials.HSSS, 4,
                Materials.Ruridit, 64, Materials.NiobiumTitanium, LuV);
        registerMotorAL(Materials.SamariumMagnetic, 1, Materials.Osmiridium, 4, Materials.Osmiridium, 4,
                Materials.Osmiridium, 8, Materials.Europium, 64 + 32, Materials.VanadiumGallium, ZPM);
        registerMotorAL(Materials.SamariumMagnetic, 1, Materials.Tritanium, 4, Materials.Tritanium, 4,
                Materials.Tritanium, 8, Materials.Americium, 64 + 32, Materials.YttriumBariumCuprate, UV);
        // pumps
        registerPump(Materials.Tin, Materials.Bronze, Materials.Tin, Materials.Tin,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, LV);
        registerPump(Materials.Copper, Materials.Steel, Materials.Bronze, Materials.Bronze,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, MV);
        registerPump(Materials.Gold, Materials.StainlessSteel, Materials.Steel, Materials.Steel,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, HV);
        registerPump(Materials.Aluminium, Materials.Titanium, Materials.StainlessSteel, Materials.StainlessSteel,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, EV);
        registerPump(Materials.Tungsten, Materials.TungstenSteel, Materials.TungstenSteel, Materials.TungstenSteel,
                new Material[] { Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, IV);
        registerPumpAL(pipeSmallFluid, Materials.NiobiumTitanium, Materials.HSSS, Materials.HSSS, Materials.HSSS,
                Materials.NiobiumTitanium, 4, LuV);
        registerPumpAL(pipeNormalFluid, Materials.Polybenzimidazole, Materials.Osmiridium, Materials.Osmiridium,
                Materials.Osmiridium, Materials.VanadiumGallium, 8, ZPM);
        registerPumpAL(pipeLargeFluid, Materials.Naquadah, Materials.Tritanium, Materials.Tritanium,
                Materials.Tritanium, Materials.YttriumBariumCuprate, 4, UV);
        // conveyors
        registerConveyor(Materials.Tin,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, LV);
        registerConveyor(Materials.Tin,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, MV);
        registerConveyor(Materials.Tin,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, HV);
        registerConveyor(Materials.Tin,
                new Material[] { Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber }, EV);
        registerConveyor(Materials.Tin, new Material[] { Materials.SiliconeRubber, Materials.StyreneButadieneRubber },
                IV);
        registerConveyorAL(Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.NiobiumTitanium,
                LuV);
        registerConveyorAL(Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium,
                Materials.VanadiumGallium, ZPM);
        registerConveyorAL(Materials.Tritanium, Materials.Tritanium, Materials.Tritanium, Materials.Tritanium,
                Materials.NiobiumTitanium, UV);
        // pistons;
        registerPiston(Materials.Steel, Materials.Tin, Materials.Steel, Materials.Steel, LV);
        registerPiston(Materials.Aluminium, Materials.Copper, Materials.Aluminium, Materials.Aluminium, MV);
        registerPiston(Materials.StainlessSteel, Materials.Gold, Materials.StainlessSteel, Materials.StainlessSteel,
                HV);
        registerPiston(Materials.Titanium, Materials.Aluminium, Materials.Tritanium, Materials.Tritanium, EV);
        registerPiston(Materials.TungstenSteel, Materials.Tungsten, Materials.TungstenSteel, Materials.TungstenSteel,
                IV);
        registerPistonAL(Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.HSSS,
                Materials.NiobiumTitanium, LuV);
        registerPistonAL(Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium,
                Materials.Osmiridium, Materials.Osmiridium, Materials.VanadiumGallium, ZPM);
        registerPistonAL(Materials.Tritanium, Materials.Tritanium, Materials.Tritanium, Materials.Tritanium,
                Materials.NaquadahAlloy, Materials.NaquadahAlloy, Materials.YttriumBariumCuprate, UV);
        // robotArms
        registerRobotArm(Materials.Tin, Materials.Steel, LV);
        registerRobotArm(Materials.Copper, Materials.Aluminium, MV);
        registerRobotArm(Materials.Gold, Materials.StainlessSteel, HV);
        registerRobotArm(Materials.Aluminium, Materials.Titanium, EV);
        registerRobotArm(Materials.Tungsten, Materials.TungstenSteel, IV);
        registerRobotArmAL(Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.NiobiumTitanium, LuV);
        registerRobotArmAL(Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium, Materials.VanadiumGallium, ZPM);
        registerRobotArmAL(Materials.Tritanium, Materials.Tritanium, Materials.Tritanium, Materials.YttriumBariumCuprate, UV);
        // fieldGenerators();
        // emitters();
        // sensors();
    }

    /**
     * The recipe is based on GTNH's recipe.
     * Duplicated recipes for multiple inputs (i.e., SBR vs Silicone) should be respected.
     * Multiply all inputs by 48x, but output 64 at a time.
     *
     * Item conversion rules (in case of odd numbers, round down):
     * All wires/cables should convert to 16x sizes (excluding fine wires).
     * All rods should convert to long rods. However, the quantity is x24, not x48.
     * Superconducting wires were added on a whim by the author :)
     */
    public static void registerMotor(Material cableMaterial, boolean isCableDouble, Material stickMaterial,
                                     Material magneticMaterial, Material wireMaterial, boolean isWireDouble,
                                     int tier) {
        int cableAmount = isCableDouble ? 12 : 6;
        int wireAmount = isWireDouble ? 24 : 12;

        COA_RECIPES.recipeBuilder()
                .input(cableGtHex, cableMaterial, cableAmount)
                .input(stickLong, stickMaterial, 48)
                .input(stickLong, magneticMaterial, 24)
                .input(wireGtHex, wireMaterial, wireAmount)
                .input(wireGtSingle, scMaterial(tier), isWireDouble ? 48 * 2 : 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(motor(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800)
                .buildAndRegister();
    }

    public static void registerMotorAL(Material magneticMaterial, int amountMagnetic, Material stickMaterial,
                                       int amountStick, Material ringMaterial, int amountRing, Material roundMaterial,
                                       int amountRound, Material fineMaterial, int amountFine, Material cableMaterial,
                                       int tier) {
        CoARecipeBuilder builder = COA_RECIPES.recipeBuilder()
                .input(stickLong, magneticMaterial, amountMagnetic * 48)
                .input(stickLong, stickMaterial, amountStick * 48)
                .input(ring, ringMaterial, amountRing * 48)
                .input(round, roundMaterial, amountRound * 48)
                .input(wireFine, fineMaterial, amountFine * 48)
                .input(cableGtHex, cableMaterial, 6)
                .input(wireGtDouble, scMaterial(tier), 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(motor(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800);

        if (tier == UV) builder.fluidInputs(Materials.Naquadria.getFluid(L * 48 * 4));

        builder.buildAndRegister();
    }

    public static void registerConveyor(Material cableMaterial, Material[] rubberMaterials, int tier) {
        for (Material rubber : rubberMaterials) {
            COA_RECIPES.recipeBuilder()
                    .input(cableGtHex, cableMaterial, 18)
                    .input(motor(tier), 48 * 2)
                    .input(wireGtSingle, scMaterial(tier), 48)
                    .fluidInputs(rubber.getFluid(L * 6 * 48))
                    .fluidInputs(Materials.SolderingAlloy.getFluid(L * tier))
                    .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                    .output(conveyor(tier), 64)
                    .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800)
                    .buildAndRegister();
        }
    }

    public static void registerConveyorAL(Material plateMaterial, Material ringMaterial, Material roundMaterial,
                                          Material screwMaterial, Material cableMaterial, int tier) {
        CoARecipeBuilder builder = COA_RECIPES.recipeBuilder()
                .input(pump(tier), 2)
                .input(plate, plateMaterial, 2 * 48)
                .input(ring, ringMaterial, 4 * 48)
                .input(round, roundMaterial, 16 * 48)
                .input(screw, screwMaterial, 4 * 48)
                .input(cableGtHex, cableMaterial, 6)
                .input(wireGtDouble, scMaterial(tier), 48)
                .fluidInputs(Materials.StyreneButadieneRubber.getFluid(L * 6 * 48))
                .fluidInputs(Materials.SolderingAlloy.getFluid(L * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(conveyor(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800);

        if (tier == UV) builder.fluidInputs(Materials.Naquadria.getFluid(L * 6 * 48));

        builder.buildAndRegister();
    }

    public static void registerPump(Material cableMaterial, Material pipeMaterial, Material screwMaterial,
                                    Material rotorMaterial, Material[] rubberMaterials, int tier) {
        for (Material rubber : rubberMaterials) {
            COA_RECIPES.recipeBuilder()
                    .input(motor(tier), 48)
                    .input(cableGtHex, cableMaterial, 3)
                    .input(pipeNormalFluid, pipeMaterial, 48)
                    .input(screw, screwMaterial, 48)
                    .input(rotor, rotorMaterial, 48)
                    .input(ring, rubber, 2 * 48)
                    .input(wireGtSingle, scMaterial(tier), 48)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                    .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                    .output(pump(tier), 64)
                    .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800)
                    .buildAndRegister();
        }
    }

    public static void registerPumpAL(OrePrefix pipePreFix, Material pipeMaterial, Material plateMaterial,
                                      Material screwMaterial, Material rotorMaterial, Material cableMaterial,
                                      int ringAmount, int tier) {
        CoARecipeBuilder builder = COA_RECIPES.recipeBuilder()
                .input(motor(tier), 48)
                .input(pipePreFix, pipeMaterial, 48)
                .input(plate, plateMaterial, 2 * 48)
                .input(screw, screwMaterial, 8 * 48)
                .input(ring, Materials.SiliconeRubber, ringAmount * 48)
                .input(rotor, rotorMaterial, 48)
                .input(cableGtHex, cableMaterial, 6).input(wireGtSingle, scMaterial(tier), 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(pump(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800);

        if (tier == UV) builder.fluidInputs(Materials.Naquadria.getFluid(L * 4 * 48));

        builder.buildAndRegister();
    }

    public static void registerPiston(Material stickMaterial, Material cableMaterial, Material plateMaterial,
                                      Material gearMaterial, int tier) {
        COA_RECIPES.recipeBuilder()
                .input(stickLong, stickMaterial, 48)
                .input(cableGtHex, cableMaterial, 6)
                .input(plate, plateMaterial, 3 * 48)
                .input(gearSmall, gearMaterial, 48)
                .input(motor(tier), 48)
                .input(wireGtSingle, scMaterial(tier), 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(piston(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800)
                .buildAndRegister();
    }

    public static void registerPistonAL(Material plateMaterial, Material ringMaterial, Material roundMaterial,
                                        Material stickMaterial, Material gearMaterial, Material smallGearMaterial,
                                        Material cableMaterial, int tier) {
        CoARecipeBuilder builder = COA_RECIPES.recipeBuilder()
                .input(motor(tier), 48)
                .input(plate, plateMaterial, 4 * 48)
                .input(ring, ringMaterial, 4 * 48)
                .input(round, roundMaterial, 16 * 48)
                .input(stickLong, stickMaterial, 2 * 48)
                .input(gear, gearMaterial, 48)
                .input(gearSmall, smallGearMaterial, 2 * 48)
                .input(cableGtHex, cableMaterial, 6)
                .input(wireGtDouble, scMaterial(tier), 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(piston(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800);

        if (tier == UV) builder.fluidInputs(Materials.Naquadria.getFluid(L * 4 * 48));

        builder.buildAndRegister();
    }

    public static void registerRobotArm(Material cableMaterial, Material stickMaterial, int tier) {
        COA_RECIPES.recipeBuilder()
                .input(cableGtHex, cableMaterial, 9)
                .input(stickLong, stickMaterial, 48)
                .input(motor(tier), 48 * 2)
                .input(piston(tier), 48)
                .input(circuit, markerMaterial(tier), 48)
                .input(wireGtSingle, scMaterial(tier), 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(robotArm(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800)
                .buildAndRegister();
    }

    public static void registerRobotArmAL(Material stickMaterial, Material gearMaterial, Material smallGearMaterial, Material cableMaterial, int tier) {
        CoARecipeBuilder builder = COA_RECIPES.recipeBuilder()
                .input(stickLong, stickMaterial, 48 * 4)
                .input(gear, gearMaterial, 48)
                .input(gearSmall, smallGearMaterial, 48 * 3)
                .input(motor(tier), 48 * 2)
                .input(piston(tier), 48)
                .input(circuit, markerMaterial(tier), 48)
                .input(circuit, markerMaterial(tier - 1), 48 * 2)
                .input(circuit, markerMaterial(tier - 2), 48 * 4)
                .input(cableGtHex, cableMaterial, 12)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(1000 * tier))
                .output(robotArm(tier), 64)
                .casingTier(tier).EUt(GTValues.VA[tier + 2]).duration(4800);

        if (tier == UV) builder.fluidInputs(Materials.Naquadria.getFluid( L * 4 * 48));

        builder.buildAndRegister();
    }
}
