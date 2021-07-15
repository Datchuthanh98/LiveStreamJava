package socket.audio;

import javax.sound.sampled.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class MyAudioClient extends Thread {
    // Audio format to play
    private AudioFormat format;
    // Speaker to play received data
    private SourceDataLine speaker;

    // TCP Socket to send and receive data from streaming server
    private Socket tcp;
    private int serverPort = 8889;

    // UDP Socket to stream
    private DatagramSocket udp;
    private int udpPort;

    public MyAudioClient() {
        try {
            // Set up TCP Socket and send UDP streaming port
            tcp = new Socket(InetAddress.getByName("localhost"), serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(tcp.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(tcp.getInputStream());
            System.out.println("Sending data port");
            udpPort = (int) ois.readObject();

            // Receive audio format from server
            float sampleRate = (float) ois.readObject();
            int sampleSizeInBits = (int) ois.readObject();
            int channel = (int) ois.readObject();
            boolean signed = (boolean) ois.readObject();
            boolean bigEndian = (boolean) ois.readObject();
            format = new AudioFormat(sampleRate, sampleSizeInBits, channel, signed, bigEndian);

            // Set up speaker
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);

            // Set up streaming UDP Socket
            udp = new DatagramSocket(udpPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            speaker.start();
            // Buffer to receive data
            byte[] data = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(data, 1024);
                udp.receive(packet);
                // Play it on speaker
                speaker.write(packet.getData(), 0, packet.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
