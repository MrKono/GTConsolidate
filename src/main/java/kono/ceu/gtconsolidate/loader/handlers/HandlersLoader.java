package kono.ceu.gtconsolidate.loader.handlers;

public class HandlersLoader {

    public static void init() {
        CoALoaders.register();
        CircuitFactoryLoader.register();
    }

    public static void low() {
        AbsoluteFreezerLoader.coolingABS();
    }
}
