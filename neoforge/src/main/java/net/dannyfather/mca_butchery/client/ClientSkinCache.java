package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientSkinCache {
    private static final Map<UUID, ResourceLocation> SKINS = new HashMap<>();
    private static final Map<UUID, NativeImage> SKIN_TEXTURES = new HashMap<>();
    private static final Set<UUID> REQUESTED = new HashSet<>();
    public static void put (UUID uuid, ResourceLocation texture, NativeImage nativeImage) {
        SKINS.put(uuid,texture);
        SKIN_TEXTURES.put(uuid,nativeImage);
    }

    public static ResourceLocation get(UUID uuid) {
        return SKINS.get(uuid);
    }


    public static NativeImage getImage(UUID uuid) {
        return SKIN_TEXTURES.get(uuid);
    }

    public static boolean contains(UUID uuid) {
        return SKINS.containsKey(uuid);
    }

    public static boolean hasRequested(UUID uuid) {
        return REQUESTED.contains(uuid);
    }

    public static void markRequested(UUID uuid) {
        REQUESTED.add(uuid);
    }

    public static void clear() {
        SKINS.clear();
        SKIN_TEXTURES.clear();
        REQUESTED.clear();
    }
}
