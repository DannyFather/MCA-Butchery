package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class CorpseTexture {

    private final ResourceLocation location;
    private final DynamicTexture dynamicTexture;

    public CorpseTexture(String name,NativeImage base,NativeImage overlay,NativeImage mask,NativeImage overlayMask, boolean blood, boolean drained) {
        int width = base.getWidth();
        int height = base.getHeight();
        NativeImage result = new NativeImage(width, height, true);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseColor = base.getPixelRGBA(x,y);
                int overlayColor = overlay.getPixelRGBA(x,y);
                int maskColor = mask.getPixelRGBA(x,y);
                int maskAlpha = (maskColor >> 8) & 0xFF;;
                int overlayMaskColor = overlayMask.getPixelRGBA(x,y);
                int overlayMaskAlpha = (overlayMaskColor >> 8) & 0xFF;

                float overlayMaskAmount = overlayMaskAlpha / 255.0F;
                int resultColor = baseColor;

                if(drained) {
                    int desaturatedBase = desaturate(baseColor, 0.6f);
                    resultColor = desaturatedBase;
                }
                if (blood) {
                    resultColor = blend(resultColor, overlayColor, maskAlpha, overlayMaskAmount);
                }


                result.setPixelRGBA(x, y, resultColor);
            }
        }

        this.location = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "dynamic/" + name);

        this.dynamicTexture = new DynamicTexture(result);

        Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
    }

    private static int blend(int base, int overlay, int amount, float overlayAmount) {

        int baseA = (base >> 24) & 0xFF;
        int baseB = (base >> 16) & 0xFF;
        int baseG = (base >> 8) & 0xFF;
        int baseR = base & 0xFF;

        int overlayB = (overlay >> 16) & 0xFF;
        int overlayG = (overlay >> 8) & 0xFF;
        int overlayR = overlay & 0xFF;

        int visible = Math.min(255 - amount,baseA);


        int r = (int) ((overlayR * overlayAmount) + (baseR * (1.0F - overlayAmount)));
        int g = (int) ((overlayG * overlayAmount) + (baseG * (1.0F - overlayAmount)));
        int b = (int) ((overlayB * overlayAmount) + (baseB * (1.0F - overlayAmount)));

        return (visible << 24) | (b << 16) | (g << 8) | r;
    }

    private static int desaturate(int color, float amount) {
        int a = (color >> 24) & 0xFF;
        int b = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int r = color & 0xFF;

        float gray = (float) (r + g + b) /3;

        r = (int) (r + (gray - r) * amount);
        g = (int) (g + (gray - g) * amount);
        b = (int) (b + (gray - b) * amount);


        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public void close() {
        dynamicTexture.close();
    }
}

