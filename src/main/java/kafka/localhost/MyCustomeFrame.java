package kafka.localhost;

import java.io.Serializable;
import java.nio.Buffer;

public class MyCustomeFrame implements Serializable {
    public int imageWidth;
    public int imageHeight;
    public int imageDepth;
    public int imageChannels;
    public int imageStride;
    private byte[][] array_bytes;

    public MyCustomeFrame() {
    }

    public MyCustomeFrame(int imageWidth, int imageHeight, int imageDepth, int imageChannels, int imageStride, byte[][] array_bytes) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageDepth = imageDepth;
        this.imageChannels = imageChannels;
        this.imageStride = imageStride;
        this.array_bytes = array_bytes;
    }

    public MyCustomeFrame(int imageWidth, int imageHeight, int imageDepth, int imageChannels, int imageStride) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageDepth = imageDepth;
        this.imageChannels = imageChannels;
        this.imageStride = imageStride;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageDepth() {
        return imageDepth;
    }

    public void setImageDepth(int imageDepth) {
        this.imageDepth = imageDepth;
    }

    public int getImageChannels() {
        return imageChannels;
    }

    public void setImageChannels(int imageChannels) {
        this.imageChannels = imageChannels;
    }

    public int getImageStride() {
        return imageStride;
    }

    public void setImageStride(int imageStride) {
        this.imageStride = imageStride;
    }

    public byte[][] getArray_bytes() {
        return array_bytes;
    }

    public void setArray_bytes(byte[][] array_bytes) {
        this.array_bytes = array_bytes;
    }
}
