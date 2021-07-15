package mqtt;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.media.sound.WaveFileReader;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;

/**
 * A Mqtt topic subscriber
 *
 */
public class TopicSubscriber {
    Gson g = new Gson();
    // Audio format to play
    private AudioFormat format;
    // Speaker to play received data
    private SourceDataLine speaker;

    String filePath = "src/testt.wav";
    AudioFileReader afd = new WaveFileReader();
    String host = "tcp://itdev.mobifone.vn:1884";
    String username = "datchuthanh";
    String password = "datchuthanh98";
    final String subTopic = "uchiha";
  final   ObjectMapper objectMapper = new ObjectMapper();
    public TopicSubscriber() {

    }

    public static String generateString(){
        String uuid = UUID.randomUUID().toString();
        return "uuid"+uuid;
    }

    public void run() {
        System.out.println("TopicSubscriber initializing...");


        try {
            // Create an Mqtt client
            MqttClient mqttClient = new MqttClient(host, generateString());
            System.out.println(generateString());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());

            // Connect the client
            System.out.println("Connecting to Solace messaging at "+host);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            // Latch used for synchronizing b/w threads
            final CountDownLatch latch = new CountDownLatch(1);

        //-------------------------
            format = afd.getAudioFileFormat(new File(filePath)).getFormat();
            final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);
            speaker.start();



            mqttClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    String time = new Timestamp(System.currentTimeMillis()).toString();
//                    System.out.println("\nReceived a Message!" +
//                            "\n\tTime:    " + time +
//                            "\n\tTopic:   " + topic +
//                            "\n\tMessage: " + new String(message.getPayload()) +
//                            "\n\tQoS:     " + message.getQos() + "\n");
//                    System.out.println("get data");
//                    System.out.println(message.toString());

                    DataModel dataModel = objectMapper.readValue(message.toString(), DataModel.class);
                    System.out.println(dataModel.getPostion());
                    speaker.write(dataModel.getBytes(), 0, dataModel.getNumBytesRead());
                }

                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to Solace messaging lost!" + cause.getMessage());
                    latch.countDown();
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }

            });

            // Subscribe client to the topic filter and a QoS level of 0
            System.out.println("Subscribing client to topic: " + subTopic);
            mqttClient.subscribe(subTopic, 0);
            System.out.println("Subscribed");

            // Wait for the message to be received
            try {
                latch.await(); // block here until message received, and latch will flip
            } catch (InterruptedException e) {
                System.out.println("I was awoken while waiting");
            }

            // Disconnect the client
//            mqttClient.disconnect();
//            System.out.println("Exiting");
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
//            System.out.println("Usage: topicSubscriber tcp://<host:port> <client-username> <client-password>");
//            System.out.println();
//            System.exit(-1);
//        }


           for(int i = 0 ;i< 1 ;i++){
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       TopicSubscriber topicSubscriber = new TopicSubscriber();
                       topicSubscriber.run();
                   }
               }).start();

           }




    }
}