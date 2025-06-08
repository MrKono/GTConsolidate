package kono.ceu.gtconsolidate.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import zone.rong.mixinbooter.ILateMixinLoader;

public class GTConsolidateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Lists.newArrayList("mixins.gtconsolidate.json");
    }
}
