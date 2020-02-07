package io.lastwill.eventscan.services.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Component
public class FillBalanceCli {
    @Autowired
    private DucatusTransitionEntryRepository repository;
    @Autowired
    private BtcdClient client;

    public void fillBalances() {
        List<DucatusTransitionEntry> entries = repository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        entries.forEach(e -> {
            String address = e.getAddress();
            BigInteger amount = client.getAddressBalance(address);
            if (amount != null) {
                e.setAmount(amount);
                repository.save(e);
                log.info("on address {} balance = {}. Save!", address, amount);
            } else {
                log.warn("Can't read balance by address {}, balacne is null", address);
            }
        });
        log.debug("FILL ADDRESSES BY BALANCES COMPLETED");
    }
}
