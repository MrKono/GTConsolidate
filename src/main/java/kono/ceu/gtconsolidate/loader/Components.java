package kono.ceu.gtconsolidate.loader;

import static gregtech.api.unification.ore.OrePrefix.*;

import net.minecraft.item.ItemStack;

import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;

public class Components {

    // Materials
    public static Material cableMaterial(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.RedAlloy;
            case 1 -> Materials.Tin;
            case 2 -> Materials.Copper;
            case 3 -> Materials.Gold;
            case 4 -> Materials.Aluminium;
            case 5 -> Materials.Tungsten;
            case 6 -> Materials.NiobiumTitanium;
            case 7 -> Materials.VanadiumGallium;
            case 8 -> Materials.YttriumBariumCuprate;
            default -> Materials.Neutronium;
        };
    }

    public static Material wireMaterial(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.Lead;
            case 1 -> Materials.Copper;
            case 2 -> Materials.Cupronickel;
            case 3 -> Materials.Electrum;
            case 4 -> Materials.Nichrome;
            case 5 -> Materials.Graphene;
            case 6 -> Materials.Ruridit;
            case 7 -> Materials.Europium;
            case 8 -> Materials.Americium;
            default -> Materials.Neutronium;
        };
    }

    // stick, round, ring etc...
    public static Material partMaterial1(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.Iron;
            case 1 -> Materials.Steel;
            case 2 -> Materials.Aluminium;
            case 3 -> Materials.StainlessSteel;
            case 4 -> Materials.Titanium;
            case 5 -> Materials.TungstenSteel;
            case 6 -> Materials.HSSS;
            case 7 -> Materials.Osmiridium;
            case 8 -> Materials.Tritanium;
            default -> Materials.Neutronium;
        };
    }

    public static Material partMaterial2(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.WroughtIron;
            case 1 -> Materials.Steel;
            case 2 -> Materials.Aluminium;
            case 3 -> Materials.StainlessSteel;
            case 4 -> Materials.Titanium;
            case 5 -> Materials.TungstenSteel;
            case 6 -> Materials.HSSS;
            case 7 -> Materials.Osmiridium;
            case 8 -> Materials.NaquadahAlloy;
            default -> Materials.Neutronium;
        };
    }

    public static Material partMaterial3(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.WroughtIron;
            case 1 -> Materials.Tin;
            case 2 -> Materials.Bronze;
            case 3 -> Materials.Steel;
            case 4 -> Materials.StainlessSteel;
            case 5 -> Materials.TungstenSteel;
            case 6 -> Materials.HSSS;
            case 7 -> Materials.Osmiridium;
            case 8 -> Materials.NaquadahAlloy;
            default -> Materials.Neutronium;
        };
    }

    public static Material partMaterial4(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.WroughtIron;
            case 1 -> Materials.Steel;
            case 2 -> Materials.Aluminium;
            case 3 -> Materials.StainlessSteel;
            case 4 -> Materials.Titanium;
            case 5 -> Materials.TungstenSteel;
            case 6 -> Materials.HSSS;
            case 7 -> Materials.NaquadahAlloy;
            case 8 -> Materials.Tritanium;
            default -> Materials.Neutronium;
        };
    }

    public static Material magneticMaterial(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.IronMagnetic;
            case 1, 3, 2 -> Materials.SteelMagnetic;
            case 4, 5 -> Materials.NeodymiumMagnetic;
            default -> Materials.SamariumMagnetic;
        };
    }

    public static Material scMaterial(int voltage) {
        return switch (voltage) {
            case 0 -> Materials.RedAlloy;
            case 1 -> Materials.ManganesePhosphide;
            case 2 -> Materials.MagnesiumDiboride;
            case 3 -> Materials.MercuryBariumCalciumCuprate;
            case 4 -> Materials.UraniumTriplatinum;
            case 5 -> Materials.SamariumIronArsenicOxide;
            case 6 -> Materials.IndiumTinBariumTitaniumCuprate;
            case 7 -> Materials.UraniumRhodiumDinaquadide;
            case 8 -> Materials.EnrichedNaquadahTriniumEuropiumDuranide;
            case 9 -> Materials.RutheniumTriniumAmericiumNeutronate;
            default -> Materials.Neutronium;
        };
    }

    public static MetaItem<?>.MetaValueItem motor(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ELECTRIC_MOTOR_LV;
            case 2 -> MetaItems.ELECTRIC_MOTOR_MV;
            case 3 -> MetaItems.ELECTRIC_MOTOR_HV;
            case 4 -> MetaItems.ELECTRIC_MOTOR_EV;
            case 5 -> MetaItems.ELECTRIC_MOTOR_IV;
            case 6 -> MetaItems.ELECTRIC_MOTOR_LuV;
            case 7 -> MetaItems.ELECTRIC_MOTOR_ZPM;
            case 8 -> MetaItems.ELECTRIC_MOTOR_UV;
            case 9 -> MetaItems.ELECTRIC_MOTOR_UHV;
            case 10 -> MetaItems.ELECTRIC_MOTOR_UEV;
            case 11 -> MetaItems.ELECTRIC_MOTOR_UIV;
            case 12 -> MetaItems.ELECTRIC_MOTOR_UXV;
            case 13, 14 -> MetaItems.ELECTRIC_MOTOR_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem pump(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ELECTRIC_PUMP_LV;
            case 2 -> MetaItems.ELECTRIC_PUMP_MV;
            case 3 -> MetaItems.ELECTRIC_PUMP_HV;
            case 4 -> MetaItems.ELECTRIC_PUMP_EV;
            case 5 -> MetaItems.ELECTRIC_PUMP_IV;
            case 6 -> MetaItems.ELECTRIC_PUMP_LuV;
            case 7 -> MetaItems.ELECTRIC_PUMP_ZPM;
            case 8 -> MetaItems.ELECTRIC_PUMP_UV;
            case 9 -> MetaItems.ELECTRIC_PUMP_UHV;
            case 10 -> MetaItems.ELECTRIC_PUMP_UEV;
            case 11 -> MetaItems.ELECTRIC_PUMP_UIV;
            case 12 -> MetaItems.ELECTRIC_PUMP_UXV;
            case 13, 14 -> MetaItems.ELECTRIC_PUMP_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem regulator(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.FLUID_REGULATOR_LV;
            case 2 -> MetaItems.FLUID_REGULATOR_MV;
            case 3 -> MetaItems.FLUID_REGULATOR_HV;
            case 4 -> MetaItems.FLUID_REGULATOR_EV;
            case 5 -> MetaItems.FLUID_REGULATOR_IV;
            case 6 -> MetaItems.FLUID_REGULATOR_LUV;
            case 7 -> MetaItems.FLUID_REGULATOR_ZPM;
            case 8 -> MetaItems.FLUID_REGULATOR_UV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem piston(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ELECTRIC_PISTON_LV;
            case 2 -> MetaItems.ELECTRIC_PISTON_MV;
            case 3 -> MetaItems.ELECTRIC_PISTON_HV;
            case 4 -> MetaItems.ELECTRIC_PISTON_EV;
            case 5 -> MetaItems.ELECTRIC_PISTON_IV;
            case 6 -> MetaItems.ELECTRIC_PISTON_LUV;
            case 7 -> MetaItems.ELECTRIC_PISTON_ZPM;
            case 8 -> MetaItems.ELECTRIC_PISTON_UV;
            case 9 -> MetaItems.ELECTRIC_PISTON_UHV;
            case 10 -> MetaItems.ELECTRIC_PISTON_UEV;
            case 11 -> MetaItems.ELECTRIC_PISTON_UIV;
            case 12 -> MetaItems.ELECTRIC_PISTON_UXV;
            case 13, 14 -> MetaItems.ELECTRIC_PISTON_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem robotArm(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ROBOT_ARM_LV;
            case 2 -> MetaItems.ROBOT_ARM_MV;
            case 3 -> MetaItems.ROBOT_ARM_HV;
            case 4 -> MetaItems.ROBOT_ARM_EV;
            case 5 -> MetaItems.ROBOT_ARM_IV;
            case 6 -> MetaItems.ROBOT_ARM_LuV;
            case 7 -> MetaItems.ROBOT_ARM_ZPM;
            case 8 -> MetaItems.ROBOT_ARM_UV;
            case 9 -> MetaItems.ROBOT_ARM_UHV;
            case 10 -> MetaItems.ROBOT_ARM_UEV;
            case 11 -> MetaItems.ROBOT_ARM_UIV;
            case 12 -> MetaItems.ROBOT_ARM_UXV;
            case 13, 14 -> MetaItems.ROBOT_ARM_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem fieldGenerator(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.FIELD_GENERATOR_LV;
            case 2 -> MetaItems.FIELD_GENERATOR_MV;
            case 3 -> MetaItems.FIELD_GENERATOR_HV;
            case 4 -> MetaItems.FIELD_GENERATOR_EV;
            case 5 -> MetaItems.FIELD_GENERATOR_IV;
            case 6 -> MetaItems.FIELD_GENERATOR_LuV;
            case 7 -> MetaItems.FIELD_GENERATOR_ZPM;
            case 8 -> MetaItems.FIELD_GENERATOR_UV;
            case 9 -> MetaItems.FIELD_GENERATOR_UHV;
            case 10 -> MetaItems.FIELD_GENERATOR_UEV;
            case 11 -> MetaItems.FIELD_GENERATOR_UIV;
            case 12 -> MetaItems.FIELD_GENERATOR_UXV;
            case 13, 14 -> MetaItems.FIELD_GENERATOR_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem conveyor(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.CONVEYOR_MODULE_LV;
            case 2 -> MetaItems.CONVEYOR_MODULE_MV;
            case 3 -> MetaItems.CONVEYOR_MODULE_HV;
            case 4 -> MetaItems.CONVEYOR_MODULE_EV;
            case 5 -> MetaItems.CONVEYOR_MODULE_IV;
            case 6 -> MetaItems.CONVEYOR_MODULE_LuV;
            case 7 -> MetaItems.CONVEYOR_MODULE_ZPM;
            case 8 -> MetaItems.CONVEYOR_MODULE_UV;
            case 9 -> MetaItems.CONVEYOR_MODULE_UHV;
            case 10 -> MetaItems.CONVEYOR_MODULE_UEV;
            case 11 -> MetaItems.CONVEYOR_MODULE_UIV;
            case 12 -> MetaItems.CONVEYOR_MODULE_UXV;
            case 13, 14 -> MetaItems.CONVEYOR_MODULE_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem emitter(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.EMITTER_LV;
            case 2 -> MetaItems.EMITTER_MV;
            case 3 -> MetaItems.EMITTER_HV;
            case 4 -> MetaItems.EMITTER_EV;
            case 5 -> MetaItems.EMITTER_IV;
            case 6 -> MetaItems.EMITTER_LuV;
            case 7 -> MetaItems.EMITTER_ZPM;
            case 8 -> MetaItems.EMITTER_UV;
            case 9 -> MetaItems.EMITTER_UHV;
            case 10 -> MetaItems.EMITTER_UEV;
            case 11 -> MetaItems.EMITTER_UIV;
            case 12 -> MetaItems.EMITTER_UXV;
            case 13, 14 -> MetaItems.EMITTER_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem sensor(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.SENSOR_LV;
            case 2 -> MetaItems.SENSOR_MV;
            case 3 -> MetaItems.SENSOR_HV;
            case 4 -> MetaItems.SENSOR_EV;
            case 5 -> MetaItems.SENSOR_IV;
            case 6 -> MetaItems.SENSOR_LuV;
            case 7 -> MetaItems.SENSOR_ZPM;
            case 8 -> MetaItems.SENSOR_UV;
            case 9 -> MetaItems.SENSOR_UHV;
            case 10 -> MetaItems.SENSOR_UEV;
            case 11 -> MetaItems.SENSOR_UIV;
            case 12 -> MetaItems.SENSOR_UXV;
            case 13, 14 -> MetaItems.SENSOR_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem bestCircuit(int voltage) {
        return switch (voltage) {
            case 0 -> MetaItems.NAND_CHIP_ULV;
            case 1 -> MetaItems.MICROPROCESSOR_LV;
            case 2 -> MetaItems.PROCESSOR_MV;
            case 3 -> MetaItems.NANO_PROCESSOR_HV;
            case 4 -> MetaItems.QUANTUM_PROCESSOR_EV;
            case 5 -> MetaItems.CRYSTAL_PROCESSOR_IV;
            case 6 -> MetaItems.WETWARE_PROCESSOR_LUV;
            case 7 -> MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM;
            case 8 -> MetaItems.WETWARE_SUPER_COMPUTER_UV;
            default -> MetaItems.WETWARE_MAINFRAME_UHV;
        };
    }

    public static ItemStack partsStack1(int voltage) {
        return switch (voltage) {
            case 1 -> OreDictUnifier.get(gem, Materials.EnderPearl);
            case 2 -> OreDictUnifier.get(gem, Materials.EnderEye);
            case 3 -> MetaItems.QUANTUM_EYE.getStackForm();
            case 4 -> OreDictUnifier.get(gem, Materials.NetherStar);
            case 5, 7, 6 -> MetaItems.QUANTUM_STAR.getStackForm();
            case 8 -> MetaItems.GRAVI_STAR.getStackForm();
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static ItemStack partsStack2(int voltage) {
        return switch (voltage) {
            case 1 -> OreDictUnifier.get(gem, Materials.Quartzite);
            case 2 -> OreDictUnifier.get(gemFlawless, Materials.Emerald);
            case 3 -> OreDictUnifier.get(gem, Materials.EnderEye);
            case 4 -> MetaItems.QUANTUM_EYE.getStackForm();
            case 5, 7, 6 -> MetaItems.QUANTUM_STAR.getStackForm();
            case 8 -> MetaItems.GRAVI_STAR.getStackForm();
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }
}
