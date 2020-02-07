package io.lastwill.eventscan.model;

public enum TransferStatus {
    WAIT_FOR_SEND,
    WAIT_FOR_CONFIRM,
    WAIT_FOR_REFILL,
    WAIT_REFILL_CONFIRM,
    REFILLED,
    DROPPED,
    ERROR,
    OK,
}