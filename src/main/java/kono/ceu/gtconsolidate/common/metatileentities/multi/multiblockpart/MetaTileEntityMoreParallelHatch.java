package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;

import gregicality.multiblocks.api.capability.IParallelHatch;
import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.common.metatileentities.multiblockpart.MetaTileEntityParallelHatch;

public class MetaTileEntityMoreParallelHatch extends MetaTileEntityParallelHatch {

    private static final int MIN_PARALLEL = 1;

    private final int maxParallel;

    private int currentParallel;

    public MetaTileEntityMoreParallelHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.maxParallel = (int) Math.pow(4, tier + 4);
        this.currentParallel = this.maxParallel;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMoreParallelHatch(this.metaTileEntityId, this.getTier());
    }

    @Override
    public int getCurrentParallel() {
        return currentParallel;
    }

    @Override
    public void setCurrentParallel(int parallelAmount) {
        this.currentParallel = MathHelper.clamp(this.currentParallel + parallelAmount, 1, this.maxParallel);
    }

    protected ModularUI createUI(@NotNull EntityPlayer entityPlayer) {
        ServerWidgetGroup parallelAmountGroup = new ServerWidgetGroup(() -> true);
        parallelAmountGroup.addWidget(new ImageWidget(62, 36, 53, 20, GuiTextures.DISPLAY)
                .setTooltip("gcym.machine.parallel_hatch.display"));

        parallelAmountGroup.addWidget(new IncrementButtonWidget(118, 36, 30, 20, 1, 4, 16, 64, this::setCurrentParallel)
                .setDefaultTooltip()
                .setShouldClientCallback(false));
        parallelAmountGroup
                .addWidget(new IncrementButtonWidget(29, 36, 30, 20, -1, -4, -16, -64, this::setCurrentParallel)
                        .setDefaultTooltip()
                        .setShouldClientCallback(false));

        parallelAmountGroup.addWidget(new TextFieldWidget2(63, 42, 51, 20, this::getParallelAmountToString, val -> {
            if (val != null && !val.isEmpty()) {
                setCurrentParallel(Integer.parseInt(val));
            }
        })
                .setCentered(true)
                .setNumbersOnly(1, this.maxParallel)
                .setMaxLength(8)
                .setValidator(getTextFieldValidator(() -> this.maxParallel)));

        return ModularUI.defaultBuilder()
                .widget(new LabelWidget(5, 5, getMetaFullName()))
                .widget(parallelAmountGroup)
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 0)
                .build(getHolder(), entityPlayer);
    }

    @Override
    public String getParallelAmountToString() {
        return Integer.toString(this.currentParallel);
    }

    public static @NotNull Function<String, String> getTextFieldValidator(IntSupplier maxSupplier) {
        return val -> {
            if (val.isEmpty())
                return String.valueOf(MIN_PARALLEL);
            int max = maxSupplier.getAsInt();
            int num;
            try {
                num = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
                return String.valueOf(max);
            }
            if (num < MIN_PARALLEL)
                return String.valueOf(MIN_PARALLEL);
            if (num > max)
                return String.valueOf(max);
            return val;
        };
    }

    @Override
    public MultiblockAbility<IParallelHatch> getAbility() {
        return GCYMMultiblockAbility.PARALLEL_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull List<IParallelHatch> list) {
        list.add(this);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("gcym.machine.parallel_hatch.tooltip", this.maxParallel));
        tooltip.add(I18n.format("gregtech.universal.disabled"));
    }
}
