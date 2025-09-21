package kono.ceu.gtconsolidate.mixin;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.timeUnit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.chance.output.impl.ChancedFluidOutput;
import gregtech.api.recipes.chance.output.impl.ChancedItemOutput;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.api.util.GTConsolidateUtil;
import kono.ceu.gtconsolidate.api.util.mixinhelper.AbstractRecipeLogicMixinHelper;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;

@Mixin(value = MultiblockDisplayText.Builder.class, remap = false)
public class MultiblockDisplayTextMixin implements MultiblockDisplayTextMixinHelper {

    @Shadow
    @Final
    private java.util.List<ITextComponent> textList;
    @Shadow
    @Final
    private boolean isStructureFormed;
    @Shadow
    @Final
    private boolean isActive;

    @SuppressWarnings("unchecked")
    private MultiblockDisplayText.Builder self() {
        return (MultiblockDisplayText.Builder) (Object) this;
    }

    // Current Parallel Line
    @Unique
    @Override
    public MultiblockDisplayText.Builder addExtendedParallelLine(AbstractRecipeLogic logic) {
        if (!isStructureFormed) {
            return self();
        }
        int maxParallel = logic.getParallelLimit();
        if (!isActive || !GTConsolidateConfig.feature.modifyParallelLine) {
            if (maxParallel > 1) {
                ITextComponent parallels = TextComponentUtil.stringWithColor(
                        TextFormatting.DARK_PURPLE,
                        TextFormattingUtil.formatNumbers(maxParallel));

                this.textList.add(TextComponentUtil.translationWithColor(
                        TextFormatting.GRAY,
                        "gregtech.multiblock.parallel",
                        parallels));
                return self();
            }
        }
        int currentParallel = ((AbstractRecipeLogicMixinHelper) logic).getCurrentParallel();
        if (currentParallel == 0) currentParallel = 1;
        ITextComponent current = TextComponentUtil.translationWithColor(TextFormatting.LIGHT_PURPLE,
                TextFormattingUtil.formatNumbers(currentParallel));
        ITextComponent max = TextComponentUtil.translationWithColor(TextFormatting.DARK_PURPLE,
                TextFormattingUtil.formatNumbers(maxParallel));

        ITextComponent body = TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                "gtconsolidate.multiblock.parallel_extended",
                current, max);
        ITextComponent hover = TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                "gtconsolidate.multiblock.parallel_extended_hover");

        this.textList.add(TextComponentUtil.setHover(body, hover));
        return self();
    }

    // Extended Progress Line
    @Unique
    @Override
    public MultiblockDisplayText.Builder addExtendedProgressLine(AbstractRecipeLogic logic) {
        if (!isStructureFormed || !isActive) {
            return self();
        }
        int currentProgress = (int) (logic.getProgressPercent() * (double) 100.0F);
        if (!GTConsolidateConfig.feature.modifyProgressLine) {
            this.textList.add(new TextComponentTranslation("gregtech.multiblock.progress", currentProgress));
            return self();
        }
        double current = (double) logic.getProgress() / 20;
        double total = (double) logic.getMaxProgress() / 20;

        this.textList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                "gtconsolidate.multiblock.progress", formatTime(current), formatTime(total), currentProgress));
        return self();
    }

    // Original: GregTechCEu#2673
    @Unique
    @Override
    public MultiblockDisplayText.Builder addOutputLine(AbstractRecipeLogic logic) {
        Recipe recipe = logic.getPreviousRecipe();
        if (!isStructureFormed || !isActive || !GTConsolidateConfig.feature.addOutputLine) {
            return self();
        }
        if (recipe == null) {
            return self();
        }
        RecipeMap<?> map = logic.getRecipeMap();
        Recipe trimmed;
        MetaTileEntity mte = logic.getMetaTileEntity();
        trimmed = Recipe.trimRecipeOutputs(recipe, map, mte.getItemOutputLimit(), mte.getFluidOutputLimit());

        long eut = trimmed == null ? 0 : trimmed.getEUt();
        long maxVoltage = logic.getMaximumOverclockVoltage();
        int recipeTier = GTUtility.getTierByVoltage(eut);
        int machineTier = GTConsolidateUtil.getOCTierByVoltage(maxVoltage);

        List<ItemStack> outputStack = new ArrayList<>();
        List<ChancedItemOutput> chancedItemOutputs = new ArrayList<>();
        List<FluidStack> fluidStacks = new ArrayList<>();
        List<ChancedFluidOutput> chancedFluidOutputs = new ArrayList<>();

        // Outputs
        outputStack.addAll(trimmed.getOutputs());
        String outputs = itemOutputsToString(outputStack);

        fluidStacks.addAll(trimmed.getFluidOutputs());
        String fluidOutputs = fluidOutputsToString(fluidStacks);

        // Chanced Outputs
        chancedItemOutputs.addAll(trimmed.getChancedOutputs().getChancedEntries());
        StringBuilder chancedOutputBuilder = new StringBuilder();
        for (ChancedItemOutput chancedItemOutput : chancedItemOutputs) {
            int ch = map.chanceFunction.getBoostedChance(chancedItemOutput, recipeTier, machineTier);
            double chance = (double) ch / 100;
            String chanceStr = String.format("%.2f", chance);
            chancedOutputBuilder.append("§b").append(chancedItemOutput.getIngredient().getDisplayName())
                    .append(" §f(§a").append(chanceStr).append("§f% ) x §6")
                    .append(chancedItemOutput.getIngredient().getCount())
                    .append("§f, ");
        }
        String chancedOutputs = chancedOutputBuilder.toString();
        if (!chancedOutputs.isEmpty()) chancedOutputs = chancedOutputs.substring(0, chancedOutputs.length() - 2);

        chancedFluidOutputs.addAll(trimmed.getChancedFluidOutputs().getChancedEntries());
        StringBuilder chancedFluidOutputBuilder = new StringBuilder();
        for (ChancedFluidOutput chancedFluidOutput : chancedFluidOutputs) {
            int ch = map.chanceFunction.getBoostedChance(chancedFluidOutput, recipeTier, machineTier);
            double chance = (double) ch / 100;
            String chanceStr = String.format("%.2f", chance);
            chancedFluidOutputBuilder.append("§b").append(chancedFluidOutput.getIngredient().getLocalizedName())
                    .append(" §f(§a").append(chanceStr).append("§f %) x§6 ")
                    .append(String.format("%,d", chancedFluidOutput.getIngredient().amount)).append(" §fL, ");
        }
        String chancedFluidOutputsStr = chancedFluidOutputBuilder.toString();
        if (!chancedFluidOutputsStr.isEmpty()) {
            chancedFluidOutputsStr = chancedFluidOutputsStr.substring(0, chancedFluidOutputsStr.length() - 2);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(outputs);
        if (!outputs.isEmpty()) {
            builder.append(", ");
        }
        builder.append(chancedOutputs);
        if (!chancedOutputs.isEmpty()) {
            builder.append(", ");
        }
        builder.append(fluidOutputs);
        if (!fluidOutputs.isEmpty()) {
            builder.append(", ");
        }
        builder.append(chancedFluidOutputsStr);

        String text = builder.toString();
        if (text.length() >= 2) {
            text = text.substring(0, text.length() - 2);
        }

        ITextComponent body = TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                "gtconsolidate.multiblock.recipe_outputs", text);
        ITextComponent hover = TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                "gtconsolidate.multiblock.recipe_outputs_hover");
        this.textList.add(TextComponentUtil.setHover(body, hover));

        return self();
    }

    @Unique
    private static ITextComponent formatTime(double time) {
        if (timeUnit().equals("hr") && time >= 3600) {
            int hr = (int) (time / 3600);
            int min = (int) ((time % 3600) / 60);
            double sec = time % 60;
            return TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gtconsolidate.multiblock.progress_hr", hr, min, String.format("%.2f", sec));
        } else if ((timeUnit().equals("min") || timeUnit().equals("hr")) && time >= 60) {
            int min = (int) (time / 60);
            double sec = time % 60;
            return TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gtconsolidate.multiblock.progress_min", min, String.format("%.2f", sec));
        } else {
            return TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gtconsolidate.multiblock.progress_sec", String.format("%.2f", time));
        }
    }

    @Unique
    private static String fluidOutputsToString(List<FluidStack> stacks) {
        StringBuilder output = new StringBuilder();
        for (FluidStack stack : stacks) {
            output.append("§b").append(stack.getLocalizedName()).append(" §fx§6 ")
                    .append(String.format("%,d", stack.amount))
                    .append(" §fL, ");
        }
        String str = output.toString();
        return str.isEmpty() ? str : str.substring(0, str.length() - 2);
    }

    @Unique
    private static String itemOutputsToString(List<ItemStack> stacks) {
        StringBuilder output = new StringBuilder();
        for (ItemStack stack : stacks) {
            output.append("§b").append(stack.getDisplayName()).append(" §fx§6 ").append(stack.getCount())
                    .append("§f, ");
        }
        String str = output.toString();
        return str.isEmpty() ? str : str.substring(0, str.length() - 2);
    }
}
