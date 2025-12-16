package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import kono.ceu.gtconsolidate.client.GTConsolidateTextures;
import kono.ceu.gtconsolidate.common.blocks.BlockGearBoxCasing;
import kono.ceu.gtconsolidate.common.blocks.BlockMultiblockCasing;
import kono.ceu.gtconsolidate.common.blocks.BlockPipeCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class MetaTileEntityOreFactory extends GCYMRecipeMapMultiblockController {

    private final boolean isParallelized;

    public MetaTileEntityOreFactory(ResourceLocation metaTileEntityId, boolean isParallelized) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES);
        this.isParallelized = isParallelized;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityOreFactory(metaTileEntityId, isParallelized);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        FactoryBlockPattern builder = FactoryBlockPattern.start()
                .aisle("AAAAAAA#####", "AGGGGGA#####", "AGGGGGA#####", "AGGGGGA#####", "AGGGGGA#####", "AGGGGGA#####",
                        "AAAAAAA#####", "############", "############", "############", "############", "############",
                        "############")
                .aisle("AAAAAAABBBBB", "GP$$$PGACCCA", "GP$$$PGACCCA", "GP$$$PGACCCA", "GP$$$PGACCCA", "GP$$$PGACCCA",
                        "AAAAAAAACCCA", "########DDD#", "########DDD#", "########DDD#", "########DDD#", "########DDD#",
                        "############")
                .aisle("AAAAAAABBBBB", "G$P$P$GC$$$C", "G$P$P$GC$$$C", "G$P$P$GC$$$C", "G$P$P$GC$$$C", "G$P$P$GC$$$C",
                        "AAAAAAAC$$$C", "#######D$$$D", "#######D$$$D", "#######D$$$D", "#######D$$$D", "#######D$$$D",
                        "########EEE#")
                .aisle("AAAAAAABBBBB", "G$$H$$GC$V$C", "G$$H$$GC$H$C", "G$$H$$GC$H$C", "G$$H$$GC$H$C", "G$$H$$GC$H$C",
                        "AAAAAAAC$H$C", "#######D$H$D", "#######D$H$D", "#######D$H$D", "#######D$H$D", "#######D$H$D",
                        "########EME#")
                .aisle("AAAAAAABBBBB", "G$P$P$GC$$$C", "G$P$P$GC$$$C", "G$P$P$GC$$$C", "G$P$P$GC$$$C", "G$P$P$GC$$$C",
                        "AAAAAAAC$$$C", "#######D$$$D", "#######D$$$D", "#######D$$$D", "#######D$$$D", "#######D$$$D",
                        "########EEE#")
                .aisle("AAAAAAABBBBB", "GP$$$PGACSCA", "GP$$$PGACCCA", "GP$$$PGACCCA", "GP$$$PGACCCA", "GP$$$PGACCCA",
                        "AAAAAAAACCCA", "########DDD#", "########DDD#", "########DDD#", "########DDD#", "########DDD#",
                        "############")
                .aisle("AAAAAAA#####", "AGGGGGA#####", "AGGGGGA#####", "AGGGGGA#####", "AGGGGGA#####", "AGGGGGA#####",
                        "AAAAAAA#####", "############", "############", "############", "############", "############",
                        "############")
                .where('A', states(getCasingState()))
                .where('B', states(getCasingState())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(3))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(5, 1)))
                .where('D', states(getCasingState2())
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(10, 1)))
                .where('E', states(getCasingState2()))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.LAMINATED_GLASS)))
                .where('H', states(getGearBoxState()))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('P', states(getPipeState()))
                .where('S', selfPredicate())
                .where('#', any())
                .where('$', air());

        if (isParallelized) {
            builder.where('C', states(getCasingState2())
                    .or(autoAbilities(true, false))
                    .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(20, 1))
                    .or(abilities(GCYMMultiblockAbility.PARALLEL_HATCH).setMaxGlobalLimited(1)))
                    .where('V',
                            tieredCasing().or(states(getGearBoxState())));
        } else {
            builder.where('C', states(getCasingState2())
                    .or(autoAbilities(true, false))
                    .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(20, 1)))
                    .where('V', states(getGearBoxState()));

        }
        return builder.build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        if (iMultiblockPart instanceof IMultiblockAbilityPart) {
            MultiblockAbility<?> ability = ((IMultiblockAbilityPart<?>) iMultiblockPart).getAbility();
            if (ability == MultiblockAbility.INPUT_ENERGY || ability == MultiblockAbility.IMPORT_FLUIDS) {
                return isParallelized ? GTConsolidateTextures.AMERICIUM_PLATED : GTConsolidateTextures.IRIDIUM_PLATED;
            }
        }
        return isParallelized ? GTConsolidateTextures.IRIDIUM_PLATED : Textures.CLEAN_STAINLESS_STEEL_CASING;
    }

    protected IBlockState getCasingState() {
        return isParallelized ?
                GTConsolidateMetaBlocks.MULTIBLOCK_CASING
                        .getState(BlockMultiblockCasing.MultiblockCasingType.AMERICIUM_PLATED) :
                GTConsolidateMetaBlocks.MULTIBLOCK_CASING
                        .getState(BlockMultiblockCasing.MultiblockCasingType.IRIDIUM_PLATED);
    }

    protected IBlockState getCasingState2() {
        return isParallelized ?
                GTConsolidateMetaBlocks.MULTIBLOCK_CASING
                        .getState(BlockMultiblockCasing.MultiblockCasingType.IRIDIUM_PLATED) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN);
    }

    protected IBlockState getGearBoxState() {
        return isParallelized ?
                GTConsolidateMetaBlocks.GEARBOX_CASING.getState(BlockGearBoxCasing.CasingType.AMERICIUM) :
                GTConsolidateMetaBlocks.GEARBOX_CASING.getState(BlockGearBoxCasing.CasingType.IRIDIUM);
    }

    protected IBlockState getPipeState() {
        return isParallelized ? GTConsolidateMetaBlocks.PIPE_CASING.getState(BlockPipeCasing.CasingType.AMERICIUM) :
                GTConsolidateMetaBlocks.PIPE_CASING.getState(BlockPipeCasing.CasingType.IRIDIUM);
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip1"));
        tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip2"));
        if (TooltipHelper.isCtrlDown()) {
            tooltip.add("");
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process1"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process2"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process3"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process4"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process5"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process6"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process7"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process8"));
            if (ConfigHolder.recipes.generateLowQualityGems) {
                tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process9"));
            }
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process10"));
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.process11"));
            tooltip.add("");
        } else {
            tooltip.add(I18n.format("gtconsolidate.machine.ore_factory.tooltip.hold_ctrl"));
        }
    }
}
