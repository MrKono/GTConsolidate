package kono.ceu.gtconsolidate.mixin;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.recipes.Recipe;

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

    // Extended Progress Line
    @Unique
    @Override
    public MultiblockDisplayText.Builder addExtendedProgressLine(int currentInt, int totalInt, double percent) {
        if (!isStructureFormed || !isActive) {
            return self();
        }
        double current = (double) currentInt / 20;
        double total = (double) totalInt / 20;
        int currentProgress = (int) (percent * (double) 100.0F);
        String c = String.format("%.2f", current);
        String t = String.format("%.2f", total);
        this.textList.add(new TextComponentTranslation("gtconsolidate.multiblock.progress", c, t,
                currentProgress));
        return self();
    }

    // Original: GregTechCEu#2673
    @Unique
    @Override
    public MultiblockDisplayText.Builder addOutputLine(Recipe recipe) {
        return addOutputLine(recipe, 1);
    }

    @Unique
    @Override
    public MultiblockDisplayText.Builder addOutputLine(Recipe recipe, int parallel) {
        if (!isStructureFormed || !isActive) {
            return self();
        }
        if (recipe == null) {
            return self();
        }
        if (!recipe.getAllItemOutputs().isEmpty()) {
            this.textList.add(new TextComponentTranslation("gtconsolidate.multiblock.recipe_outputs",
                    itemOutputsToString(recipe.getAllItemOutputs(), parallel)));
        }
        if (!recipe.getAllFluidOutputs().isEmpty()) {
            this.textList.add(new TextComponentTranslation("gtconsolidate.multiblock.recipe_outputs",
                    fluidOutputsToString(recipe.getAllFluidOutputs(), parallel)));
        }

        return self();
    }

    @Unique
    private static String fluidOutputsToString(List<FluidStack> stacks, int parallel) {
        StringBuilder output = new StringBuilder();
        for (FluidStack stack : stacks) {
            output.append("§b").append(stack.getLocalizedName()).append(" §fx§6 ").append(stack.amount * parallel)
                    .append("L§f, ");
        }
        String str = output.toString();
        return str.substring(0, str.length() - 2);
    }

    @Unique
    private static String itemOutputsToString(List<ItemStack> stacks, int parallel) {
        StringBuilder output = new StringBuilder();
        for (ItemStack stack : stacks) {
            output.append("§b").append(stack.getDisplayName()).append(" §fx§6 ").append(stack.getCount() * parallel)
                    .append("§f, ");
        }
        String str = output.toString();
        return str.substring(0, str.length() - 2);
    }
}
