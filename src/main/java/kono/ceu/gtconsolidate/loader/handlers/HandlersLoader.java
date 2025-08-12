package kono.ceu.gtconsolidate.loader.handlers;

public class HandlersLoader {

    public static void init() {
        AbsoluteFreezerLoader.register();
        CoALoaders.register();
        CircuitFactoryLoader.register();
    }
}
