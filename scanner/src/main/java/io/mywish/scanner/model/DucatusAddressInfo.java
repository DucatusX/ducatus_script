package io.mywish.scanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DucatusAddressInfo {
    String addrStr;
    String balance;
    String balanceSat;
    String totalReceived;
    String totalReceivedSat;
    String totalSent;
    String totalSentSat;
    String unconfirmedBalance;
    String unconfirmedBalanceSat;
    String unconfirmedTxApperances;
    String txApperances;
}
