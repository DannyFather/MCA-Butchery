package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientSkinCache {
    private static final Map<String, ResourceLocation> SKINS = new HashMap<>();
    private static final Map<String, NativeImage> SKIN_TEXTURES = new HashMap<>();
    private static final Set<String> REQUESTED = new HashSet<>();
    public static void put (String uuid, ResourceLocation texture, NativeImage nativeImage) {
        SKINS.put(uuid,texture);
        SKIN_TEXTURES.put(uuid,nativeImage);
    }

    public static ResourceLocation get(String uuid) {
        return SKINS.get(uuid);
    }


    public static NativeImage getImage(String uuid) {
        return SKIN_TEXTURES.get(uuid);
    }

    public static boolean contains(String uuid) {
        return SKINS.containsKey(uuid);
    }

    public static boolean hasRequested(String uuid) {
        return REQUESTED.contains(uuid);
    }

    public static void markRequested(String uuid) {
        REQUESTED.add(uuid);
    }

    public static void clear() {
        SKINS.clear();
        SKIN_TEXTURES.clear();
        REQUESTED.clear();
    }
}
