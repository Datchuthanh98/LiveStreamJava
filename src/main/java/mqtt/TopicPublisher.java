package mqtt;
import com.google.gson.Gson;
import com.sun.media.sound.WaveFileReader;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class TopicPublisher {
    String host = "tcp://itdev.mobifone.vn:1884";
    String username = "datchuthanh";
    String password = "datchuthanh98";
    final String subTopic = "uchiha";

    // Input stream of audio file
    AudioInputStream ais;
    // Format of audio file
    AudioFormat format;
    // Reader to read audio file
    AudioFileReader afd = new WaveFileReader();
    // Server to send data to clients
    // Path to file
    String filePath = "src/testt.wav";
    Properties props = new Properties();
//    AudioTypeFormat audioTypeFormat;
    Gson gson = new Gson();

    public TopicPublisher() {

    }

    public void run() {
        try {
            byte[] buffer = new byte[1024];
            // Speaker
            SourceDataLine speaker = null;
            // Set up speaker
            format = afd.getAudioFileFormat(new File(filePath)).getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);
            // mute the speaker
            BooleanControl muteControl = (BooleanControl) speaker.getControl(BooleanControl.Type.MUTE);
            muteControl.setValue(true);
            speaker.start();


            // Create an Mqtt client
            MqttClient mqttClient = new MqttClient(host, "HelloWorldPub");
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());

            // Connect the client
            System.out.println("Connecting to Solace messaging at " + host);
            mqttClient.connect(connOpts);
            System.out.println("Connected");



            while (true) {
                // read audio stream again if stream reaches its end
                ais = afd.getAudioInputStream(new File(filePath));
                // total byte read from stream
                long total = 0;
                // total byte of stream
                long totalToRead = ais.getFrameLength() * ais.getFormat().getFrameSize();
                // read until end of stream
                int position = 0 ;
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
//                    audioServer.send(buffer, numBytesRead);
                    DataModel data = new DataModel(buffer,numBytesRead,position);
                    String dataString = gson.toJson(data);
                    MqttMessage message = new MqttMessage(dataString.getBytes());
                    message.setQos(0);
                    mqttClient.publish(subTopic, message);
                    position++;
                    System.out.println(data.getNumBytesRead());

                }
                // empty the speaker queue
                speaker.drain();
                // close stream
                ais.close();
            }

            // Disconnect the client
//            mqttClient.disconnect();
//            System.out.println("Message published. Exiting");
//            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Check command line arguments
//        if (args.length != 3) {
//            System.out.println("Usage: topicPublisher tcp://<host:port> <client-username> <client-password>");
//            System.out.println();
//            System.exit(-1);
//        }
         TopicPublisher topicPublisher = new TopicPublisher();
         topicPublisher.run();
    }
}