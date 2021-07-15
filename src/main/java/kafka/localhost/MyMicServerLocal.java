package kafka.localhost;

import com.sun.media.sound.WaveFileReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import java.util.Properties;


public class MyMicServerLocal extends Thread {
    AudioFileReader afd = new WaveFileReader();
    Properties props = new Properties();
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    // Audio source: microphone
    private TargetDataLine microphone;
    // Server to send data to clients


//    AudioTypeFormat audioTypeFormat;
    Integer position = 0;



    public MyMicServerLocal() {

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.41.128:6667");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataJsonSerializer.class.getName());
        try {
            // Set up microphone audio source
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        byte[] buffer = new byte[1024];
        KafkaProducer<String, DataModel> producer = new KafkaProducer<String, DataModel>(props);
        microphone.start();
        while (true) {
                int byteRead = microphone.read(buffer, 0, 1024);
                DataModel data = new DataModel(buffer,byteRead,position);
                ProducerRecord<String, DataModel> record = new ProducerRecord<String, DataModel>("sound-livestream",data);
                producer.send(record);
                position++;
            }
    }

    public static void main(String[] args) throws InterruptedException {
        MyMicServerLocal myMicServer = new MyMicServerLocal();
        myMicServer.start();
    }
}
