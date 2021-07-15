package kafka.product;

import java.util.Arrays;

public class DataModel {
    public byte[] bytes;
    public int numBytesRead;
    public long position ;
    public int type;
    float sampleRate;
    int sampleSizeInBits ;
    int channel ;
    boolean signed ;
    boolean bigEndian ;


    public DataModel() {
    }

    public DataModel( byte[] bytes, int numBytesRead, long position) {
        this.bytes = bytes;
        this.numBytesRead = numBytesRead;
        this.position = position;
    }

    public DataModel(int type, byte[] bytes, int numBytesRead, long position) {
        this.type = type;
        this.bytes = bytes;
        this.numBytesRead = numBytesRead;
        this.position = position;
    }

    public DataModel( int type, float sampleRate, int sampleSizeInBits, int channel, boolean signed, boolean bigEndian,long position) {
        this.position = position;
        this.type = type;
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channel = channel;
        this.signed = signed;
        this.bigEndian = bigEndian;
    }

    public DataModel(int type, byte[] bytes, int numBytesRead, float sampleRate, int sampleSizeInBits, int channel, boolean signed, boolean bigEndian,long position){
        this.type = type;
        this.bytes = bytes;
        this.numBytesRead = numBytesRead;
        this.position = position;
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channel = channel;
        this.signed = signed;
        this.bigEndian = bigEndian;
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

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    public void setSampleSizeInBits(int sampleSizeInBits) {
        this.sampleSizeInBits = sampleSizeInBits;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public boolean isBigEndian() {
        return bigEndian;
    }

    public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "bytes=" + Arrays.toString(bytes) +
                ", numBytesRead=" + numBytesRead +
                ", position=" + position +
                '}';
    }
}
