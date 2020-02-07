package io.mywish.scanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Tx {
    String txId;
    Vout[] vout;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Vout {
        @Setter
        @Getter
        ScriptPubKey scriptPubKey;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScriptPubKey {
        @Setter
        @Getter
        String[] addresses;
    }
}
