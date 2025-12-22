package su.nightexpress.excellentcrates.crate.reward;

import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.dialog.DialogKey;
import su.nightexpress.excellentcrates.dialog.reward.RewardCreationDialog;

public class RewardDialogs {

    public static final DialogKey<RewardCreationDialog.Data> CREATION    = new DialogKey<>("reward_creation");
    public static final DialogKey<Crate>                     SORTING     = new DialogKey<>("reward_sorting");
}
