package memory;

import jakarta.annotation.PostConstruct;

public class MemoryFinder {

    public Memory get() {
        final long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        final long max = Runtime.getRuntime().maxMemory();
        return new Memory(used, max);
    }

    @PostConstruct
    public void init() {
        System.out.println("MemoryFinder bean created");
    }
}
