package su.nightexpress.excellentcrates.dialog;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Dialog<T> implements LangContainer {

    private static final Map<UUID, Runnable> CALLBACKS = new HashMap<>();

    @NotNull
    public abstract WrappedDialog create(@NotNull Player player, @NotNull T source);

    public void show(@NotNull Player player, @NotNull T source, @Nullable Runnable callback) {
        if (callback != null) {
            CALLBACKS.put(player.getUniqueId(), callback);
        }
        Dialogs.showDialog(player, this.create(player, source));
    }

    public static void handleCallback(@NotNull Player player) {
        Runnable callback = CALLBACKS.remove(player.getUniqueId());
        if (callback != null) {
            callback.run();
        }
    }

    @NotNull
    protected static String title(@NotNull String prefix, @NotNull String title) {
        return TagWrappers.YELLOW.and(TagWrappers.BOLD).wrap(prefix.toUpperCase()) + TagWrappers.DARK_GRAY.wrap( " » ") + TagWrappers.WHITE.wrap(title);
    }
}
