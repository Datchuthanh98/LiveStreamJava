package testCam;

import java.awt.*;
import java.util.Properties;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import javax.swing.*;

import com.sun.media.sound.WaveFileReader;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameRecorder.Exception;

public class CameraTestJavaCV extends JFrame{
    final private static int WEBCAM_DEVICE_INDEX = 0;
    final private static int AUDIO_DEVICE_INDEX = 4;

    final private static int FRAME_RATE = 30;
    final private static int GOP_LENGTH_IN_FRAMES = 60;

    private static long startTime = 0;
    private static long videoTS = 0;
    private JPanel panel;
    private JLabel image;



    public static void main(String[] args) throws Exception, org.bytedeco.javacv.FrameGrabber.Exception {



//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                AudioFileReader afd = new WaveFileReader();
//                Properties props = new Properties();
//                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
//                // Audio source: microphone
//                TargetDataLine microphone = null;
//                // Server to send data to clients
//                byte[] buffer = new byte[1024];
//                SourceDataLine speaker = null;
//
//                try {
//                    // Set up microphone audio source
//                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//                    microphone = (TargetDataLine) AudioSystem.getLine(info);
//                    microphone.open(format);
//
//                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
//                    speaker = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//                    speaker.open(format);
//                } catch (
//                        java.lang.Exception e) {
//                    e.printStackTrace();
//                }
//
//                microphone.start();
//                speaker.start();
//                while (true) {
//                    int byteRead = microphone.read(buffer, 0, 1024);
//                    speaker.write(buffer,0,byteRead);
//
//                }
//            }
//        }).start();



     new Thread(new Runnable() {
         @Override
         public void run() {
             CameraTestJavaCV jframe = new CameraTestJavaCV();
             jframe.setContentPane(jframe.panel);
             jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
             jframe.panel.setPreferredSize(new Dimension(1280, 720));
             jframe.pack();
             jframe.setLocationRelativeTo(null);
             jframe.setVisible(true);

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

             while (true){
                 try {
                     System.out.println(grabber.grabFrame());
                     jframe.image.setIcon(new ImageIcon(new Java2DFrameConverter().getBufferedImage(grabber.grabFrame())));
                 } catch (FrameGrabber.Exception e) {
                     e.printStackTrace();
                 }
             }
         }
     }).start();


    }
}