package com.trionesdev.commons.mybatisplus.incrementer;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TrionesIdentifierGenerator implements IdentifierGenerator {
    private final Sequence sequence;

    @SneakyThrows
    public TrionesIdentifierGenerator(){
        InetAddress inetAddress = InetAddress.getLocalHost();
        this.sequence = new Sequence(inetAddress);
    }

    public TrionesIdentifierGenerator(InetAddress inetAddress) {
        this.sequence = new Sequence(inetAddress);
    }

    public TrionesIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public TrionesIdentifierGenerator(Sequence sequence) {
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
