package sh.iwmc;

import sh.iwmc.core.DefaultServer;
import sh.iwmc.core.Server;

/**
 * Created by Brent on 01/24/2016.
 */
public class Fern {

    private Server server;

    private static Fern fern;

    public static void main(String[] args) {
        fern = new Fern();
        fern.init();
    }

    private Fern() {
    }

    private void init() {
        server = new DefaultServer();
        server.start();
    }

    public static Server getServer() {
        return fern.server;
    }
}
