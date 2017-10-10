package simpleworkflow.engine.persistence.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import simpleworkflow.core.error.WorkflowPersistenceException;

public class DataIdGenerator {

    private final byte _serverNum;
    private final Random _rand;
    private final AtomicInteger _seq;

    public DataIdGenerator(byte serverNum) {
        _serverNum = serverNum;
        _rand = new Random(System.currentTimeMillis());
        _seq = new AtomicInteger(Integer.MIN_VALUE);

    }
    
    public String newDataId() throws WorkflowPersistenceException {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.clear();

        buffer.order(ByteOrder.BIG_ENDIAN);

        //UTC(8 byte) ----->
        buffer.putLong(System.currentTimeMillis());

        //4 byte ----->
        int randNum = (_rand.nextInt() & 0x00ffffff)
                | ( (_serverNum) << 24);
        buffer.putInt(randNum);

        //4 byte ----->
        buffer.putInt(_seq.incrementAndGet());


        return HexUtil.toHexString(buffer.array(), 0, 16);
    }
}
