package io.mywish.daps.blockchain.services;

import io.mywish.blockchain.WrapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class WrapperTransactionDapsService {
    public WrapperTransaction build(String txId) {
        return new WrapperTransaction(
                txId,
                Collections.emptyList(),
                Collections.emptyList(),
                false
        );
    }
}
