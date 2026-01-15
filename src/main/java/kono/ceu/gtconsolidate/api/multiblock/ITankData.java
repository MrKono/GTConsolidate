package kono.ceu.gtconsolidate.api.multiblock;

import org.jetbrains.annotations.NotNull;

public interface ITankData {

    int getTier();

    long getCapacity();

    @NotNull
    String getTankName();
}
