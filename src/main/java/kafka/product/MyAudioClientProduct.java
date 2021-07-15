package kafka.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.sound.WaveFileReader;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import javax.xml.bind.SchemaOutputResolver;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

public class MyAudioClientProduct implements Runnable {
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

    static {
//            PropertiesConfiguration config = AppConfig.getPropertiesConfiguration();
//            System.setProperty("java.security.auth.login.config", config.getString("java.security.auth.login.config"));
//            System.setProperty("java.security.krb5.conf", config.getString("java.security.krb5.conf"));
//            System.setProperty("javax.security.auth.useSubjectCredsOnly", config.getString("javax.security.auth.useSubjectCredsOnly"));
//            System.setProperty("sun.security.krb5.debug", config.getString("sun.security.krb5.debug"));
//            System.out.println("set variable successful");

        try {
            PropertiesConfiguration config = AppConfig.getPropertiesConfiguration();
            System.setProperty("java.security.auth.login.config", "C:\\\\hadoop\\\\kafka-jaas.conf");
            System.setProperty("java.security.krb5.conf", "C:\\\\hadoop\\\\krb5.conf");
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
            System.setProperty("sun.security.krb5.debug", "false");
            System.out.println("set variable successful");
        } catch (AppConfigException e) {
            e.printStackTrace();
        }


    }

    public static String generateString() {
        return "uuid" + UUID.randomUUID().toString();
    }
    public MyAudioClientProduct(Integer i) {
        this.i = i;

        try {
            props = AppConfig.getKafkaProperties();

            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

            props.put(ConsumerConfig.CLIENT_ID_CONFIG, generateString());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, generateString());

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
            new Thread(new MyAudioClientProduct(finalI), finalI + "").start();
        }
    }
}
