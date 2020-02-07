package io.mywish.blockchain;

import lombok.Getter;

import java.util.List;

@Getter
public class WrapperLog {
    protected final List<Object> args;
    private final String address;
    private final String name;
    private final String signature;

    public WrapperLog(String address, String name, String signature, List<Object> args) {
        this.address = address;
        this.name = name;
        this.signature = signature;
        this.args = args;
    }
}
