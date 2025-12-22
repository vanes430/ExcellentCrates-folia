package su.nightexpress.excellentcrates.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.item.ItemTypes;
import su.nightexpress.excellentcrates.api.item.ItemProvider;

public class ItemHelper {

    @NotNull
    public static ItemStack toItemStack(@NotNull ItemProvider provider) {
        return provider.getItemStack();
    }

    @NotNull
    public static ItemProvider adapt(@NotNull ItemStack itemStack) {
        return ItemTypes.fromItem(itemStack);
    }

    @NotNull
    public static ItemProvider adapt(@NotNull ItemStack itemStack, boolean allowCustoms) {
        return allowCustoms ? ItemTypes.fromItem(itemStack) : ItemTypes.vanilla(itemStack);
    }

    public static boolean isCustom(@NotNull ItemStack itemStack) {
        return ItemTypes.isCustom(itemStack);
    }
}
