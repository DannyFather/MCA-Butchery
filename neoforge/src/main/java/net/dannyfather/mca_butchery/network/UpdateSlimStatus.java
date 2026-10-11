package net.dannyfather.mca_butchery.network;

import io.netty.buffer.ByteBuf;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateSlimStatus(boolean slim) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateSlimStatus> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"update_slim_status"));

    public static final StreamCodec<ByteBuf, UpdateSlimStatus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            UpdateSlimStatus::slim,
            UpdateSlimStatus::new)
            ;

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
