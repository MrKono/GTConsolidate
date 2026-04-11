package kono.ceu.gtconsolidate.api.util;

import static gregtech.api.items.toolitem.ToolHelper.*;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockUtility;
import gregtech.api.util.GregFakePlayer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.ToolItems;

import kono.ceu.gtconsolidate.GTConsolidateConfig;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.ModuleArboriculture;

public class TreeFarmUtil {

    public static final int MAX_LOGS_PER_TICK = Math.max(1, GTConsolidateConfig.treeFarm.maxLogsPerTick);
    public static final int MAX_LEAVES_PER_TICK = Math.max(1, GTConsolidateConfig.treeFarm.maxLeavesPerTick);
    private static final boolean checkForestry = gregtech.api.util.Mods.ForestryArboriculture.isModLoaded();

    private static ItemStack axe = getAndSetToolData(ToolItems.AXE, Materials.Steel, 999999, 1, 1.0F, 0.1F);

    private static final Map<WorldServer, FakePlayer> FAKE_PLAYERS = new WeakHashMap<>();

    public static final List<ItemStack> saplings = new ArrayList<>();

    static {
        saplings.add(new ItemStack(Blocks.SAPLING, 1, 32767));
        saplings.add(new ItemStack(MetaBlocks.RUBBER_SAPLING));
        if (Mods.GregTechFoodOption.isModLoaded()) {
            saplings.add(Mods.GregTechFoodOption.getItem("gtfo_sapling_0", 1, 32767));
            saplings.add(Mods.GregTechFoodOption.getItem("gtfo_sapling_1", 1, 32767));
        }
        if (checkForestry) {
            saplings.add(ModuleArboriculture.getItems().sapling.getWildcard());
        }
    }

    public enum WorkPhase {
        IDLE,
        SCANNING_TREE,
        HARVESTING_TREE,
        HARVESTING_LEAVES
    }

