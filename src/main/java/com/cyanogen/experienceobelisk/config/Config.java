package com.cyanogen.experienceobelisk.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static class Common{

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedFluids;
        public final ForgeConfigSpec.ConfigValue<Integer> capacity;
        public final ForgeConfigSpec.ConfigValue<Double> amuletRange;
        public final ForgeConfigSpec.ConfigValue<Double> bindingRange;
        public final ForgeConfigSpec.ConfigValue<Boolean> formatting;

        public List<String> defaultAllowedFluids = new ArrayList<>();
        public int defaultCapacity = 100000000;
        public double defaultAmuletRange = 8.0;
        public double defaultBindingRange = 48.0;
        public boolean defaultFormatting = true;

        public Common(ForgeConfigSpec.Builder builder){

            defaultAllowedFluids.add("mob_grinding_utils:fluid_xp");
            defaultAllowedFluids.add("cofh_core:experience");
            defaultAllowedFluids.add("industrialforegoing:essence");
            defaultAllowedFluids.add("sophisticatedcore:xp_still");
            defaultAllowedFluids.add("enderio:xp_juice");

            builder.push("Allowed Experience Fluids");
            this.allowedFluids = builder.comment("Add IDs of fluids you want the obelisk to support here in the form mod_id:fluid_name. Fluids have to be tagged forge:experience.")
                    .define("AllowedFluids", defaultAllowedFluids);
            builder.pop();

            builder.push("Experience Obelisk Capacity");
            this.capacity = builder.comment("The fluid capacity of the obelisk in mB. Default = 100000000, which is ~1072 levels' worth. Ensure that the new value is divisible by 20.")
                    .comment("Warning: setting this value above the default may lead to unintended loss or gain of XP.")
                    .defineInRange("Capacity", defaultCapacity, 1000, 2147483640);
            builder.pop();

            builder.push("Enlightened Amulet Range");
            this.amuletRange = builder.comment("The range of the enlightened amulet in blocks. Accepts decimals. Default = 8.0.")
                    .defineInRange("Range", defaultAmuletRange, 1, 16.0);
            builder.pop();

            builder.push("Staff of Attunement Range");
            this.bindingRange = builder.comment("The binding range of the Staff of Attunement in blocks. Accepts decimals. Default = 48.0.")
                    .comment("Lower this if you experience issues with bound blocks not working correctly.")
                    .defineInRange("Range", defaultBindingRange, 4, 100.0);
            builder.pop();

            builder.push("Enable Name Formatting Anvil Recipes");
            this.formatting = builder.comment("Whether custom recipes that allow for the changing of item name color & formatting are enabled")
                    .define("Formatting", defaultFormatting);
            builder.pop();

        }

    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
