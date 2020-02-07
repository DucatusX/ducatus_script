package io.lastwill.eventscan.services.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.mywish.scanner.model.DucatusAddressInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;

@Slf4j
//@Component
public class FillBalanceFromInsight {
    @Autowired
    private DucatusTransitionEntryRepository repository;
    private final String URI_PREFIX = "https://oldins.ducatus.io/insight-lite-api/addr/";
    private final String URI_POSTFIX = "";


    //    @PostConstruct
    public void fillBalances() {
        List<DucatusTransitionEntry> entries = repository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        entries.forEach(e -> {
            String address = e.getAddress();
            DucatusAddressInfo ducatusAddressInfo = null;
            try {
                ducatusAddressInfo = objectMapper.readValue(new URL(URI_PREFIX.concat(address).concat(URI_POSTFIX)), DucatusAddressInfo.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (ducatusAddressInfo != null) {
                e.setAmount(new BigInteger(ducatusAddressInfo.getTotalReceivedSat()));
                repository.save(e);
            } else {
                log.warn("Can't read balance by address {}, ducatusAddressInfo is null", address);
            }
        });
        log.debug("FILL ADDRESSES BY BALANCES COMPLETED");
    }
}
