package kafka.localhost;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.jws.soap.SOAPBinding;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.net.SocketImpl;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Properties;


public class MyCamServerLocal extends Thread {


    final private static int WEBCAM_DEVICE_INDEX = 0;
    final private static int AUDIO_DEVICE_INDEX = 4;

    final private static int FRAME_RATE = 30;
    final private static int GOP_LENGTH_IN_FRAMES = 60;

    private static long startTime = 0;
    private static long videoTS = 0;
    private JPanel panel;
    private JLabel image;

    Properties props = new Properties();




    public MyCamServerLocal() {

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.41.131:6667");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataJsonSerializer.class.getName());

    }

    public void run() {
        final int captureWidth = 1280;
        final int captureHeight = 720;
        //play web cam
        final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        KafkaProducer<String, MyCustomeFrame> producer = new KafkaProducer<String, MyCustomeFrame>(props);


        while (true){
            try {
                Frame frame = grabber.grabFrame();
                byte[][] array_bytes = new byte[frame.image.length][];
                for(int i=0; i<array_bytes.length;i++){
                    Buffer b = frame.image[i];
                    ByteBuffer tmp=(ByteBuffer) b;
                    array_bytes[i] = new byte[tmp.limit()];
                    tmp.rewind();
                    if(tmp.remaining()>0){
                        tmp.get(array_bytes[i] ,0,tmp.remaining());
                    }
                }


                MyCustomeFrame data = new MyCustomeFrame(frame.imageWidth,frame.imageHeight,frame.imageDepth,frame.imageChannels,frame.imageStride);
                ProducerRecord<String, MyCustomeFrame> record = new ProducerRecord<String, MyCustomeFrame>("video-channel",data);
                producer.send(record);



            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyCamServerLocal myAudioServer = new MyCamServerLocal();
        myAudioServer.start();
    }
}
