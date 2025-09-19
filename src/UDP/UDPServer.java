package UDP;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.*;

public class UDPServer {
    private final int port;
    private DatagramSocket socket;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ConcurrentMap<String, ByteArrayOutputStream> buffers = new ConcurrentHashMap<>();

    public UDPServer(int port) {
        this.port = port;
        MongoDBHelper.connect();
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("[UDPServer] listening on port " + port);
            byte[] buf = new byte[65507];
            while (true) {
                DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                socket.receive(pkt);
                // copy packet data
                byte[] data = new byte[pkt.getLength()];
                System.arraycopy(pkt.getData(), pkt.getOffset(), data, 0, pkt.getLength());
                pool.submit(() -> handlePacket(data));
            }
        } catch (java.net.BindException be) {
            System.err.println("[UDPServer] Port " + port + " already in use. " + be.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private void handlePacket(byte[] data) {
        try {
            String preview;
            try { preview = new String(data, 0, Math.min(200, data.length), "UTF-8"); } catch (Exception e) { preview = ""; }

            if (preview.startsWith("START||")) {
                String s = new String(data, "UTF-8");
                String[] p = s.split("\\|\\|", 3);
                if (p.length >= 3) {
                    String username = p[1], filename = p[2];
                    String key = key(username, filename);
                    buffers.put(key, new ByteArrayOutputStream());
                    System.out.printf("[SERVER] START key=%s filename=%s%n", key, filename);
                }
            } else if (preview.startsWith("DATA||")) {
                // DATA||username||filename||seq||len||<payload bytes...>
                String s = new String(data, 0, data.length, "ISO-8859-1");
                String[] p = s.split("\\|\\|", 6);
                if (p.length >= 6 && "DATA".equals(p[0])) {
                    String username = p[1], filename = p[2];
                    int payloadLen = Integer.parseInt(p[4]);
                    int idx = nthIndexOf(s, "||", 5);
                    if (idx >= 0) {
                        int payloadStart = idx + 2;
                        int avail = data.length - payloadStart;
                        int copyLen = Math.min(payloadLen, Math.max(0, avail));
                        if (copyLen > 0) {
                            byte[] payload = new byte[copyLen];
                            System.arraycopy(data, payloadStart, payload, 0, copyLen);
                            String key = key(username, filename);
                            ByteArrayOutputStream baos = buffers.computeIfAbsent(key, k -> new ByteArrayOutputStream());
                            baos.write(payload);
                        }
                    }
                } else {
                    // fallback: append to some buffer
                    if (!buffers.isEmpty()) buffers.values().iterator().next().write(data);
                }
            } else if (preview.startsWith("END||")) {
                String s = new String(data, "UTF-8");
                String[] p = s.split("\\|\\|", 3);
                if (p.length >= 3) {
                    String username = p[1], filename = p[2];
                    String key = key(username, filename);
                    ByteArrayOutputStream baos = buffers.remove(key);
                    if (baos != null) {
                        byte[] fileBytes = baos.toByteArray();
                        System.out.printf("[SERVER] END key=%s filename=%s size=%d bytes%n", key, filename, fileBytes.length);

                        // find user id if exists
                        Document userDoc = MongoDBHelper.findUser(username);
                        ObjectId uid = userDoc == null ? null : userDoc.getObjectId("_id");

                        // save to MongoDB
                        MongoDBHelper.saveFileBytes(uid, username, filename, fileBytes);
                        System.out.println("[SERVER] Saved file to MongoDB 'files' collection");
                    } else {
                        System.err.println("[SERVER] END but buffer missing for key=" + key);
                    }
                }
            } else {
                System.out.printf("[SERVER] Raw packet (%d bytes) ignored%n", data.length);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String key(String u, String f) { return u + "||" + f; }

    private int nthIndexOf(String s, String token, int n) {
        int pos = -1, from = 0;
        for (int i = 0; i < n; i++) {
            pos = s.indexOf(token, from);
            if (pos == -1) return -1;
            from = pos + token.length();
        }
        return pos;
    }

    public void shutdown() {
        if (socket != null && !socket.isClosed()) socket.close();
        pool.shutdownNow();
        System.out.println("[UDPServer] stopped");
    }

    public static void main(String[] args) {
        new UDPServer(9873).start();
    }
}
