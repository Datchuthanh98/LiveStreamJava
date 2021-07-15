package socket.audio;

import com.sun.media.sound.WaveFileReader;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;
import java.io.IOException;

public class MyAudioPlayer extends Thread {
    // Input stream of audio file
    AudioInputStream ais;
    // Format of audio file
    AudioFormat format;
    // Reader to read audio file
    AudioFileReader afd = new WaveFileReader();
    // Server to send data to clients
    MyAudioServer audioServer;
    // Path to file
    String filePath = "src/music.wav";

    public MyAudioPlayer(MyAudioServer audioServer) {
        this.audioServer = audioServer;
        try {
            // Read format of file
            format = afd.getAudioFileFormat(new File(filePath)).getFormat();
            // Set format to audio server
            audioServer.setFormat(format);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Buffer to write audio data to
        byte[] buffer = new byte[1024];

        // Speaker
        SourceDataLine speaker = null;
        try {
            // Set up speaker
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);
            // mute the speaker
            BooleanControl muteControl = (BooleanControl) speaker.getControl(BooleanControl.Type.MUTE);
            muteControl.setValue(true);
            speaker.start();

            while (true) {
                // read audio stream again if stream reaches its end
                ais = afd.getAudioInputStream(new File(filePath));
                // total byte read from stream
                long total = 0;
                // total byte of stream
                long totalToRead = ais.getFrameLength() * ais.getFormat().getFrameSize();
                // read until end of stream
                while (total < totalToRead) {
                    // read byte to buffer
                    int numBytesRead = ais.read(buffer, 0, 1024);
                    // if end of stream: stop reading
                    if (numBytesRead == -1) break;
                    // increase number of byte read
                    total += numBytesRead;
                    // write byte to speaker but mute the speaker
                    speaker.write(buffer, 0, numBytesRead);
                    // send to clients
                    audioServer.send(buffer, numBytesRead);
                }
                // empty the speaker queue
                speaker.drain();
                // close stream
                ais.close();
            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyAudioServer server = new MyAudioServer();
        server.start();
        MyAudioPlayer myAudioPlayer = new MyAudioPlayer(server);
        myAudioPlayer.start();


        MyAudioClient client = new MyAudioClient();
        client.start();


//        Thread.sleep(5000);
//        MyAudioClient client2 = new MyAudioClient();
//        client2.start();
    }
}
