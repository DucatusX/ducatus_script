package io.lastwill.eventscan.services.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.mywish.scanner.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FillAddressFromCli {
    @Autowired
    BtcdClient client;

    @Autowired
    private DucatusTransitionEntryRepository repository;

//    @PostConstruct
//    public void fillAddresses() {
//
//        DucatusAddressInfo ducatusAddressInfo = null;
//        Set<String> addresses = new HashSet<>();
//        try {
//            int lastBlock = mapper.readValue(new URL(URI_INFO), Inf.class).getInfo().getBlocks();
//            for (int i = 1; i < lastBlock; i++) {
//                String blockHash = mapper.readValue(new URL(URI_BLOCK_INDEX.concat(String.valueOf(i))), BlockIndex.class).getBlockHash();
//                String[] tx = mapper.readValue(new URL(URI_BLOCK.concat(blockHash)), Block.class).getTx();
//                if (tx == null) {
//                    log.info("tx is null on {} block", i);
//                    continue;
//                }
//                for (String s : tx) {
//                    Tx map = mapper.readValue(new URL(URI_TX.concat(s)), Tx.class);
//                    Tx.Vout[] vouts = map.getVout();
//                    if (vouts == null) {
//                        log.info("vout is null on {} block, tx = {}", i, tx);
//                        continue;
//                    }
//                    log.info("{} block have {} vout", i, vouts.length);
//                    for (Tx.Vout vout : vouts) {
//                        Tx.ScriptPubKey tempScript = vout.getScriptPubKey();
//                        if (tempScript == null) {
//                            continue;
//                        }
//                        String[] addressesTemp = tempScript.getAddresses();
//                        if (addressesTemp == null || addressesTemp.length == 0) {
//                            continue;
//                        }
//                        addresses.addAll(Arrays.asList(addressesTemp));
//                        log.info("addresses size is {}", addresses.size());
//                        if (addresses.size() % 100 == 0) {
//                            saveAddresses(addresses);
//                        }
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        saveAddresses(addresses);
//    }

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
