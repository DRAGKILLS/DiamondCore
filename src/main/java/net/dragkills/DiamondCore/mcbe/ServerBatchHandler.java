package net.dragkills.DiamondCore.mcbe;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import io.netty.buffer.ByteBuf;
import net.dragkills.DiamondCore.Player;

import java.util.Collection;

public class ServerBatchHandler implements BatchHandler {

    private final BedrockSession session;

    private final Player player;

    public ServerBatchHandler(BedrockSession session, Player player) {
        this.session = session;
        this.player = player;
    }

    @Override
    public void handle(BedrockSession bedrockSession, ByteBuf byteBuf, Collection<BedrockPacket> collection) {
        //NOP
    }

    public boolean handlePacket(BedrockPacket packet) {
        if(packet instanceof ServerToClientHandshakePacket) {
            ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
            session.sendPacket(clientToServerHandshake);
            return true;
        }
        if(packet instanceof ResourcePacksInfoPacket) {
            ResourcePackClientResponsePacket pk = new ResourcePackClientResponsePacket();
            pk.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
            session.sendPacket(pk);
            return true;
        }
        if(packet instanceof ResourcePackStackPacket) {
            ResourcePackClientResponsePacket pk = new ResourcePackClientResponsePacket();
            pk.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
            session.sendPacket(pk);
            return true;
        }
        if(packet instanceof StartGamePacket) {
            player.setPlayerIdServer((int) ((StartGamePacket) packet).getRuntimeEntityId());
            player.sendMove(((StartGamePacket) packet).getPlayerPosition(), MovePlayerPacket.Mode.TELEPORT);

            RequestChunkRadiusPacket chunkRadiusPacket = new RequestChunkRadiusPacket();
            chunkRadiusPacket.setRadius(8);
            session.sendPacket(chunkRadiusPacket);

            TickSyncPacket tickSyncPacket = new TickSyncPacket();
            tickSyncPacket.setResponseTimestamp(0);
            tickSyncPacket.setRequestTimestamp(0);
            session.sendPacket(tickSyncPacket);

            SetLocalPlayerAsInitializedPacket initializedPacket = new SetLocalPlayerAsInitializedPacket();
            initializedPacket.setRuntimeEntityId(player.getPlayerIdServer());
            session.sendPacket(initializedPacket);
            return true;
        }
        if(packet instanceof PlayStatusPacket) {
            if(((PlayStatusPacket) packet).getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
                SetLocalPlayerAsInitializedPacket initializedPacket = new SetLocalPlayerAsInitializedPacket();
                initializedPacket.setRuntimeEntityId(player.getPlayerIdServer());
                session.sendPacket(initializedPacket);
            }
            return true;
        }
        if(packet instanceof LoginPacket) {
            PlayStatusPacket status = new PlayStatusPacket();
            status.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
            session.sendPacket(status);
            return true;
        }
        if(packet instanceof BossEventPacket) {
            ((BossEventPacket) packet).setTitle(((BossEventPacket) packet).getTitle());
        }
        if(packet instanceof DisconnectPacket) {
            session.disconnect();
            return false;
        }
        if(packet instanceof AvailableEntityIdentifiersPacket || packet instanceof BiomeDefinitionListPacket || packet instanceof CreativeContentPacket || packet instanceof ItemComponentPacket) {
            return true;
        }
        return false;
    }
}
