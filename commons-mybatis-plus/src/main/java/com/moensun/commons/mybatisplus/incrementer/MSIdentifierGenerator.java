package com.moensun.commons.mybatisplus.incrementer;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MSIdentifierGenerator implements IdentifierGenerator {
    private final Sequence sequence;

    @SneakyThrows
    public MSIdentifierGenerator(){
        InetAddress inetAddress = InetAddress.getLocalHost();
        this.sequence = new Sequence(inetAddress);
    }

    public MSIdentifierGenerator(InetAddress inetAddress) {
        this.sequence = new Sequence(inetAddress);
    }

    public MSIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public MSIdentifierGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Number nextId(Object entity) {
        return sequence.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }
}
