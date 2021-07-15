package kafka.product;

import com.sun.media.sound.WaveFileReader;
import kafka.localhost.DataJsonSerializer;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.IOException;
import java.util.Properties;


public class MyMicServerProduct extends Thread {
    AudioFileReader afd = new WaveFileReader();
    Properties props = new Properties();
    private AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    // Audio source: microphone
    private TargetDataLine microphone;
    int position = 0 ;
//    Properties props = new Properties();
//    AudioTypeFormat audioTypeFormat;



    static {
        try {
//            PropertiesConfiguration config = AppConfig.getPropertiesConfiguration();
//            System.setProperty("java.security.auth.login.config", config.getString("java.security.auth.login.config"));
//            System.setProperty("java.security.krb5.conf", config.getString("java.security.krb5.conf"));
//            System.setProperty("javax.security.auth.useSubjectCredsOnly", config.getString("javax.security.auth.useSubjectCredsOnly"));
//            System.setProperty("sun.security.krb5.debug", config.getString("sun.security.krb5.debug"));
//            System.out.println("set variable successful");

            PropertiesConfiguration config = AppConfig.getPropertiesConfiguration();
            System.setProperty("java.security.auth.login.config", "C:\\\\hadoop\\\\kafka-jaas.conf");
            System.setProperty("java.security.krb5.conf", "C:\\\\hadoop\\\\krb5.conf");
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
            System.setProperty("sun.security.krb5.debug", "false");
            System.out.println("set variable successful");
        } catch (AppConfigException e) {
//            System.out.println(e);
        }
    }

    public MyMicServerProduct() {
        {
            try {
                props = AppConfig.getKafkaProperties();
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataJsonSerializer.class.getName());
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        byte[] buffer = new byte[1024];
        KafkaProducer<String, DataModel> producer = new KafkaProducer<String, DataModel>(props);
        microphone.start();
        while (true) {
            int byteRead = microphone.read(buffer, 0, 1024);
            DataModel data = new DataModel(buffer,byteRead,position);
            ProducerRecord<String, DataModel> record = new ProducerRecord<String, DataModel>("test-events",data);
            producer.send(record);
            position++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyMicServerProduct myAudioServer = new MyMicServerProduct();
        myAudioServer.start();
    }
}
