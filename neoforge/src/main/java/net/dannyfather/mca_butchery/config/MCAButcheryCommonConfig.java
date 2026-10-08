package net.dannyfather.mca_butchery.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class MCAButcheryCommonConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> VANILLA_VILLAGER_CORPSE;
    public static final ModConfigSpec.ConfigValue<Boolean> PLAYER_CORPSE;
    public static final ModConfigSpec.ConfigValue<Boolean> GREY_EYES;
    public static final ModConfigSpec.ConfigValue<Boolean> CLOSED_EYES;



    static {
        BUILDER.push("Configs for MCA Descendants");

        VANILLA_VILLAGER_CORPSE = BUILDER.comment("MCA: Butchery automatically sets dropping villager/player corpses to false in Butchery's config for compatibility, enable/disable drops here")
                .define("Drop Vanilla Villagers", true);
        PLAYER_CORPSE = BUILDER.define("Drop Default Player Corpse",false);


        GREY_EYES = BUILDER.comment("Desaturates eyes of MCA Villager corpses").define("Fresh Corpse Grey Eyes",false);
        CLOSED_EYES = BUILDER.define("Closed Eyes", false);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
