package su.nightexpress.excellentcrates.util.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrateRegistry<K extends CrateRegistryKey, V> {

    private final Map<K, V> registry;

    public CrateRegistry() {
        this.registry = new ConcurrentHashMap<>();
    }

    public void register(@NotNull K key, @NotNull V value) {
        this.registry.put(key, value);
    }

    @Nullable
    public V byKey(@NotNull K key) {
        return this.registry.get(key);
    }

    @NotNull
    public Collection<V> values() {
        return this.registry.values();
    }

    public void clear() {
        this.registry.clear();
    }
}
