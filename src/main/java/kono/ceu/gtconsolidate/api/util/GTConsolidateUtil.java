package kono.ceu.gtconsolidate.api.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;

import org.lwjgl.input.Keyboard;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.unification.material.Material;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;

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
                Logs.logger.warn(
                        "Invalid format in intakeHatchDimensionMaterials entry: '{}'. Expected format: DimID@modId:materialName. Skipping.",
                        entry);
                continue;
            }

            String[] parts = entry.split("@", -1);
            if (parts.length != 2) {
                Logs.logger.warn(
                        "Invalid format in intakeHatchDimensionMaterials entry: '{}'. Expected format: DimID@modId:materialName. Skipping.",
                        entry);
                continue;
            }

            int id;
            try {
                id = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                Logs.logger.warn("Invalid Dimension ID '{}' in intakeHatchDimensionMaterials. Skipping entry.",
                        parts[0]);
                continue;
            }

            if (result.containsKey(id)) {
                Logs.logger.warn("Duplicate id: {}", id);
                continue;
            }

            String name = parts[1];
            if (name.isEmpty()) {
                Logs.logger.warn("Missing MaterialName: {}", entry);
                continue;
            }

            Material material = GregTechAPI.materialManager.getMaterial(name);
            if (material == null) {
                Logs.logger.warn("Cannot find '{}'. Skipping entry", name);
                continue;

            }
            if (material.getFluid(FluidStorageKeys.GAS) == null) {
                Logs.logger.warn("'{}' does not have Gas. Skipping entry", material.getRegistryName());
                continue;
            }

            Logs.logger.info("DimID '{}' has been assigned to '{}'.", id, material.getRegistryName());
            result.put(id, material);
        }

        Logs.logger.info("Registered Intake Hatch dimension-material mappings...");
        return result;
    }

    public static MultiblockDisplayText.Builder addExtendedParallelLine(MultiblockDisplayText.Builder builder,
                                                                        AbstractRecipeLogic logic) {
        int maxParallel = logic.getParallelLimit();
        if (!GTConsolidateConfig.feature.modifyParallelLine) {
            return builder.addParallelsLine(maxParallel);
        } else {
            return ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(logic);
        }
    }

    public static MultiblockDisplayText.Builder addExtendedProgressLine(MultiblockDisplayText.Builder builder,
                                                                        AbstractRecipeLogic logic) {
        if (!GTConsolidateConfig.feature.modifyProgressLine) {
            int currentProgress = (int) (logic.getProgressPercent() * (double) 100.0F);
            return builder.addProgressLine(currentProgress);
        } else {
            return ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(logic);
        }
    }

    public static int getFirstUnemptyItemSlot(IItemHandler handler, int startSlot) {
        for (int i = startSlot; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty())
                return i;
        }
        for (int i = 0; i < startSlot; i++) {
            if (!handler.getStackInSlot(i).isEmpty())
                return i;
        }
        return -1;
    }

    public static List<ItemStack> parseToPlantableSapling(String[] entries) {
        List<ItemStack> stacks = new ArrayList<>();
        Logs.logger.info("Plantable sapling registration started...");
        if (entries.length == 0) {
            return stacks;
        }

        for (String entry : entries) {
            if (entry == null || entry.isEmpty()) {
                continue;
            }
            String[] parts = entry.split("@", -1);
            int meta;
            if (parts.length == 2) {
                try {
                    meta = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    Logs.logger.warn("Invalid meta '{}' in '{}'. Skipping entry.", parts[1], entry);
                    continue;
                }
            } else {
                meta = 32767;
            }
            ItemStack sapling = GameRegistry.makeItemStack(parts[0], meta, 1, null);
            if (sapling.isEmpty()) {
                Logs.logger.warn("Unable to find item with name `{}`, skipping entry, `{}`", parts[0], entry);
                continue;
            }
            stacks.add(sapling);
        }
        Logs.logger.info("Plantable sapling registration finished.");
        return stacks;
    }
}
