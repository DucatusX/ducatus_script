package io.lastwill.eventscan.services.saver;

public interface DbSaver<T> {
    T save(T entry);
}
