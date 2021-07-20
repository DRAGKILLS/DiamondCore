package net.dragkills.DiamondCore.mcbe;

import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import com.nukkitx.protocol.bedrock.v440.Bedrock_v440;
import net.dragkills.DiamondCore.Player;
import net.dragkills.DiamondCore.Server;
import net.dragkills.DiamondCore.log.Logger;

import java.net.InetSocketAddress;

public class ServerHandler implements BedrockServerEventHandler {

    private Server server;

    public ServerHandler(Server DiamondCoreServer) {
        server = DiamondCoreServer;
    }

    @Override
    public boolean onConnectionRequest(InetSocketAddress address) {
        return true;
    }

    @Override
    public BedrockPong onQuery(InetSocketAddress address) {
        BedrockPong pong = new BedrockPong();
        pong.setEdition("MCPE");
        pong.setMotd("§l§bDiamondCore Server");
        pong.setPlayerCount(Server.players.size());
        pong.setGameType("Survival");
        pong.setVersion("1.17.10");
        pong.setProtocolVersion(Bedrock_v440.V440_CODEC.getProtocolVersion());
        pong.setIpv4Port(19132);
        pong.setIpv6Port(19132);
        pong.setNintendoLimited(false);
        pong.setMaximumPlayerCount(20);
        pong.setSubMotd("§l§bDiamondCore server");
        return pong;
    }

    public DisconnectPacket createDisconnect(String message){
        DisconnectPacket dc = new DisconnectPacket();
        dc.setKickMessage(message);
        dc.setMessageSkipped(false);
        return dc;
    }

    @Override
    public void onSessionCreation(BedrockServerSession serverSession) {
        if(Server.players.size() > 20) {
            serverSession.sendPacketImmediately(createDisconnect("Server full!"));
            serverSession.disconnect();
            return;
        }
        Player player = new Player(serverSession, this, server);
        serverSession.addDisconnectHandler((reason) -> {
            if(player.getServerSession() != null) {
                player.getServerSession().disconnect();
            }
            player.close();
        });
        serverSession.setBatchHandler(new ClientBatchHandler(serverSession, player));
        Logger.info(serverSession.getAddress() + " Joined The Server!");
    }
}
