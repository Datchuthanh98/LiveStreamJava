package kafka.localhost;

import java.util.concurrent.atomic.AtomicInteger;


public class MyCustomArray {
    private int maxCapacity;
    private final AtomicInteger size;
    private Long offset;
    private int currentPosition;
    private DataModel[] array;

    public MyCustomArray() {
        maxCapacity = 100000;
        size = new AtomicInteger(0);
        offset = null;
        currentPosition = 0;
        array = new DataModel[maxCapacity];
    }

    public void add(DataModel data) {
        if (offset == null) {
            offset = -data.position;
        }
        int arrayPosition = (int) (data.position + offset);
        // If queue reaches its capacity
        if (arrayPosition == maxCapacity) {
            synchronized (this) {
                if (currentPosition == 0 || currentPosition < maxCapacity/10) {
                    // rare case: queue is full or reading rate is much smaller than writing rate, expand queue
                    maxCapacity *= 2;
                    // overflow
                    if (maxCapacity < 0) throw new RuntimeException("Array capacity overflows");

                    DataModel[] tmp = new DataModel[maxCapacity];
                    System.arraycopy(array, 0, tmp, 0, maxCapacity / 2);
                    array = tmp;
                } else {
                    // if queue is not full, move unprocessed data to the beginning and decrease offset
                    System.arraycopy(array, currentPosition, array, 0, maxCapacity - currentPosition);
                    offset -= currentPosition;
                    arrayPosition -= currentPosition;
                    currentPosition = 0;
                }
            }
        }
        // Only take data after current position
        if (arrayPosition >= currentPosition) {
            array[arrayPosition] = data;
            size.incrementAndGet();
        }
    }

    public DataModel take() {
        synchronized (this) {
            if (size.get() == 0) return null;
            size.decrementAndGet();
            return array[currentPosition++];
        }
    }

    public int getSize() {
        return size.get();
    }
}
