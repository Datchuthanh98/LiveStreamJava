package socket.audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyAudioServer extends Thread {
    // Socket to handle clients
    private ServerSocket serverSocket;
    private final int serverPort = 8889;
    private final int udpPort = 9000;
    private List<ClientAddress> clients = new ArrayList<>();
    private HashMap<InetAddress, Integer> addressPortMap = new HashMap<>();
    private AudioFormat format;

    private DatagramSocket socket = null;

    public MyAudioServer() {
        try {
            serverSocket = new ServerSocket(serverPort);
            socket = new DatagramSocket(udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted " + clientSocket.getInetAddress().toString()+":"+clientSocket.getPort());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClientAddress clientAddress = null;
                        try {
                            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                            int portUDp = 6969;
                            oos.writeObject(portUDp);
                            System.out.println(clientSocket.getInetAddress().toString() + " receive port: " + portUDp);
                            clientAddress = new ClientAddress(clientSocket.getInetAddress(), portUDp);
                            clients.add(clientAddress);

                            // Send client format of audio
                            oos.writeObject(format.getSampleRate());
                            oos.writeObject(format.getSampleSizeInBits());
                            oos.writeObject(format.getChannels());
                            oos.writeObject(true);
                            oos.writeObject(format.isBigEndian());

                            while (true) {
                                int cmd = ois.readInt();
                                if (cmd == 0) {
                                    clients.remove(clientAddress);
                                    clientSocket.close();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            clients.remove(clientAddress);
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data, int len) {
        for (ClientAddress address : clients) {
            if (socket != null) {
                DatagramPacket packet = new DatagramPacket(data, len, address.address, address.port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFormat(AudioFormat format) {
        this.format = format;
    }

    class ClientAddress{
        public final InetAddress address;
        public final int port;

        public ClientAddress(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientAddress that = (ClientAddress) o;
            return port == that.port && address.equals(that.address);
        }
    }
}
