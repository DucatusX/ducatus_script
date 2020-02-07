package io.lastwill.eventscan.services.scheduler;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.RedeemScript;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FillAddressFromCli {
    @Autowired
    BtcdClient client;

    @Autowired
    private DucatusTransitionEntryRepository repository;

    @PostConstruct
    public void fillAddresses() throws BitcoindException, CommunicationException {

        Set<String> addresses = new HashSet<>();
        int lastBlock = client.getBlockCount();
        for (int i = 1; i < lastBlock; i++) {
            String hash = client.getBlockHash(i);
            Block block = client.getBlock(hash);
            List<String> txs = block.getTx();
            if (txs == null || txs.isEmpty()) {
                log.info("tx is null on {} block", i);
                continue;
            }
            for (String tx : txs) {
                RedeemScript script = client.decodeScript(tx);
                if (script == null || script.getP2sh() == null || script.getP2sh().isEmpty()) {
                    log.info("address is empty  on {} block tx {}", i, tx);
                    continue;
                }
                String address = script.getP2sh();
                addresses.add(address);
            }
            log.info("addresses size is {} on block {}", addresses.size(), i);
            if (addresses.size() % 10000 == 0) {
                saveAddresses(addresses);
            }
            saveAddresses(addresses);
        }
    }

    private void saveAddresses(Collection addresses) {
        Set<String> temp = new HashSet<>(addresses);
        List<DucatusTransitionEntry> entries = repository.findByAddressesList(addresses);
        List<String> repeatAddresses = entries.stream().filter(entry -> addresses.contains(entry.getAddress()))
                .map(DucatusTransitionEntry::getAddress)
                .collect(Collectors.toList());
        temp.removeAll(repeatAddresses);

        if (!temp.isEmpty()) {
            temp.forEach(e -> {
                repository.save(new DucatusTransitionEntry(e));
            });
        } else {
            log.warn("addresses from insight are empty");
        }
    }
}
