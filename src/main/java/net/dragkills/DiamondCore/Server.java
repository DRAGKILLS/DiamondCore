package net.dragkills.DiamondCore;

import com.nukkitx.protocol.bedrock.BedrockServer;
import lombok.Getter;
import net.dragkills.DiamondCore.log.Logger;
import net.dragkills.DiamondCore.mcbe.ServerHandler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Server
{

    @Getter
    private InetSocketAddress address;

    public static ArrayList<Player> players = new ArrayList<>();

    public Server(InetSocketAddress address)
    {
        this.address = address;
    }

    public void openHost()
    {
        InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", 19132);
        BedrockServer server = new BedrockServer(bindAddress);

        server.setHandler(new ServerHandler(this));

        server.bind().join();

        Logger.info("DiamondCore server running on " + address);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //NOP
            }
        };
        Timer timer = new Timer("main");
        timer.scheduleAtFixedRate(timerTask, 50, 50);
    }

    public Server getServer()
    {
        return this;
    }
}
