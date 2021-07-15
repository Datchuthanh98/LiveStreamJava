package kafka.webcam;

import com.sun.media.sound.WaveFileReader;
import kafka.product.DataModel;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.spi.AudioFileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

public class MyWebCamClient implements Runnable {
    private static Integer idClient;
    // Audio format to play
    private AudioFormat format;
    // Speaker to play received data
    private SourceDataLine speaker;

    String filePath = "src/songio.wav";
    AudioFileReader afd = new WaveFileReader();
    Properties props = new Properties();
    Properties props2 = new Properties();
//    private volatile AtomicInteger currentPostion = new AtomicInteger(0);
    Integer currentPostion = 0 ;
    PriorityBlockingQueue<DataModel> queue = new PriorityBlockingQueue<>(20, new Comparator<DataModel>() {
        @Override
        public int compare(DataModel o1,DataModel o2) {
            if (o1.position > o2.position) return 1;
            else if (o1.position < o2.position) return -1;
            else return 0;
        }
    });

    String i = "" ;

    public static String generateString(){
        return "uuid"+UUID.randomUUID().toString();
    }

    Integer indexPosition = 0;




    public MyWebCamClient(String  i) {
        this.i = i;

        try {


            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.41.128:6667");
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



            props2.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.41.128:6667");
            props2.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
            //props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
            props2.put(ConsumerConfig.CLIENT_ID_CONFIG,generateString());
            props2.put(ConsumerConfig.GROUP_ID_CONFIG,generateString());
            props2.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props2.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, StickyAssignor.class.getName());
            props2.put("key.deserializer",
                    "org.apache.kafka.common.serialization.StringDeserializer");
            props2.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            props2.put("serializer.class", "kafka.serializer.DefaultEncoder");




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {


            KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(props2);
            consumer.subscribe(Collections.singleton("sound-livestream"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(0);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value().toString());
//                    ObjectMapper objectMapper = new ObjectMapper();
//                        DataModel dataModel = objectMapper.readValue(record.value(), DataModel.class);


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    public static void main(String[] args) {

        for(int i = 0 ; i< 1; i++){
            final int finalI = i;
            new Thread(new MyWebCamClient(""+finalI), finalI+"").start();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    MyAudioClient myAudioClient = new MyAudioClient(""+ finalI);
//                    myAudioClient.run();
//                }
//            }).start();
        }



        }


}