    public static FakePlayer getFakePlayer(WorldServer world) {
        FakePlayer fakePlayer = FAKE_PLAYERS.get(world);
        if (fakePlayer == null) {
            fakePlayer = GregFakePlayer.get(world);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    public static void breakLogWithAxe(WorldServer server, BlockPos pos, List<ItemStack> drops, boolean hasSpace) {
        ItemStack axeStack = axe.copy();
        IBlockState state = server.getBlockState(pos);
        Block block = state.getBlock();

        FakePlayer lumberjack = getFakePlayer(server);
        TileEntity tileEntity = server.getTileEntity(pos);
        boolean canHarvest = state.getBlock().removedByPlayer(state, server, pos, lumberjack, true);
        if (canHarvest) {
            server.playEvent(null, 2001, pos, Block.getStateId(state));
            block.onPlayerDestroy(server, pos, state);
            BlockUtility.startCaptureDrops();
            if (hasSpace) {
                state.getBlock().harvestBlock(server, lumberjack, pos, state, tileEntity, axeStack);
                drops.addAll(BlockUtility.stopCaptureDrops());
            } else {
                double itemSpawnX = pos.getX() + 0.5;
                double itemSpawnY = pos.getY() + 0.5;
                double itemSpawnZ = pos.getZ() + 0.5;
                for (ItemStack overStack : BlockUtility.stopCaptureDrops()) {
                    EntityItem item = new EntityItem(server, itemSpawnX, itemSpawnY, itemSpawnZ, overStack);
                    server.spawnEntity(item);
                }
            }
        }
    }

    public static void breakLeaves(WorldServer server, BlockPos pos, List<ItemStack> drops, boolean hasSpace,
                                   int fortune, int tier) {
        IBlockState state = server.getBlockState(pos);
        Block block = state.getBlock();

        FakePlayer leafBreaker = getFakePlayer(server);

        boolean canHarvest = state.getBlock().removedByPlayer(state, server, pos, leafBreaker, true);
        NonNullList<ItemStack> blockDrops = NonNullList.create();

        if (canHarvest) {
            server.playEvent(null, 2001, pos, Block.getStateId(state));
            int x = tier * 100;
            while (true) {
                int r = server.rand.nextInt(10000) + 1;

                if (r > x) break;
                block.getDrops(blockDrops, server, pos, state, fortune);

            }
            block.getDrops(blockDrops, server, pos, state, fortune);
            block.onPlayerDestroy(server, pos, state);
            if (!blockDrops.isEmpty()) {
                if (hasSpace) {
                    drops.addAll(blockDrops);
                } else {
                    double itemSpawnX = pos.getX() + 0.5;
                    double itemSpawnY = pos.getY() + 0.5;
                    double itemSpawnZ = pos.getZ() + 0.5;
                    for (ItemStack overStack : blockDrops) {
                        EntityItem item = new EntityItem(server, itemSpawnX, itemSpawnY, itemSpawnZ, overStack);
                        server.spawnEntity(item);
                    }
                }
            }
        }
    }

    public static boolean placeSapling(WorldServer server, BlockPos pos, ItemStack saplingStack) {
        FakePlayer placer = getFakePlayer(server);
        ItemStack placedStack = saplingStack.copy();

        placer.inventory.currentItem = 0;
        placer.inventory.setInventorySlotContents(0, placedStack);
        placer.setHeldItem(EnumHand.MAIN_HAND, placedStack);

        BlockPos supportPos = pos.down();
        if (checkForestry) {
            ITree tree = TreeManager.treeRoot.getMember(placedStack);
            if (tree == null) {
                return false;
            }

            if (!tree.canStay(server, pos)) {
                return false;
            }
            return TreeManager.treeRoot.plantSapling(server, tree, placer.getGameProfile(), pos);
        } else {
            EnumActionResult result = placedStack.onItemUse(placer, server, supportPos, EnumHand.MAIN_HAND,
                    EnumFacing.UP, 0.5F, 1.0F, 0.5F);

            return result == EnumActionResult.SUCCESS;
        }
    }

    public static void collectNearbyLeaves(World world, BlockPos center, Deque<BlockPos> pendingLeaves,
                                           Set<BlockPos> queuedLeaves, int radius) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    mutablePos.setPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                    if (!world.isBlockLoaded(mutablePos)) {
                        continue;
                    }

                    BlockPos immutablePos = mutablePos.toImmutable();
                    if (queuedLeaves.contains(immutablePos)) {
                        continue;
                    }

                    if (isLeaf(world, immutablePos)) {
                        queuedLeaves.add(immutablePos);
                        pendingLeaves.add(immutablePos);
                    }
                }
            }
        }
    }

    public static boolean isSapling(ItemStack sapling) {
        for (ItemStack stack : saplings) {
            if (sapling.getItem() == stack.getItem()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLog(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isWood(world, pos);
    }

    public static boolean isLeaf(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isLeaves(state, world, pos);
    }

    public static boolean isSoil(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS;
    }

    public static boolean scanTreeStep(World world, BlockPos scanStart, Queue<BlockPos> scanQueue,
                                       Set<BlockPos> visitedLogs, int maxNodesPerTick) {
        if (scanStart == null || scanQueue.isEmpty()) {
            return true;
        }

        IBlockState startState = world.getBlockState(scanStart);
        Block startBlock = startState.getBlock();

        if (!startBlock.isWood(world, scanStart)) {
            return true;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int n = 0; n < maxNodesPerTick && !scanQueue.isEmpty(); n++) {
            BlockPos check = scanQueue.poll();
            if (check == null) {
                break;
            }

            if (!visitedLogs.add(check)) {
                continue;
            }

            for (int y = 0; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }

                        mutablePos.setPos(check.getX() + x, check.getY() + y, check.getZ() + z);

                        if (visitedLogs.contains(mutablePos)) {
                            continue;
                        }

                        if (!world.isBlockLoaded(mutablePos)) {
                            continue;
                        }

                        IBlockState neighborState = world.getBlockState(mutablePos);
                        Block neighborBlock = neighborState.getBlock();

                        if (neighborBlock == startBlock && neighborBlock.isWood(world, mutablePos)) {
                            scanQueue.add(mutablePos.toImmutable());
                        }
                    }
                }
            }
        }

        return scanQueue.isEmpty();
    }

    public static void sortLogsByY(Set<BlockPos> visitedLogs, Deque<BlockPos> pendingLogs) {
        visitedLogs.stream()
                .sorted(Comparator.comparingInt(BlockPos::getY))
                .forEachOrdered(pendingLogs::add);
    }
}
