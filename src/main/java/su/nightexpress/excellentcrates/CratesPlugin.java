package su.nightexpress.excellentcrates;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.command.basic.BaseCommands;
import su.nightexpress.excellentcrates.config.*;
import su.nightexpress.excellentcrates.crate.CrateManager;
import su.nightexpress.excellentcrates.crate.effect.EffectRegistry;
import su.nightexpress.excellentcrates.data.DataHandler;
import su.nightexpress.excellentcrates.data.DataManager;
import su.nightexpress.excellentcrates.editor.EditorManager;
import su.nightexpress.excellentcrates.hologram.HologramManager;
import su.nightexpress.excellentcrates.hooks.HookId;
import su.nightexpress.excellentcrates.hooks.impl.PlaceholderHook;
import su.nightexpress.excellentcrates.key.KeyManager;
import su.nightexpress.excellentcrates.opening.OpeningManager;
import su.nightexpress.excellentcrates.opening.ProviderRegistry;
import su.nightexpress.excellentcrates.crate.reward.RewardDialogs;
import su.nightexpress.excellentcrates.dialog.DialogRegistry;
import su.nightexpress.excellentcrates.dialog.reward.RewardCreationDialog;
import su.nightexpress.excellentcrates.dialog.reward.RewardSortingDialog;
import su.nightexpress.excellentcrates.user.UserManager;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.lib.folialib.FoliaLib;
import su.nightexpress.nightcore.util.Plugins;

import java.util.Optional;

public class CratesPlugin extends NightPlugin implements ImprovedCommands {

    private DataHandler dataHandler;
    private DataManager dataManager;
    private UserManager userManager;

    private HologramManager hologramManager;
    private OpeningManager  openingManager;
    private KeyManager      keyManager;
    private CrateManager    crateManager;
    //private MenuManager     menuManager;
    private EditorManager   editorManager;

    private DialogRegistry  dialogRegistry;

    private CrateLogger     crateLogger;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Crates", new String[]{"crates", "ecrates", "excellentcrates", "crate", "case", "cases"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    public void runFoliaTask(@NotNull Runnable runnable) {
        this.getFoliaLib().getImpl().runNextTick(task -> runnable.run());
    }

    public void runTaskAtPlayer(@NotNull org.bukkit.entity.Player player, @NotNull Runnable runnable) {
        this.getFoliaLib().getScheduler().runAtEntity(player, task -> runnable.run());
    }

    public void runFoliaTaskAsync(@NotNull Runnable runnable) {
        this.getFoliaLib().getImpl().runAsync(task -> runnable.run());
    }

    public void runFoliaTaskLater(@NotNull Runnable runnable, long delay) {
        this.getFoliaLib().getImpl().runLater(task -> runnable.run(), delay * 50L, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void runFoliaTaskTimer(@NotNull Runnable runnable, long delay, long period) {
        this.getFoliaLib().getImpl().runTimer(task -> runnable.run(), delay * 50L, period * 50L, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    @Override
    public void enable() {
        this.loadEngine();

        if (!Plugins.hasEconomyBridge()) {
            this.warn("*".repeat(25));
            this.warn("You don't have " + HookId.ECONOMY_BRIDGE + " installed.");
            this.warn("The following features will be unavailable:");
            this.warn("- Crate open cost.");
            this.warn("- Custom item plugin support.");
            this.warn("*".repeat(25));
        }

        this.getLangManager().loadEntries(EditorLang.class);
        this.loadCommands();

        this.crateLogger = new CrateLogger(this);

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.dataManager = new DataManager(this);
        this.dataManager.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.openingManager = new OpeningManager(this);
        this.openingManager.setup();

        this.keyManager = new KeyManager(this);
        this.keyManager.setup();

        this.crateManager = new CrateManager(this);
        this.crateManager.setup();

        if (Config.HOLOGRAMS_ENABLED.get()) {
            this.hologramManager = new HologramManager(this);
            this.hologramManager.setup();
        }

        this.dialogRegistry = new DialogRegistry(this);
        this.dialogRegistry.register(RewardDialogs.CREATION, () -> new RewardCreationDialog(this));
        this.dialogRegistry.register(RewardDialogs.SORTING, RewardSortingDialog::new);

//        this.menuManager = new MenuManager(this);
//        this.menuManager.setup();

        this.editorManager = new EditorManager(this);
        this.editorManager.setup();

        this.dataHandler.updateRewardLimits();

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
    }

    @NotNull
    public DialogRegistry getDialogRegistry() {
        return this.dialogRegistry;
    }

    @Override
    public void disable() {
        if (this.editorManager != null) this.editorManager.shutdown();
        if (this.openingManager != null) this.openingManager.shutdown();
        if (this.keyManager != null) this.keyManager.shutdown();
        if (this.crateManager != null) this.crateManager.shutdown();
        //if (this.menuManager != null) this.menuManager.shutdown();
        if (this.hologramManager != null) this.hologramManager.shutdown();
        if (this.userManager != null) this.userManager.shutdown();
        if (this.dataManager != null) this.dataManager.shutdown();
        if (this.dataHandler != null) this.dataHandler.shutdown();

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }

        EffectRegistry.clear();
        Keys.clear();
        CratesAPI.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ProviderRegistry.clear(); // Clear only on server's shutdown (remember: PlugMan is a cancer), not on reload.
    }

    private void loadEngine() {
        CratesAPI.load(this);
        Keys.load(this);
        EffectRegistry.load();
        ProviderRegistry.load();
    }

    private void loadCommands() {
        BaseCommands.load(this);
    }

    public boolean hasHolograms() {
        return this.hologramManager != null && this.hologramManager.hasHandler();
    }

    @NotNull
    public Optional<HologramManager> getHologramManager() {
        return Optional.ofNullable(this.hologramManager);
    }

    @NotNull
    public CrateLogger getCrateLogger() {
        return this.crateLogger;
    }

    @NotNull
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    @NotNull
    public DataManager getDataManager() {
        return this.dataManager;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }

    @NotNull
    public OpeningManager getOpeningManager() {
        return this.openingManager;
    }

    @NotNull
    public EditorManager getEditorManager() {
        return this.editorManager;
    }

    @NotNull
    public KeyManager getKeyManager() {
        return this.keyManager;
    }

    @NotNull
    public CrateManager getCrateManager() {
        return this.crateManager;
    }

//    @NotNull
//    public MenuManager getMenuManager() {
//        return this.menuManager;
//    }
}
