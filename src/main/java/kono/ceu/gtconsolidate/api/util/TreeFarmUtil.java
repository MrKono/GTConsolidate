package kono.ceu.gtconsolidate.api.util;

import static gregtech.api.items.toolitem.ToolHelper.*;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.mojang.authlib.GameProfile;

import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.ToolItems;

public class TreeFarmUtil {

    private static final GameProfile TREE_FARM_PROFILE = new GameProfile(
            UUID.fromString("12345678-9876-5432-1012-345678909876"), "[Lumberjack]");
    private static ItemStack axe = getAndSetToolData(ToolItems.AXE, Materials.Steel, 999999, 1, 1.0F, 0.1F);

    private static final Map<WorldServer, FakePlayer> FAKE_PLAYERS = new HashMap<>();

    public static final List<ItemStack> saplings = new ArrayList<>();

    static {
        saplings.add(new ItemStack(Blocks.SAPLING, 1, 32767));
        saplings.add(new ItemStack(MetaBlocks.RUBBER_SAPLING));
        if (Mods.GregTechFoodOption.isModLoaded()) {
            saplings.add(Mods.GregTechFoodOption.getItem("gtfo_sapling_0", 1, 32767));
            saplings.add(Mods.GregTechFoodOption.getItem("gtfo_sapling_1", 1, 32767));
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
            fakePlayer = FakePlayerFactory.get(world, TREE_FARM_PROFILE);
            FAKE_PLAYERS.put(world, fakePlayer);
        }

        return fakePlayer;
    }

    public static void breakLogWithAxe(WorldServer server, BlockPos pos, List<ItemStack> drops, boolean dropped) {
        axe = axe.copy();
        IBlockState state = server.getBlockState(pos);
        Block block = state.getBlock();

        FakePlayer lumberjack = getFakePlayer(server);
        lumberjack.inventory.clear();
        lumberjack.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        lumberjack.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
        lumberjack.motionX = lumberjack.motionY = lumberjack.motionZ = 0.0;
        lumberjack.fallDistance = 0.0F;
        lumberjack.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        lumberjack.setHeldItem(EnumHand.MAIN_HAND, axe);
        lumberjack.interactionManager.setGameType(GameType.SURVIVAL);

        List<ItemStack> generatedDrops = new ArrayList<>();
        NonNullList<ItemStack> blockDrops = NonNullList.create();
        block.getDrops(blockDrops, server, pos, state, 0);

        for (ItemStack drop : blockDrops) {
            if (!drop.isEmpty()) {
                generatedDrops.add(drop.copy());
            }
        }

        TileEntity tileEntity = server.getTileEntity(pos);
        boolean canHarvest = block.canHarvestBlock(server, pos, lumberjack);
        boolean removed = block.removedByPlayer(state, server, pos, lumberjack, canHarvest);

        if (removed) {
            block.onPlayerDestroy(server, pos, state);

            if (dropped && canHarvest) {
                block.harvestBlock(server, lumberjack, pos, state, tileEntity, axe);

            } else {
                for (ItemStack drop : generatedDrops) {
                    drops.add(drop.copy());
                }
            }

            axe.onBlockDestroyed(server, state, pos, lumberjack);
        }
        lumberjack.getHeldItemMainhand().copy();
    }

    public static void breakLeaves(WorldServer server, BlockPos pos, List<ItemStack> drops, boolean dropped,
                                   int fortune) {
        axe = axe.copy();
        if (fortune > 0) axe.addEnchantment(Enchantments.FORTUNE, fortune);

        IBlockState state = server.getBlockState(pos);
        Block block = state.getBlock();

        FakePlayer leafBraker = getFakePlayer(server);
        leafBraker.inventory.clear();
        leafBraker.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        leafBraker.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
        leafBraker.motionX = leafBraker.motionY = leafBraker.motionZ = 0.0;
        leafBraker.fallDistance = 0.0F;
        leafBraker.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        leafBraker.setHeldItem(EnumHand.MAIN_HAND, axe);
        leafBraker.interactionManager.setGameType(GameType.SURVIVAL);

        List<ItemStack> generatedDrops = new ArrayList<>();
        NonNullList<ItemStack> blockDrops = NonNullList.create();
        block.getDrops(blockDrops, server, pos, state, 0);

        for (ItemStack drop : blockDrops) {
            if (!drop.isEmpty()) {
                generatedDrops.add(drop.copy());
            }
        }

        TileEntity tileEntity = server.getTileEntity(pos);
        boolean canHarvest = block.canHarvestBlock(server, pos, leafBraker);
        boolean removed = block.removedByPlayer(state, server, pos, leafBraker, canHarvest);

        if (removed) {
            block.onPlayerDestroy(server, pos, state);

            if (dropped && canHarvest) {
                block.harvestBlock(server, leafBraker, pos, state, tileEntity, axe);

            } else {
                for (ItemStack drop : generatedDrops) {
                    drops.add(drop.copy());
                }
            }

            axe.onBlockDestroyed(server, state, pos, leafBraker);
        }
        leafBraker.getHeldItemMainhand().copy();
    }

    public static void placeSapling(WorldServer server, BlockPos pos, ItemStack saplingStack) {
        if (saplingStack.isEmpty()) {
            return;
        }

        FakePlayer placer = getFakePlayer(server);
        ItemStack placeSapling = saplingStack.copy();

        placer.inventory.clear();
        placer.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        placer.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
        placer.motionX = placer.motionY = placer.motionZ = 0.0;
        placer.fallDistance = 0.0F;
        placer.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        placer.setHeldItem(EnumHand.MAIN_HAND, placeSapling);

        BlockPos supportPos = pos.down();

        placeSapling.onItemUse(placer, server, supportPos, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 1.0F, 0.5F);

        placer.getHeldItem(EnumHand.MAIN_HAND).copy();
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
