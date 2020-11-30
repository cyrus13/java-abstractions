package net.anastasakis.pattern.chain;

public interface Handler<R,T> {
    boolean canApply(R request);
    T doApply(R request);
}