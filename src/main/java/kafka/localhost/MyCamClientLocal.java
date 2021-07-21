package kafka.localhost;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.sound.WaveFileReader;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.spi.AudioFileReader;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class MyCamClientLocal implements Runnable {
    private static Integer idClient;
    // Audio format to play
    private AudioFormat format = null;
    // Speaker to play received data
    private SourceDataLine speaker;

    String filePath = "src/music1.wav";
    AudioFileReader afd = new WaveFileReader();
    Properties props = new Properties();
    DataLine.Info dataLineInfo;
    Integer i = 0;



   MyCustomArray arrayTest = new MyCustomArray();


    public static String generateString() {
        return "uuid" + UUID.randomUUID().toString();
    }
    public MyCamClientLocal(Integer i) {
        this.i = i;

        try {
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.41.131:6667");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
            //props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
            props.put(ConsumerConfig.CLIENT_ID_CONFIG,generateString());
            props.put(ConsumerConfig.GROUP_ID_CONFIG,generateString());
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, StickyAssignor.class.getName());
            props.put("key.deserializer",
                    "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("serializer.class", "kafka.serializer.DefaultEncoder");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(Collections.singleton("video-channel"));
            ObjectMapper objectMapper = new ObjectMapper();
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(5);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());
//                    MyCustomeFrame myCustomeFrame = objectMapper.readValue(record.value(), MyCustomeFrame.class);
//                    System.out.println(myCustomeFrame.imageHeight);
                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {
            final int finalI = i;
            new Thread(new MyCamClientLocal(finalI), finalI + "").start();
        }
    }
}
