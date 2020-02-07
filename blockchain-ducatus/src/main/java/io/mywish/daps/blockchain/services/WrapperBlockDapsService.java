package io.mywish.daps.blockchain.services;

import com.neemre.btcdcli4j.core.domain.Block;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockDapsService {
    @Autowired
    private WrapperTransactionDapsService transactionBuilder;

    public WrapperBlock build(Block block, Long height) {
        String hash = block.getHash();
        Instant timestamp = Instant.ofEpochSecond(block.getTime());
        List<WrapperTransaction> transactions = block
                .getTx()
                .stream()
                .map(transactionBuilder::build)
                .collect(Collectors.toList());
        return new WrapperBlock(hash, height, timestamp, transactions);
    }
}
