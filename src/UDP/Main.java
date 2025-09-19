package UDP;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // ensure DB connected + default admin
        MongoDBHelper.connect();
        MongoDBHelper.ensureDefaultAdmin();

        // start UDP server in background (daemon)
        Thread serverThread = new Thread(() -> {
            UDPServer server = new UDPServer(9873);
            server.start();
        }, "UDP-Server");
        serverThread.setDaemon(true);
        serverThread.start();

        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}
