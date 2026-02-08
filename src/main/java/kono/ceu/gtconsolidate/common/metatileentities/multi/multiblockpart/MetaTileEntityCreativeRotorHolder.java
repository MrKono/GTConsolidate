package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IRotorHolder;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockNotifiablePart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityCreativeRotorHolder extends MetaTileEntityMultiblockNotifiablePart
                                               implements IMultiblockAbilityPart<IRotorHolder>, IRotorHolder {

    private final int MAX = Integer.MAX_VALUE;
    private int tier;
    private int maxSpeed;
    private int currentSpeed;
    private int rotorEfficiency;
    private int rotorPower;
    private boolean frontFaceFree;

    public MetaTileEntityCreativeRotorHolder(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTValues.MAX, false);
        this.tier = GTValues.ULV;
        this.maxSpeed = 5000;
        this.rotorEfficiency = 100;
        this.rotorPower = 100;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCreativeRotorHolder(metaTileEntityId);
    }

    @Override
    public void update() {
        super.update();
        if (getWorld().isRemote) return;

        if (getOffsetTimer() % 20 == 0) {
            boolean isFrontFree = checkTurbineFaceFree();
            if (isFrontFree != this.frontFaceFree) {
                this.frontFaceFree = isFrontFree;
                writeCustomData(GregtechDataCodes.FRONT_FACE_FREE, buf -> buf.writeBoolean(this.frontFaceFree));
            }
        }

        MetaTileEntityLargeTurbine controller = (MetaTileEntityLargeTurbine) getController();

        if (controller != null) {
            if (controller.isActive()) {
                setRotorSpeed(this.maxSpeed);
            } else if (!controller.getRecipeMapWorkable().isWorkingEnabled()) {
                setRotorSpeed(0);
            }
        }
    }

    private void setRotorSpeed(int speed) {
        this.currentSpeed = speed;
        markDirty();
    }

    private void setTier(int tier) {
        this.tier = tier;
        markDirty();
    }

    private int getRotorColor(int tier) {
        return GTValues.VC[tier];
    }

    @Override
    public boolean hasRotor() {
        MetaTileEntityLargeTurbine controller = (MetaTileEntityLargeTurbine) getController();
        return controller != null;
    }

    private boolean isRotorSpinning() {
        MetaTileEntityLargeTurbine controller = (MetaTileEntityLargeTurbine) getController();
        return controller != null && controller.isActive();
    }

    @Override
    public int getRotorSpeed() {
        return this.currentSpeed;
    }

    @Override
    public int getRotorEfficiency() {
        return this.rotorEfficiency;
    }

    @Override
    public int getRotorPower() {
        return this.rotorPower;
    }

    @Override
    public int getRotorDurabilityPercent() {
        return 100;
    }

    @Override
    public void damageRotor(int i) {}

    @Override
    public int getMaxRotorHolderSpeed() {
        return this.maxSpeed;
    }

    @Override
    public int getHolderPowerMultiplier() {
        int tierDifference = getTierDifference();
        if (tierDifference == -1) return -1;

        return (int) Math.pow(2, getTierDifference());
    }

    @Override
    public int getHolderEfficiency() {
        int tierDifference = getTierDifference();
        if (tierDifference == -1)
            return -1;

        return 100 + 10 * tierDifference;
    }

    private int getTierDifference() {
        return getControllerTier() == -1 ? -1 : this.tier - getControllerTier();
    }

    private int getControllerTier() {
        if (getController() instanceof ITieredMetaTileEntity) {
            return ((ITieredMetaTileEntity) getController()).getTier();
        }

        return -1;
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

    public boolean isFrontFaceFree() {
        return frontFaceFree;
    }

    private boolean checkTurbineFaceFree() {
        final EnumFacing front = getFrontFacing();
        final EnumFacing upwards = front.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.UP;

        for (int left = -1; left <= 1; left++) {
            for (int up = -1; up <= 1; up++) {
                final BlockPos checkPos = RelativeDirection.offsetPos(
                        getPos(), front, upwards, false, up, left, 1);
                final IBlockState state = getWorld().getBlockState(checkPos);
                if (!state.getBlock().isAir(state, getWorld(), checkPos)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setMaxSpeed(int speed) {
        this.maxSpeed = MathHelper.clamp(speed, 1, MAX);
    }

    private String getMaxSpeedToString() {
        return Integer.toString(this.maxSpeed);
    }

    private void setRotorEfficiency(int efficiency) {
        this.rotorEfficiency = MathHelper.clamp(efficiency, 1, MAX);
    }

    private String getRotorEfficiencyToString() {
        return Integer.toString(this.rotorEfficiency);
    }

    private void setRotorPower(int power) {
        this.rotorPower = MathHelper.clamp(power, 1, MAX);
    }

    private String getRotorPowerToString() {
        return Integer.toString(this.rotorPower);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 220, 166)
                .widget(new LabelWidget(5, 5, getMetaFullName()))
                // tier line
                .widget(new LabelWidget(5, 23, "gtconsolidate.machine.creative_rotor_holder.tier"))
                .widget(new CycleButtonWidget(30, 16, 30, 20, GTValues.VNF, () -> tier, this::setTier))
                .widget(new AdvancedTextWidget(65, 16, this::addTierInfoText, 4210752).setMaxWidthLimit(150));

        // max speed line
        int yPos = 40;
        builder.widget(
                new LabelWidget(55, yPos + 6, "gtconsolidate.machine.creative_rotor_holder.max_speed")
                        .setXCentered(true))
                .widget(new ImageWidget(103, yPos, 74, 20, GuiTextures.DISPLAY))
                .widget((new TextFieldWidget2(105, yPos + 6, 70, 16, this::getMaxSpeedToString, val -> {
                    if (val != null && !val.isEmpty()) {
                        setMaxSpeed(Integer.parseInt(val));
                    }
                })
                        .setCentered(true)
                        .setNumbersOnly(1, this.MAX)
                        .setMaxLength(19)
                        .setValidator(getTextFieldValidator(() -> this.MAX))))
                .widget(new LabelWidget(180, yPos + 6, "RPM"));

        // rotor efficiency line
        yPos += 35;
        builder.widget(
                new LabelWidget(55, yPos + 6, "gtconsolidate.machine.creative_rotor_holder.rotor_eff")
                        .setXCentered(true))
                .widget(new ImageWidget(103, yPos, 74, 20, GuiTextures.DISPLAY))
                .widget((new TextFieldWidget2(105, yPos + 6, 70, 16, this::getRotorEfficiencyToString, val -> {
                    if (val != null && !val.isEmpty()) {
                        setRotorEfficiency(Integer.parseInt(val));
                    }
                })
                        .setCentered(true)
                        .setNumbersOnly(1, this.MAX)
                        .setMaxLength(19)
                        .setValidator(getTextFieldValidator(() -> this.MAX))))
                .widget(new LabelWidget(180, yPos + 6, "%%"));

        // rotor power line
        yPos += 35;
        builder.widget(
                new LabelWidget(55, yPos + 6, "gtconsolidate.machine.creative_rotor_holder.rotor_pow")
                        .setXCentered(true))
                .widget(new ImageWidget(103, yPos, 74, 20, GuiTextures.DISPLAY))
                .widget((new TextFieldWidget2(105, yPos + 6, 70, 16, this::getRotorPowerToString, val -> {
                    if (val != null && !val.isEmpty()) {
                        setRotorPower(Integer.parseInt(val));
                    }
                })
                        .setCentered(true)
                        .setNumbersOnly(1, this.MAX)
                        .setMaxLength(19)
                        .setValidator(getTextFieldValidator(() -> this.MAX))))
                .widget(new LabelWidget(180, yPos + 6, "%%"));

        return builder.build(getHolder(), entityPlayer);
    }

    public static @NotNull Function<String, String> getTextFieldValidator(IntSupplier maxSupplier) {
        int MIN = 1;
        return val -> {
            if (val.isEmpty())
                return String.valueOf(MIN);
            int max = maxSupplier.getAsInt();
            int num;
            try {
                num = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
                return String.valueOf(max);
            }
            if (num < MIN)
                return String.valueOf(MIN);
            if (num > max)
                return String.valueOf(max);
            return val;
        };
    }

    public void addTierInfoText(List<ITextComponent> textList) {
        textList.add(TextComponentUtil.translationWithColor(
                getTierDifference() > -1 ? TextFormatting.AQUA : TextFormatting.YELLOW,
                getTierDifference() > -1 ? "gtconsolidate.machine.creative_rotor_holder.not_work" :
                        "gtconsolidate.machine.creative_rotor_holder.tier_low"));
    }

    @Override
    public MultiblockAbility<IRotorHolder> getAbility() {
        return MultiblockAbility.ROTOR_HOLDER;
    }

    @Override
    public void registerAbilities(List<IRotorHolder> abilityList) {
        abilityList.add(this);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.ROTOR_HOLDER_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        Textures.LARGE_TURBINE_ROTOR_RENDERER.renderSided(renderState, translation, pipeline, getFrontFacing(),
                getController() != null, hasRotor(), isRotorSpinning(), getRotorColor(this.tier));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("currentSpeed", currentSpeed);
        data.setInteger("maxSpeed", this.maxSpeed);
        data.setInteger("efficiency", this.rotorEfficiency);
        data.setInteger("power", this.rotorPower);
        data.setInteger("tier", this.tier);
        data.setBoolean("FrontFree", frontFaceFree);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.currentSpeed = data.getInteger("currentSpeed");
        this.maxSpeed = data.getInteger("maxSpeed");
        this.rotorEfficiency = data.getInteger("efficiency");
        this.rotorPower = data.getInteger("power");
        this.tier = data.getInteger("tier");
        this.frontFaceFree = data.getBoolean("FrontFree");
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.FRONT_FACE_FREE) {
            this.frontFaceFree = buf.readBoolean();
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.maxSpeed);
        buf.writeInt(this.rotorEfficiency);
        buf.writeInt(this.rotorPower);
        buf.writeInt(this.tier);
        buf.writeBoolean(frontFaceFree);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.maxSpeed = buf.readInt();
        this.rotorEfficiency = buf.readInt();
        this.rotorPower = buf.readInt();
        this.tier = buf.readInt();
        this.frontFaceFree = buf.readBoolean();
        scheduleRenderUpdate();
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.creative_tooltip.1") + TooltipHelper.RAINBOW +
                I18n.format("gregtech.creative_tooltip.2") + I18n.format("gregtech.creative_tooltip.3"));
    }
}
