package com.cyanogen.experienceobelisk.registries;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExperienceObelisk.MOD_ID);

    public static final RegistryObject<SoundEvent> ENLIGHTENED_AMULET_ACTIVATE = registerSound("enlightened_amulet_activate", 4);
    public static final RegistryObject<SoundEvent> ENLIGHTENED_AMULET_DEACTIVATE = registerSound("enlightened_amulet_deactivate", 4);
    public static final RegistryObject<SoundEvent> METAMORPHER_BUSY1 = registerSound("metamorpher_busy1", 3);
    public static final RegistryObject<SoundEvent> METAMORPHER_BUSY2 = registerSound("metamorpher_busy2", 3);

    public static RegistryObject<SoundEvent> registerSound(String soundName, float range){
        return SOUNDS.register(soundName, () -> new SoundEvent(new ResourceLocation(ExperienceObelisk.MOD_ID, soundName)){
            @Override
            public float getRange(float f) {
                return range;
            }
        });
    }

    public static void register(IEventBus eventBus){
        SOUNDS.register(eventBus);
    }
}
