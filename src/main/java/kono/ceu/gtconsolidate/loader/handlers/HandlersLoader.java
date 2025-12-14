package kono.ceu.gtconsolidate.loader.handlers;

public class HandlersLoader {

    public static void init() {
        CoALoaders.register();
        CircuitFactoryLoader.register();
        OreFactoryHandler.init();
    }

    public static void low() {
        AbsoluteFreezerLoader.coolingABS();
    }
}
