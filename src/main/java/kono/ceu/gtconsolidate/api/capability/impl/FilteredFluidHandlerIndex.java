package kono.ceu.gtconsolidate.api.capability.impl;

import java.util.Map;
import java.util.WeakHashMap;

import gregtech.api.capability.impl.FilteredFluidHandler;

public class FilteredFluidHandlerIndex {

    private static final Map<FilteredFluidHandler, Integer> INDEX_MAP = new WeakHashMap<>();

    private static int nextId = 0;

    private FilteredFluidHandlerIndex() {}

    public static int register(FilteredFluidHandler handler) {
        int id = nextId++;
        INDEX_MAP.put(handler, id);
        return id;
    }

    public static int getId(FilteredFluidHandler handler) {
        return INDEX_MAP.getOrDefault(handler, -1);
    }
}
