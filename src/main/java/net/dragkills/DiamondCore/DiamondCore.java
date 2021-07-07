package net.dragkills.DiamondCore;

import net.dragkills.DiamondCore.data.Data;
import net.dragkills.DiamondCore.log.Logger;

import java.net.InetSocketAddress;

public class DiamondCore {

    public static void main(String[] args) {
        Logger.info("Loading Data...");
        Data.loadItemEntries();
        Data.loadBiomeDefinitions();
        Data.loadEntityIdentifiers();
        //Data.loadServer();

        Server server = new Server(new InetSocketAddress("0.0.0.0", 19132));

        server.openHost();
    }
}
