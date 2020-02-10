package io.lastwill.eventscan.model;

import lombok.Getter;

@Getter
public enum NetworkType {
    DAPS_MAINNET(NetworkProviderType.DAPS),
    DUC_MAINNET(NetworkProviderType.DUC),
    DUC_SAVE(NetworkProviderType.DUC);

    public final static String DAPS_MAINNET_VALUE = "DAPS_MAINNET";
    public final static String DUC_MAINNET_VALUE = "DUC_MAINNET";
    public final static String DUC_SAVE_VALUE = "DUC_SAVE";


    private final NetworkProviderType networkProviderType;

    NetworkType(NetworkProviderType networkProviderType) {
        this.networkProviderType = networkProviderType;
    }

}
