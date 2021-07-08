package net.dragkills.DiamondCore;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlags;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;
import lombok.Getter;
import lombok.Setter;
import net.dragkills.DiamondCore.log.Logger;
import net.dragkills.DiamondCore.mcbe.ClientBatchHandler;
import net.dragkills.DiamondCore.mcbe.ServerBatchHandler;
import net.dragkills.DiamondCore.mcbe.ServerHandler;

import java.util.ArrayList;
import java.util.LinkedList;

public class Player {

    @Getter
    private final ServerHandler serverHandler;

    @Getter
    @Setter
    private ClientBatchHandler clientBatchHandler;

    @Getter
    private final Server server;

    @Getter
    @Setter
    private ServerBatchHandler serverBatchHandler;

    @Getter
    private final BedrockSession clientSession;

    @Getter
    @Setter
    private int playerId;

    @Getter
    private BedrockSession serverSession;

    @Getter
    @Setter
    private boolean initialized;

    @Getter
    @Setter
    private int playerIdServer;

    @Setter
    @Getter
    private Vector3f position = Vector3f.ZERO;

    public Player(BedrockSession client, ServerHandler serverHandler, Server server) {
        this.serverHandler = serverHandler;
        this.server = server;
        this.clientSession = client;
        Server.players.add(this);
    }

    public void sendTip(String msg) {
        TextPacket textPacket = new TextPacket();
        textPacket.setParameters(new ArrayList<>());
        textPacket.setXuid("");
        textPacket.setSourceName("");
        textPacket.setMessage(msg);
        textPacket.setPlatformChatId("");
        textPacket.setType(TextPacket.Type.TIP);
        textPacket.setNeedsTranslation(false);
        clientSession.sendPacket(textPacket);
    }

    public void sendMove(Vector3f vector3f, MovePlayerPacket.Mode mode) {
        MovePlayerPacket packet = new MovePlayerPacket();
        packet.setRuntimeEntityId(playerId);
        packet.setTeleportationCause(MovePlayerPacket.TeleportationCause.BEHAVIOR);
        packet.setRotation(vector3f);
        packet.setPosition(vector3f);
        packet.setMode(mode);
        packet.setTick(0);
        packet.setOnGround(false);
        packet.setEntityType(0);
        packet.setRidingRuntimeEntityId(0);
        clientSession.sendPacket(packet);
        setPlayerFlag(EntityFlag.SHAKING, true);
    }

    public void sendMessage(String str) {
        TextPacket packet = new TextPacket();
        packet.setMessage(str);
        packet.setType(TextPacket.Type.RAW);
        packet.setPlatformChatId("");
        packet.setNeedsTranslation(false);
        packet.setXuid("");
        packet.setParameters(new LinkedList<>());
        packet.setSourceName("");
        clientSession.sendPacket(packet);
    }

    public void close() {
        Logger.info(clientSession.getAddress().getHostName() + " disconnected");
        Server.players.remove(this);
    }

    public void joinToServer() {
        sendMessage(clientSession.getAddress() + " joined to server!");
        setPlayerFlag(EntityFlag.BREATHING, true);
        setPlayerFlag(EntityFlag.INVISIBLE, false);
        setPlayerFlag(EntityFlag.ALWAYS_SHOW_NAME, true);
        setPlayerFlag(EntityFlag.BLOCKING, true);
        setPlayerFlag(EntityFlag.CAN_SWIM, true);
        setPlayerFlag(EntityFlag.MOVING, true);
        setPlayerFlag(EntityFlag.CAN_WALK, true);
    }

    public boolean handleChat(String message)
    {
        return false;
    }

    public void setFlying(boolean value) {
        setPlayerFlag(EntityFlag.CAN_FLY, value);
    }

    public void setNameAlwaysVisible(boolean value) {
        setPlayerFlag(EntityFlag.ALWAYS_SHOW_NAME, value);
    }

    public void setInvisible(boolean value) {
        setPlayerFlag(EntityFlag.INVISIBLE, value);
    }

    public void disconnectedFromServer() {
        this.setServerBatchHandler(null);
    }

    public void setPlayerFlag(EntityFlag flags, boolean value) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.setRuntimeEntityId(playerId);
        packet.setTick(0);
        EntityFlags flag = new EntityFlags();
        flag.setFlag(flags, value);
        packet.getMetadata().putFlags(flag);
        clientSession.sendPacket(packet);
    }
}
