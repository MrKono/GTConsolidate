package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps.COA_RECIPES;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaItems;

import kono.ceu.gtconsolidate.api.recipes.builder.CoARecipeBuilder;

public class CoALoaders {

    public static void register() {
        // motors
        registerMotors(Materials.Tin, false, Materials.Iron, Materials.IronMagnetic, Materials.Copper, false, LV);
        registerMotors(Materials.Tin, false, Materials.Steel, Materials.SteelMagnetic, Materials.Copper, false, LV);
        registerMotors(Materials.Copper, false, Materials.Aluminium, Materials.SteelMagnetic, Materials.Cupronickel, true, MV);
        registerMotors(Materials.Silver, true, Materials.StainlessSteel, Materials.IronMagnetic, Materials.Electrum, true, HV);
        registerMotors(Materials.Aluminium, true, Materials.Titanium, Materials.NeodymiumMagnetic, Materials.Kanthal, true, EV);
        registerMotors(Materials.Tungsten, true, Materials.TungstenSteel, Materials.NeodymiumMagnetic, Materials.Graphene, true, IV);
        registerMotorsAL(Materials.SamariumMagnetic, 1, Materials.HSSS, 2, Materials.HSSS, 2, Materials.HSSS, 4, Materials.Ruridit, 64, Materials.NiobiumTitanium, LuV);
        registerMotorsAL(Materials.SamariumMagnetic, 1, Materials.Osmiridium, 4, Materials.Osmiridium, 4, Materials.Osmiridium, 8, Materials.Europium, 64 + 32, Materials.VanadiumGallium, ZPM);
        registerMotorsAL(Materials.SamariumMagnetic, 1, Materials.Tritanium, 4, Materials.Tritanium, 4, Materials.Tritanium, 8, Materials.Americium, 64 + 32, Materials.YttriumBariumCuprate, UV);
        //pumps();
        // conveyors
        registerConveyor(Materials.Tin, new Material[] {Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber}, LV);
        registerConveyor(Materials.Tin, new Material[] {Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber}, MV);
        registerConveyor(Materials.Tin, new Material[] {Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber}, HV);
        registerConveyor(Materials.Tin, new Material[] {Materials.Rubber, Materials.SiliconeRubber, Materials.StyreneButadieneRubber}, EV);
        registerConveyor(Materials.Tin, new Material[] {Materials.SiliconeRubber, Materials.StyreneButadieneRubber}, IV);
        registerConveyorAL(Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.HSSS, Materials.NiobiumTitanium, LuV);
        registerConveyorAL(Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium, Materials.Osmiridium, Materials.VanadiumGallium, ZPM);
        registerConveyorAL(Materials.Tritanium, Materials.Tritanium, Materials.Tritanium, Materials.Tritanium, Materials.NiobiumTitanium, UV);
        //pistons();
        //robotArms();
        //fieldGenerators();
        //emitters();
        //sensors();
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
    public static void registerMotors(Material cableMaterial, boolean isCableDouble, Material stickMaterial, Material magneticMaterial, Material wireMaterial, boolean isWireDouble, int tier) {
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

    public static void registerMotorsAL(Material magneticMaterial, int amountMagnetic, Material stickMaterial, int amountStick, Material ringMaterial, int amountRing, Material roundMaterial, int amountRound, Material fineMaterial, int amountFine, Material cableMaterial, int tier) {
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

    public static void registerConveyorAL(Material plateMaterial, Material ringMaterial, Material roundMaterial, Material screwMaterial, Material cableMaterial, int tier) {
        CoARecipeBuilder builder = COA_RECIPES.recipeBuilder()
                .input(pump(tier), 2)
                .input(plate, plateMaterial, 2* 48)
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


}
