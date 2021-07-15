package kafka.webcam;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.io.IOException;
import java.util.Properties;


public class MyWebCamServer extends Thread {
    Properties props = new Properties();
//    AudioTypeFormat audioTypeFormat;



    public MyWebCamServer() {

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.41.128:6667");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataJsonSerializer.class.getName());
//        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer"); // Serialization of values
//        props.put("key.deserializer",
//                "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("serializer.class", "kafka.serializer.DefaultEncoder");


    }

    public void run() {
        final int captureWidth = 1280;
        final int captureHeight = 720;
        //play web cam
        final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);

        try {
            grabber.start();
            KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
            while (true) {
                Frame data = grabber.grabFrame();
                System.out.println(data.image.toString());
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>("sound-livestream",data.image.toString());
                    producer.send(record);
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyWebCamServer myWebCamServer = new MyWebCamServer();
        myWebCamServer.start();
    }
}
