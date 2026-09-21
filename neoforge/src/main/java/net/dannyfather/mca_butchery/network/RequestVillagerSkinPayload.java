package net.dannyfather.mca_butchery.network;

import io.netty.buffer.ByteBuf;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.UUID;

public record RequestVillagerSkinPayload(UUID uuid) implements CustomPacketPayload {
    public static final Type<RequestVillagerSkinPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"request_villager_skin"));

    public static final StreamCodec<ByteBuf, RequestVillagerSkinPayload> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC,RequestVillagerSkinPayload::uuid,RequestVillagerSkinPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
