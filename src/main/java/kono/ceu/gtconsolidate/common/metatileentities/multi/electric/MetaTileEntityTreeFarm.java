package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static kono.ceu.gtconsolidate.api.util.TreeFarmUtil.*;
import static kono.ceu.gtconsolidate.api.util.TreeFarmUtil.WorkPhase.HARVESTING_LEAVES;
import static kono.ceu.gtconsolidate.api.util.TreeFarmUtil.WorkPhase.HARVESTING_TREE;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import gregtech.api.capability.*;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;

import kono.ceu.gtconsolidate.api.util.GTConsolidateUtil;
import kono.ceu.gtconsolidate.api.util.TreeFarmUtil;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityTreeFarm extends MultiblockWithDisplayBase implements IControllable {

    private final int energyCheckInterval = 10; // 0.5 /s
    private final int maxWoodsPerTick = 8;
    private final int maxLeavesPerTick = 8;
    private int scanInterval;
    private int radiusScanning;
    private int radiusLeaf;
    private int slot;
    private int scanIndex = 0;

    private long harvestPerEnergy;
    private long workPerEnergy;

    private boolean isActive = true;
    private boolean isWorkingEnabled = true;
    private boolean hasEnoughEnergy = false;
    private boolean hasSpace = true;

    private IEnergyContainer energyContainer;

    private final Queue<BlockPos> scanQueue = new ArrayDeque<>();

    private final Set<BlockPos> visitedLogs = new HashSet<>();
    private final Set<BlockPos> harvestedLogs = new HashSet<>();
    private final Set<BlockPos> queuedLeaves = new HashSet<>();

    private final Deque<BlockPos> pendingLogs = new ArrayDeque<>();
    private final Deque<BlockPos> pendingLeaves = new ArrayDeque<>();

    private final Set<BlockPos> pendingSaplingPositions = new HashSet<>();

    private final BlockPos.MutableBlockPos mutableScanPos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos mutableBelowPos = new BlockPos.MutableBlockPos();

    private BlockPos scanStart = null;

    private final List<ItemStack> pendingDrops = new ArrayList<>();
    private final List<ItemStack> dummyStacks = new ArrayList<>();

    private TreeFarmUtil.WorkPhase workPhase = TreeFarmUtil.WorkPhase.IDLE;

    public MetaTileEntityTreeFarm(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.radiusScanning = 1;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityTreeFarm(metaTileEntityId);
    }

    protected void initializeAbilities() {
        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.importItems = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.exportItems = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
    }

    private void resetTileAbilities() {
        this.energyContainer = new EnergyContainerList(Lists.newArrayList());
        this.importItems = new GTItemStackHandler(this, 0);
        this.exportItems = new GTItemStackHandler(this, 0);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        setWorkPerEnergy();
        setHarvestPerEnergy();
        setRadiusLeaf();
        setScanInterval();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
    }

    @Override
    protected void updateFormedValid() {}

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("###############", "###############", "###############", "###############", "###############",
                        "DDDDDDDDDDDDDDD")
                .setRepeatable(5)
                .aisle("#####CCCCC#####", "#####CCCCC#####", "#####CCCCC#####", "#####CCCCC#####", "#####CCCCC#####",
                        "DDDDDDDDDDDDDDD")
                .aisle("#####CCCCC#####", "#####CPPPC#####", "#####CPPPC#####", "#####CPPPC#####", "#####CPPPC#####",
                        "DDDDDDDDDDDDDDD")
                .aisle("#####CCCCC#####", "#####CPGPC#####", "#####CPGPC#####", "#####CPGPC#####", "#####CPGPC#####",
                        "DDDDDDDDDDDDDDD")
                .aisle("#####CCCCC#####", "#####CPPPC#####", "#####CPPPC#####", "#####CPPPC#####", "#####CPPPC#####",
                        "DDDDDDDDDDDDDDD")
                .aisle("#####CCSCC#####", "#####CCCCC#####", "#####CCCCC#####", "#####CCCCC#####", "#####CCCCC#####",
                        "DDDDDDDDDDDDDDD")
                .aisle("###############", "###############", "###############", "###############", "###############",
                        "DDDDDDDDDDDDDDD")
                .setRepeatable(5)
                .where('C', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1, 1).setMaxGlobalLimited(5))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1, 1).setMaxGlobalLimited(5))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(1, 1).setMaxGlobalLimited(5))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1)))
                .where('D',
                        blocks(Blocks.DIRT, Blocks.GRASS).or(any()).addTooltips("gtconsolidate.multiblock.pattern.any"))
                .where('G',
                        states(MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE)))
                .where('S', selfPredicate())
                .where('#', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.CUTTER_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(isWorkingEnabled, hasSpace)
                .addLowPowerLine(!hasEnoughEnergy)
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.radius",
                                TextComponentUtil.stringWithColor(
                                        TextFormatting.AQUA,
                                        TextFormattingUtil.formatNumbers(getScanRadius()))));

                        ITextComponent radiusLeafBody = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.radius_leaf.body",
                                TextComponentUtil.stringWithColor(
                                        TextFormatting.GREEN,
                                        TextFormattingUtil.formatNumbers(radiusLeaf)));
                        ITextComponent radiusLeafHover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.radius_leaf.hover");
                        tl.add(TextComponentUtil.setHover(radiusLeafBody, radiusLeafHover));

                        ITextComponent workEnergyBody = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.work_per_energy.body",
                                TextFormattingUtil.formatNumbers(workPerEnergy));
                        ITextComponent workEnergyHover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.work_per_energy.hover");
                        tl.add(TextComponentUtil.setHover(workEnergyBody, workEnergyHover));

                        ITextComponent harvestEnergyBody = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.harvest_per_energy.body",
                                TextFormattingUtil.formatNumbers(harvestPerEnergy));
                        ITextComponent harvestEnergyHover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.harvest_per_energy.hover");
                        tl.add(TextComponentUtil.setHover(harvestEnergyBody, harvestEnergyHover));

                        ITextComponent intervalBody = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.scan_interval.body",
                                TextFormattingUtil.formatNumbers(scanInterval));
                        ITextComponent intervalHover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.scan_interval.hover");
                        tl.add(TextComponentUtil.setHover(intervalBody, intervalHover));

                        ITextComponent fortuneBody = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.tree_farm.fortune.body",
                                TextComponentUtil.stringWithColor(
                                        TextFormatting.AQUA,
                                        TextFormattingUtil.formatNumbers(getFortuneLevel())));
                        ITextComponent fortuneHover = TextComponentUtil.translationWithColor(
                                TextFormatting.AQUA,
                                "gtconsolidate.multiblock.tree_farm.fortune.hover");
                        tl.add(TextComponentUtil.setHover(fortuneBody, fortuneHover));

                        if (!hasSpace) {
                            tl.add(TextComponentUtil.translationWithColor(
                                    TextFormatting.YELLOW,
                                    "gtconsolidate.multiblock.tree_farm.not_enough_space"));
                        } else {
                            if (hasEnoughEnergy) {
                                if (workPhase == HARVESTING_TREE) {
                                    tl.add(TextComponentUtil.translationWithColor(
                                            TextFormatting.GOLD,
                                            "gtconsolidate.multiblock.tree_farm.status.harvesting_log"));
                                } else if (workPhase == HARVESTING_LEAVES) {
                                    tl.add(TextComponentUtil.translationWithColor(
                                            TextFormatting.GREEN,
                                            "gtconsolidate.multiblock.tree_farm.status.harvesting_leaf"));
                                } else {
                                    tl.add(TextComponentUtil.translationWithColor(
                                            TextFormatting.AQUA,
                                            "gtconsolidate.multiblock.tree_farm.status.scanning"));
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void update() {
        super.update();
        if (this.getWorld().isRemote) return;

        if (!isWorkingEnabled || this.energyContainer == null) return;

        long stored = energyContainer.getEnergyStored();

        switch (workPhase) {
            case SCANNING_TREE:
                tickTreeScan();
                return;

            case HARVESTING_TREE:
                tickHarvestingWoods();
                return;

            case HARVESTING_LEAVES:
                tickHarvestingLeaves();
                return;
            case IDLE:
            default:
                break;
        }

        setWorkPerEnergy();
        setHarvestPerEnergy();
        if (getOffsetTimer() % energyCheckInterval == 0) {
            checkHasSpace();
            if (stored >= workPerEnergy) {
                this.hasEnoughEnergy = true;
                if (getOffsetTimer() % scanInterval == 0) {
                    scanNextBlock();
                }
            } else {
                this.hasEnoughEnergy = false;
            }
            energyContainer.removeEnergy(Math.min(stored, workPerEnergy));
        }
    }

    @SuppressWarnings("ResultIsAlwaysInverted")
    private boolean canContinueHarvesting() {
        return energyContainer.getEnergyStored() >= harvestPerEnergy;
    }

    private void checkHasSpace() {
        this.hasSpace = GTTransferUtils.addItemsToItemHandler(exportItems, true, dummyStacks);
    }

    // == Setter ==
    private void setWorkPerEnergy() {
        this.workPerEnergy = getHighestVoltage() / 4;
    }

    private void setHarvestPerEnergy() {
        this.harvestPerEnergy = Math.max(1, Math.toIntExact(getHighestVoltage() / 2));
    }

    private void setRadiusLeaf() {
        this.radiusLeaf = Math.max(1, GTUtility.getTierByVoltage(getHighestVoltage()));
    }

    private void setScanInterval() {
        int r = 2 * radiusScanning + 1;
        this.scanInterval = 4000 / (r * r);
    }

    // == Getter ==
    private long getHighestVoltage() {
        if (energyContainer instanceof EnergyContainerList) {
            return Math.max(8L, ((EnergyContainerList) energyContainer).getHighestInputVoltage());
        }
        return 8L;
    }

    private int getScanRadius() {
        return radiusScanning;
    }

    private int getFortuneLevel() {
        int num;
        if (energyContainer != null && energyContainer instanceof EnergyContainerList) {
            num = ((EnergyContainerList) energyContainer).getNumHighestInputContainers();
        } else {
            num = 1;
        }
        return num * Math.max(0, GTUtility.getTierByVoltage(getHighestVoltage()) - 1);
    }

    // == Tree Farm ==
    private void scanNextBlock() {
        BlockPos center = getPos().offset(getFrontFacing().getOpposite(), 2);
        int range = getScanRadius();
        int size = range * 2 + 1;
        int total = size * size;

        int xOffset = scanIndex % size;
        int zOffset = scanIndex / size;

        int x = center.getX() + xOffset - range;
        int y = center.getY() + 8;
        int z = center.getZ() + zOffset - range;

        mutableScanPos.setPos(x, y, z);

        // skip if unloading chunk
        if (this.getWorld().isBlockLoaded(mutableScanPos)) {
            processScanPos(mutableScanPos);
        }
        scanIndex = (scanIndex + 1) % total;
    }

    private void processScanPos(BlockPos pos) {
        World world = this.getWorld();
        IBlockState state = world.getBlockState(pos);

        // if log, try to harvest logs
        if (isLog(world, pos)) {
            startTreeScan(pos);
        }

        // if blow block is air, try to place sapling
        if (world.isAirBlock(pos)) {
            mutableBelowPos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
            IBlockState belowState = world.getBlockState(mutableBelowPos);
            Block belowBlock = belowState.getBlock();

            if (isSoil(belowBlock)) {
                placeSapling(world, pos);
            }
        }
    }

    private void tickHarvestingWoods() {
        if (!canContinueHarvesting()) {
            energyContainer.removeEnergy(Math.min(energyContainer.getEnergyStored(), harvestPerEnergy));
            return;
        }
        energyContainer.removeEnergy(Math.min(energyContainer.getEnergyStored(), harvestPerEnergy));

        World world = this.getWorld();
        if (pendingLogs.isEmpty()) {
            return;
        }

        BlockPos target = pendingLogs.removeLast();

        if (world.isBlockLoaded(target)) {
            IBlockState state = world.getBlockState(target);
            Block block = state.getBlock();

            if (block.isWood(world, target)) {
                harvestedLogs.add(target.toImmutable());
                breakLogWithAxe((WorldServer) world, target, pendingDrops, !hasSpace);
            }
        }

        if (pendingLogs.isEmpty()) {
            startLeafScan();
        }

        moveToOutputInventory();
    }

    private void startTreeScan(BlockPos start) {
        this.workPhase = WorkPhase.SCANNING_TREE;
        this.scanStart = start.toImmutable();

        this.scanQueue.clear();
        this.visitedLogs.clear();
        this.pendingLogs.clear();
        this.pendingLeaves.clear();
        this.queuedLeaves.clear();
        this.pendingDrops.clear();
        this.harvestedLogs.clear();

        this.scanQueue.add(this.scanStart);
    }

    private void tickTreeScan() {
        boolean finished = TreeFarmUtil.scanTreeStep(this.getWorld(), scanStart, scanQueue, visitedLogs,
                maxWoodsPerTick);
        if (finished) {
            finishTreeScan();
        }
    }

    private void finishTreeScan() {
        if (visitedLogs.isEmpty()) {
            resetWork();
            return;
        }

        TreeFarmUtil.sortLogsByY(visitedLogs, pendingLogs);

        BlockPos baseLog = visitedLogs.stream()
                .min(Comparator.comparingInt(BlockPos::getY))
                .orElse(null);

        if (baseLog != null) {
            pendingSaplingPositions.add(baseLog.toImmutable());
        }

        visitedLogs.clear();
        scanQueue.clear();
        scanStart = null;

        workPhase = pendingLogs.isEmpty() ? WorkPhase.IDLE : WorkPhase.HARVESTING_TREE;
    }

    private void startLeafScan() {
        pendingLeaves.clear();
        queuedLeaves.clear();

        for (BlockPos logPos : harvestedLogs) {
            TreeFarmUtil.collectNearbyLeaves(this.getWorld(), logPos, pendingLeaves, queuedLeaves, radiusLeaf);
        }

        if (pendingLeaves.isEmpty()) {
            plantPendingSaplings();
            moveToOutputInventory();
            resetWork();
        } else {
            workPhase = WorkPhase.HARVESTING_LEAVES;
        }
    }

    private void tickHarvestingLeaves() {
        World world = this.getWorld();

        for (int i = 0; i < maxLeavesPerTick && !pendingLeaves.isEmpty(); i++) {

            BlockPos pos = pendingLeaves.removeFirst();

            if (!world.isBlockLoaded(pos)) {
                continue;
            }

            IBlockState state = world.getBlockState(pos);

            if (state.getBlock().isLeaves(state, world, pos)) {
                TreeFarmUtil.breakLeaves((WorldServer) world, pos, pendingDrops, !hasSpace, getFortuneLevel());
            }
        }

        if (pendingLeaves.isEmpty()) {
            plantPendingSaplings();
            moveToOutputInventory();
            resetWork();
        }
    }

    private void plantPendingSaplings() {
        World world = this.getWorld();
        if (!(world instanceof WorldServer)) {
            return;
        }

        Iterator<BlockPos> iterator = pendingSaplingPositions.iterator();

        while (iterator.hasNext()) {
            BlockPos logPos = iterator.next();
            BlockPos soilPos = logPos.down();
            IBlockState belowState = world.getBlockState(soilPos);
            Block belowBlock = belowState.getBlock();

            if (!world.isBlockLoaded(logPos) || !world.isBlockLoaded(soilPos)) {
                continue;
            }

            if (!world.isAirBlock(logPos)) {
                continue;
            }

            if (!TreeFarmUtil.isSoil(belowBlock)) {
                continue;
            }

            placeSapling(world, logPos);

            iterator.remove();
        }
    }

    private void resetWork() {
        workPhase = WorkPhase.IDLE;
        scanStart = null;

        scanQueue.clear();
        visitedLogs.clear();
        harvestedLogs.clear();
        pendingLogs.clear();
        pendingLeaves.clear();
        queuedLeaves.clear();
    }

    private void placeSapling(World world, BlockPos pos) {
        if (!(world instanceof WorldServer worldServer)) {
            return;
        }

        slot = GTConsolidateUtil.getFirstUnemptyItemSlot(importItems, slot + 1);

        if (slot == -1) return;

        ItemStack stack = importItems.extractItem(slot, 1, true);

        if (stack.isEmpty()) return;

        if (!TreeFarmUtil.isSapling(stack)) {
            ItemStack mismatch = importItems.extractItem(slot, importItems.getStackInSlot(slot).getCount(), false);
            if (GTTransferUtils.addItemsToItemHandler(exportItems, true, Collections.singletonList(mismatch))) {
                GTTransferUtils.addItemsToItemHandler(exportItems, false, Collections.singletonList(mismatch));
            }
            return;
        }

        TreeFarmUtil.placeSapling(worldServer, pos, stack);
        importItems.extractItem(slot, 1, false);
    }

    private void moveToOutputInventory() {
        if (GTTransferUtils.addItemsToItemHandler(exportItems, true, pendingDrops)) {
            GTTransferUtils.addItemsToItemHandler(exportItems, false, pendingDrops);
            dummyStacks.clear();
            dummyStacks.addAll(pendingDrops);
            pendingDrops.clear();
        } else {
            this.hasSpace = false;
        }
    }

    // GUI
    private void decrementRadius(Widget.ClickData data) {
        this.radiusScanning = MathHelper.clamp(radiusScanning - (data.isShiftClick ? 2 : 1), 1, 7);
        setScanInterval();
    }

    private void incrementRadius(Widget.ClickData data) {
        this.radiusScanning = MathHelper.clamp(radiusScanning + (data.isShiftClick ? 2 : 1), 1, 7);
        setScanInterval();
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementRadius)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gtconsolidate.multiblock.tree_farm.radius_decrement"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementRadius)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gtconsolidate.multiblock.tree_farm.radius_increment"));
        return group;
    }

    // Other
    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public boolean allowsFlip() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public boolean isWorkingEnabled() {
        return isWorkingEnabled && hasSpace;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        this.isWorkingEnabled = isWorkingAllowed;
        markDirty();
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(isWorkingEnabled));
        }
    }

    @Override
    public boolean isActive() {
        return this.hasSpace && this.hasEnoughEnergy;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("isActive", this.isActive);
        data.setBoolean("isWorkingEnabled", this.isWorkingEnabled);
        data.setBoolean("hasEnoughEnergy", this.hasEnoughEnergy);
        data.setBoolean("hasSpace", this.hasSpace);
        data.setInteger("radius", this.radiusScanning);
        data.setInteger("scanIndex", this.scanIndex);
        data.setLong("harvestPerEnergy", this.harvestPerEnergy);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.isActive = data.getBoolean("isActive");
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        this.hasEnoughEnergy = data.getBoolean("hasEnoughEnergy");
        this.hasSpace = data.getBoolean("hasSpace");
        this.radiusScanning = data.getInteger("radius");
        this.scanIndex = data.getInteger("scanIndex");
        this.harvestPerEnergy = data.getLong("harvestPerEnergy");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.isActive);
        buf.writeBoolean(this.isWorkingEnabled);
        buf.writeBoolean(this.hasEnoughEnergy);
        buf.writeBoolean(this.hasSpace);
        buf.writeInt(this.radiusScanning);
        buf.writeInt(this.scanIndex);
        buf.writeLong(this.harvestPerEnergy);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isActive = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
        this.hasEnoughEnergy = buf.readBoolean();
        this.hasSpace = buf.readBoolean();
        this.radiusScanning = buf.readInt();
        this.scanIndex = buf.readInt();
        this.harvestPerEnergy = buf.readLong();
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            this.isActive = buf.readBoolean();
            scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.isWorkingEnabled = buf.readBoolean();
            scheduleRenderUpdate();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.2.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.2.2"));
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.2.3"));
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.2.4"));
        tooltip.add(I18n.format("gtconsolidate.machine.tree_farm.tooltip.3"));
    }
}
