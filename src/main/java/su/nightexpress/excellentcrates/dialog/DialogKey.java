package su.nightexpress.excellentcrates.dialog;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.util.registry.CrateRegistryKey;

public class DialogKey<T> extends CrateRegistryKey {

    public DialogKey(@NotNull String id) {
        super(id);
    }
}
