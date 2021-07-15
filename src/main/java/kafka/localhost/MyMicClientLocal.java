package kafka.localhost;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.sound.sampled.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

public class MyMicClientLocal implements Runnable {
    private static Integer idClient;
    // Audio format to play
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    // Speaker to play received data
    private SourceDataLine speaker;

    Properties props = new Properties();

    long currentPostion = 0 ;
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

    public MyMicClientLocal(String  i) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
;
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speaker.open(format);
            speaker.start();
            KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(Collections.singleton("sound-livestream"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(0);
                for (ConsumerRecord<String, String> record : records) {
                    ObjectMapper objectMapper = new ObjectMapper();
                        DataModel dataModel = objectMapper.readValue(record.value(), DataModel.class);
                        if(currentPostion < dataModel.getPosition()){
                        queue.put(dataModel);
                            System.out.println("vao queue "+dataModel.getPosition()+ "    "+this.i);
                        }else{
                            System.out.println("ko vao queue "+dataModel.getPosition() + "    "+this.i);
                        }

                           DataModel  dataModel1 = queue.take();
                            currentPostion = dataModel1.getPosition();
                            speaker.write(dataModel1.getBytes(), 0, dataModel1.getNumBytesRead());

//                            System.out.println("ra queue "+currentPostion);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        for(int i = 0 ; i< 1; i++){
            final int finalI = i;
            new Thread(new MyMicClientLocal(""+finalI), finalI+"").start();
        }



        }


}
