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
import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class MyAudioServerProduct extends Thread {
    // Input stream of audio file
    AudioInputStream ais;
    // Format of audio file
    AudioFormat format;
    // Reader to read audio file
    AudioFileReader afd = new WaveFileReader();
    // Server to send data to clients
    String filePath = "src/music1.wav";
    int position = 0 ;
//    Properties props = new Properties();
//    AudioTypeFormat audioTypeFormat;



    static {

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

    Properties props;




    public MyAudioServerProduct() {

        {
            try {
                props = AppConfig.getKafkaProperties();
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataJsonSerializer.class.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void run() {
        // Buffer to write audio data to
        try {
            // Speaker
            SourceDataLine speaker = null;
            // Set up speaker

            KafkaProducer<String, DataModel> producer = new KafkaProducer<String, DataModel>(props);
            while (true) {
                for (int i = 1; i <= 3; i++) {
                    format = afd.getAudioFileFormat(new File("src/bai"+i+".wav")).getFormat();
                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                    speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    speaker.open(format);

                    // mute the speaker
                    BooleanControl muteControl = (BooleanControl) speaker.getControl(BooleanControl.Type.MUTE);
                    muteControl.setValue(true);
                    speaker.start();
                    // read audio stream again if stream reaches its end
                    ais = afd.getAudioInputStream(new File("src/bai"+i+".wav"));
                    long frames1 = ais.getFrameLength();
                    double durationInSeconds1 = (frames1 + 0.0) / format.getFrameRate();

                    // total byte read from stream
                    long total = 0;
                    // total byte of stream
                    double totalToRead = ais.getFrameLength() * ais.getFormat().getFrameSize();
                    System.out.println("play "+"src/music"+i+".wav");
                    System.out.println("Total byte" + totalToRead);
                    System.out.println("Total total time" + durationInSeconds1);
                    int byteRead = (int) (totalToRead / durationInSeconds1);
                    System.out.println("byte to read " + byteRead);


                    float sampleRate = format.getSampleRate();
                    int sampleSizeInBits = format.getSampleSizeInBits();
                    int channel = (int) format.getChannels();
                    boolean signed = true;
                    boolean bigEndian = (boolean) format.isBigEndian();
                    format = new AudioFormat(sampleRate, sampleSizeInBits, channel, signed, bigEndian);
                    DataModel newMusic = new DataModel(1,sampleRate, sampleSizeInBits, channel,signed,bigEndian,position);
                    ProducerRecord<String, DataModel> recordNewMusic = new ProducerRecord<String, DataModel>("test-events", newMusic);
                    producer.send(recordNewMusic);

                    byte[] buffer = new byte[byteRead];

                    // read until end of stream
                    while (total < totalToRead) {
                        // read byte to buffer
                        int numBytesRead = ais.read(buffer, 0, byteRead);
                        // if end of stream: stop reading
                        if (numBytesRead == -1) break;
                        // increase number of byte read
                        total += numBytesRead;
                        // write byte to speaker but mute the speaker
                        speaker.write(buffer, 0, numBytesRead);
                        // send to clients
                        DataModel data = new DataModel(0,buffer, numBytesRead,sampleRate, sampleSizeInBits, channel,signed,bigEndian,position);

                        System.out.println("position " + position);
                        ProducerRecord<String, DataModel> record = new ProducerRecord<String, DataModel>("test-events", data);
                        producer.send(record);
                        position++;

                    }

                    // empty the speaker queue
                    speaker.drain();
                    // close stream
                    ais.close();
                }
            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyAudioServerProduct myAudioServer = new MyAudioServerProduct();
        myAudioServer.start();
    }
}
