package kono.ceu.gtconsolidate.api.multiblock;

import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;

import java.util.List;

public interface IMultiblockDualAbilityPart<T, U> extends IMultiblockPart {

    MultiblockAbility<T> getAbility1();
    MultiblockAbility<U> getAbility2();

    void registerAbilities1(List<T> abilityList);
    void registerAbilities2(List<U> abilityList);
}
