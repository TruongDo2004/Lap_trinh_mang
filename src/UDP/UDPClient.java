package UDP;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public interface ProgressListener {
        void onProgress(long sent, long total);
        void onDone();
        void onError(Exception ex);
    }

    private static final int CHUNK = 60000;

    public static void sendFile(File file, String username, String host, int port, ProgressListener listener) {
        new Thread(() -> {
            DatagramSocket socket = null;
            FileInputStream fis = null;
            try {
                socket = new DatagramSocket();
                InetAddress addr = InetAddress.getByName(host);
                String filename = file.getName();

                // START
                String start = "START||" + username + "||" + filename;
                socket.send(new DatagramPacket(start.getBytes("UTF-8"), start.getBytes("UTF-8").length, addr, port));

                long total = file.length();
                long sent = 0;
                fis = new FileInputStream(file);
                byte[] buf = new byte[CHUNK];
                int seq = 0;
                int read;
                while ((read = fis.read(buf)) != -1) {
                    String head = "DATA||" + username + "||" + filename + "||" + seq + "||" + read + "||";
                    byte[] headB = head.getBytes("ISO-8859-1");
                    byte[] packet = new byte[headB.length + read];
                    System.arraycopy(headB, 0, packet, 0, headB.length);
                    System.arraycopy(buf, 0, packet, headB.length, read);
                    DatagramPacket dp = new DatagramPacket(packet, packet.length, addr, port);
                    socket.send(dp);

                    sent += read;
                    seq++;
                    if (listener != null) listener.onProgress(sent, total);

                    try { Thread.sleep(2); } catch (InterruptedException ignored) {}
                }

                // END
                String end = "END||" + username + "||" + filename;
                socket.send(new DatagramPacket(end.getBytes("UTF-8"), end.getBytes("UTF-8").length, addr, port));

                if (listener != null) { listener.onProgress(total, total); listener.onDone(); }
            } catch (Exception ex) {
                if (listener != null) listener.onError(ex);
                else ex.printStackTrace();
            } finally {
                try { if (fis != null) fis.close(); } catch (Exception ignored) {}
                if (socket != null) socket.close();
            }
        }, "UDPClient-SendThread").start();
    }
}
