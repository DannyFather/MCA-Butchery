package net.dannyfather.mca_butchery.network;

import io.netty.buffer.ByteBuf;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record DownloadVillagerSkinPayload(byte[] data, UUID villager) implements CustomPacketPayload {
    public static final Type<UploadVillagerSkinPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"villager_skin_upload"));

    public static final StreamCodec<ByteBuf, UploadVillagerSkinPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE_ARRAY,
                    UploadVillagerSkinPayload::data,
                    UUIDUtil.STREAM_CODEC,
                    UploadVillagerSkinPayload::villager,
                    UploadVillagerSkinPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
