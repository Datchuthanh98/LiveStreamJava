package mqtt;

import java.util.Arrays;

public class DataModel {
    public byte[] bytes;
    public int numBytesRead;
    public int posiion ;

    public DataModel() {
    }

    public DataModel(byte[] bytes, int numBytesRead, int posiion) {
        this.bytes = bytes;
        this.numBytesRead = numBytesRead;
        this.posiion = posiion;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getNumBytesRead() {
        return numBytesRead;
    }

    public void setNumBytesRead(int numBytesRead) {
        this.numBytesRead = numBytesRead;
    }

    public int getPostion() {
        return posiion;
    }

    public void setPostion(int postion) {
        this.posiion = postion;
    }

    @Override
    public String toString() {
        return "mqtt.DataModel{" +
                "bytes=" + Arrays.toString(bytes) +
                ", numBytesRead=" + numBytesRead +
                ", postion=" + posiion +
                '}';
    }
}
