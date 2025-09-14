package kono.ceu.gtconsolidate.common.metatileentities.multi.primitive;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.RecipeProgressWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class MetaTileEntityMegaPrimitiveBlastFurnace extends RecipeMapPrimitiveMultiblockController {

    public MetaTileEntityMegaPrimitiveBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.MEGA_PRIMITIVE_BLAST_FURNACE_RECIPE);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMegaPrimitiveBlastFurnace(metaTileEntityId);
    }

    @Override
    protected void initializeAbilities() {
        this.importItems = new NotifiableItemStackHandler(this, 18, this,
                false);
        this.exportItems = new NotifiableItemStackHandler(this, 18, this,
                true);

        this.itemInventory = new ItemHandlerProxy(this.importItems, this.exportItems);
        this.fluidInventory = new FluidHandlerProxy(this.importFluids, this.exportFluids);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("##XXXXXXXXX##", "##XXXXXXXXX##", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############")
                .aisle("#XXXXXXXXXXX#", "#XXXXXXXXXXX#", "###F#####F###", "###F#####F###", "###FFFFFFF###",
                        "#############", "#############", "#############", "#############", "#############",
                        "####FFFFF####", "#############", "#############", "#############", "#############",
                        "#############", "#############")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "##F#######F##", "##F#######F##", "##FFFHHHFFF##",
                        "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##FFFHHHFFF##", "#############", "#############", "#############", "#############",
                        "#############", "###TTTTTTT###")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "#F####P####F#", "#F####P####F#", "#FFHHHPHHHFF#",
                        "######P######", "######P######", "######P######", "######P######", "######P######",
                        "##FHHHPHHHF##", "######P######", "######P######", "######P######", "######P######",
                        "######P######", "##TTTTPTTTT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "####BBPBB####", "####TTTTT####", "#FFHHHHHHHFF#",
                        "####BTTTB####", "####CCCCC####", "####CCCCC####", "####CCCCC####", "####BTTTB####",
                        "#FFHHHHHHHFF#", "####BTTTB####", "####CCCCC####", "####CCCCC####", "####CCCCC####",
                        "####BTTTB####", "##TTTTPTTTT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "####BAAAB####", "####TAAAT####", "#FHHHAAAHHHF#",
                        "####TAAAT####", "####CAAAC####", "####CAAAC####", "####CAAAC####", "####TAAAT####",
                        "#FHHHAAAHHHF#", "####TAAAT####", "####CAAAC####", "####CAAAC####", "####CAAAC####",
                        "####TAAAT####", "##TTTTPTTTT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "###PPAAAPP###", "###PTAAATP###", "#FHPHAAAHPHF#",
                        "###PTAAATP###", "###PCAAACP###", "###PCAAACP###", "###PCAAACP###", "###PTAAATP###",
                        "#FHPHAAAHPHF#", "###PTAAATP###", "###PCAAACP###", "###PCAAACP###", "###PCAAACP###",
                        "###PTAAATP###", "##TPPPTPPPT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "####BAAAB####", "####TAAAT####", "#FHHHAAAHHHF#",
                        "####TAAAT####", "####CAAAC####", "####CAAAC####", "####CAAAC####", "####TAAAT####",
                        "#FHHHAAAHHHF#", "####TAAAT####", "####CAAAC####", "####CAAAC####", "####CAAAC####",
                        "####TAAAT####", "##TTTTPTTTT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "####BBPBB####", "####TTTTT####", "#FFHHHHHHHFF#",
                        "####BTTTB####", "####CCCCC####", "####CCCCC####", "####CCCCC####", "####BTTTB####",
                        "#FFHHHHHHHFF#", "####BTTTB####", "####CCCCC####", "####CCCCC####", "####CCCCC####",
                        "####BTTTB####", "##TTTTPTTTT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "#F####P####F#", "#F####P####F#", "#FFHHHPHHHFF#",
                        "######P######", "######P######", "######P######", "######P######", "######P######",
                        "##FHHHPHHHF##", "######P######", "######P######", "######P######", "######P######",
                        "######P######", "##TTTTPTTTT##")
                .aisle("XXXXXXXXXXXXX", "XXXXXXXXXXXXX", "##F#######F##", "##F#######F##", "##FFFHHHFFF##",
                        "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##FFFHHHFFF##", "#############", "#############", "#############", "#############",
                        "#############", "###TTTTTTT###")
                .aisle("#XXXXXXXXXXX#", "#XXXXXXXXXXX#", "###F#####F###", "###F#####F###", "###FFFFFFF###",
                        "#############", "#############", "#############", "#############", "#############",
                        "####FFFFF####", "#############", "#############", "#############", "#############",
                        "#############", "#############")
                .aisle("##XXXXXXXXX##", "##XXXXSXXXX##", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()))
                .where('F', frames(ConfigHolder.machines.steelSteamMultiblocks ? Materials.Steel : Materials.Bronze))
                .where('H', states(getCasingState()))
                .where('P', states(getPipeState()))
                .where('B', states(getFireboxState()))
                .where('T', states(getCasingState2()))
                .where('C', blocks(Blocks.MAGMA))
                .where('A', air())
                .where('#', any())
                .build();
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.METAL_CASING.getState(ConfigHolder.machines.steelSteamMultiblocks ?
                BlockMetalCasing.MetalCasingType.STEEL_SOLID : BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    private static IBlockState getFireboxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(ConfigHolder.machines.steelSteamMultiblocks ?
                BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX :
                BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX);
    }

    private static IBlockState getPipeState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE) :
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.PRIMITIVE_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        return ModularUI.builder(GuiTextures.PRIMITIVE_BACKGROUND, 176, 166)
                .shouldColor(false)
                .widget(new LabelWidget(5, 5, getMetaFullName()))
                .widget(new SlotWidget(importItems, 0, 52, 20, true, true)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_INGOT_OVERLAY))
                .widget(new SlotWidget(importItems, 1, 52, 38, true, true)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_DUST_OVERLAY))
                .widget(new SlotWidget(importItems, 2, 52, 56, true, true)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_FURNACE_OVERLAY))
                .widget(new RecipeProgressWidget(recipeMapWorkable::getProgressPercent, 77, 39, 20, 15,
                        GuiTextures.PRIMITIVE_BLAST_FURNACE_PROGRESS_BAR, ProgressWidget.MoveType.HORIZONTAL,
                        RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES))
                .widget(new SlotWidget(exportItems, 0, 104, 29, true, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_INGOT_OVERLAY))
                .widget(new SlotWidget(exportItems, 1, 122, 29, true, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_INGOT_OVERLAY))
                .widget(new SlotWidget(exportItems, 2, 140, 29, true, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_INGOT_OVERLAY))
                .widget(new SlotWidget(exportItems, 3, 104, 47, true, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_DUST_OVERLAY))
                .widget(new SlotWidget(exportItems, 4, 122, 47, true, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_DUST_OVERLAY))
                .widget(new SlotWidget(exportItems, 5, 140, 47, true, false)
                        .setBackgroundTexture(GuiTextures.PRIMITIVE_SLOT, GuiTextures.PRIMITIVE_DUST_OVERLAY))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.PRIMITIVE_SLOT, 0);
    }
}
