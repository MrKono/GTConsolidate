package kono.ceu.gtconsolidate.api.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Material;

public class GTConsolidateUtil {

    public static boolean isTABDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_TAB);
    }

    public static boolean isAltDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_RMENU) || Keyboard.isKeyDown(Keyboard.KEY_LMENU);
    }

    public static byte getOCTierByVoltage(long voltage) {
        if (voltage <= GTValues.V[GTValues.ULV]) {
            return GTValues.ULV;
        }
        return (byte) ((62 - Long.numberOfLeadingZeros(voltage - 1)) >> 1);
    }

    public static Map<Integer, Material> parseToMap(String[] entries) {
        Logs.logger.info("Registering Intake Hatch dimension-material mappings...");
        Map<Integer, Material> result = new LinkedHashMap<>();

        for (String entry : entries) {
            if (entry == null || entry.isEmpty()) {
                Logs.logger.fatal(
                        "Invalid format in intakeHatchDimensionMaterials entry: '{}'. Expected format: DimID@modId:materialName. Skipping.",
                        entry);
            }

            String[] parts = entry.split("@", -1);
            if (parts.length != 2) {
                Logs.logger.fatal(
                        "Invalid format in intakeHatchDimensionMaterials entry: '{}'. Expected format: DimID@modId:materialName. Skipping.",
                        entry);
            }

            int id = 0;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                Logs.logger.warn("Invalid Dimension ID '{}' in intakeHatchDimensionMaterials. Skipping entry.",
                        parts[0]);
            }

            if (result.containsKey(id)) {
                Logs.logger.warn("Duplicate id: " + id);
            }

            String name = parts[1];
            if (name.isEmpty()) {
                Logs.logger.warn("Missing MaterialName: " + entry);
            }

            Material material = GregTechAPI.materialManager.getMaterial(name);
            if (material == null) {
                Logs.logger.warn("Cannot find '{}'. Skipping entry", name);

            }
            if (material.getFluid() == null || material.getFluid(FluidStorageKeys.GAS) == null) {
                Logs.logger.warn("'{}' does not have Fluid or Gas. Skipping entry", material.getRegistryName());
            }

            Logs.logger.info("DimID '{}' has been assigned to '{}'.", id, material.getRegistryName());
            result.put(id, material);
        }

        Logs.logger.info("Registered Intake Hatch dimension-material mappings...");
        return result;
    }
}
