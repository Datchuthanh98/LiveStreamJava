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

public class MyAudioClientLocal implements Runnable {
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

//    PriorityBlockingQueue<DataModel> queue = new PriorityBlockingQueue<>(20, new Comparator<DataModel>() {
//        @Override
//        public int compare(DataModel o1, DataModel o2) {
//            if (o1.position > o2.position) return 1;
//            else if (o1.position < o2.position) return -1;
//            else return 0;
//        }
//    });

   MyCustomArray arrayTest = new MyCustomArray();


    public static String generateString() {
        return "uuid" + UUID.randomUUID().toString();
    }
    public MyAudioClientLocal(Integer i) {
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
            consumer.subscribe(Collections.singleton("test-events"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(0);
                for (ConsumerRecord<String, String> record : records) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    DataModel dataModel = objectMapper.readValue(record.value(), DataModel.class);
                    arrayTest.add(dataModel);
                }
                DataModel dataModel1 = arrayTest.take();

                if (dataModel1 != null) {
                    if (format == null) {
                        System.out.println("Create New Format Audio");
                        format = new AudioFormat(dataModel1.getSampleRate(), dataModel1.getSampleSizeInBits(), dataModel1.getChannel(), dataModel1.signed, dataModel1.bigEndian);
                        // Set up speaker
                        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                        speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                        speaker.open(format);
                        speaker.start();

                    }
//                    if(i != 0){
//                        BooleanControl muteControl = (BooleanControl) speaker.getControl(BooleanControl.Type.MUTE);
//                        muteControl.setValue(true);
//                    }
                    if (dataModel1.getType() == 0) {
                        System.out.println("speak pkg data: " + dataModel1.getPosition());
                        speaker.write(dataModel1.getBytes(), 0, dataModel1.getNumBytesRead());
                    } else if (dataModel1.getType() == 1) {
                        speaker.close();
                        System.out.println("Reset New Format Audio");
                        format = new AudioFormat(dataModel1.getSampleRate(), dataModel1.getSampleSizeInBits(), dataModel1.getChannel(), dataModel1.signed, dataModel1.bigEndian);
                        // Set up speaker
                        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                        speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                        speaker.open(format);
                        speaker.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {
            final int finalI = i;
            new Thread(new MyAudioClientLocal(finalI), finalI + "").start();
        }
    }
}
