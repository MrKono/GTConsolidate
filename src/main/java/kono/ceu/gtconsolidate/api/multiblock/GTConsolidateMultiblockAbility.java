package kono.ceu.gtconsolidate.api.multiblock;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import kono.ceu.gtconsolidate.api.capability.impl.GTConsolidateDualHandler;
import kono.ceu.gtconsolidate.api.capability.impl.IGTConsolidateDualHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;

@SuppressWarnings("InstantiationOfUtilityClass")
public class GTConsolidateMultiblockAbility {

    public static final MultiblockAbility<IFluidTank> IMPORT_DUAL = new MultiblockAbility<>("dual_import_hatch");
    public static final MultiblockAbility<IFluidTank> EXPORT_DUAL = new MultiblockAbility<>("dual_export_hatch");

    private GTConsolidateMultiblockAbility() {}
}
