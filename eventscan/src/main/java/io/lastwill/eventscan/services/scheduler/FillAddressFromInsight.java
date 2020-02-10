package io.lastwill.eventscan.services.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.scanner.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
//@Component
public class FillAddressFromInsight {
    @Autowired
    BtcdClient btcdClient;
    @Autowired
    private DucatusTransitionEntryRepository repository;
    @Autowired
    FillBalanceCli balanceFiller;
    @Autowired
    private LastBlockRepository blockRepository;

    long timerToSleep = 10000;
    long timerMaxValue = 4800000;

    private final String URI_INFO = "https://oldins.ducatus.io/insight-lite-api/status?q=getInfo";
    private final String URI_BLOCK_INDEX = "https://oldins.ducatus.io/insight-lite-api/block-index/";
    private final String URI_BLOCK = "https://oldins.ducatus.io/insight-lite-api/block/";
    private final String URI_TX = "https://oldins.ducatus.io/insight-lite-api/tx/";

    //    @PostConstruct
    public void fillAddresses() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> addresses = new HashSet<>();
        try {
            long startBlock = blockRepository.getLastBlockForNetwork(NetworkType.DUC_SAVE);
            if (startBlock == 0) {
                startBlock = 1;
            }
            int lastBlock = mapper.readValue(new URL(URI_INFO), Inf.class).getInfo().getBlocks();
            for (int i = (int) startBlock; i < lastBlock; i++) {
                String blockHash = mapper.readValue(new URL(URI_BLOCK_INDEX.concat(String.valueOf(i))), BlockIndex.class).getBlockHash();
                String[] tx = mapper.readValue(new URL(URI_BLOCK.concat(blockHash)), Block.class).getTx();
                if (tx == null) {
                    log.info("tx is null on {} block", i);
                    continue;
                }
                for (String s : tx) {
                    Tx map = mapper.readValue(new URL(URI_TX.concat(s)), Tx.class);
                    Tx.Vout[] vouts = map.getVout();
                    if (vouts == null) {
                        log.info("vout is null on {} block, tx = {}", i, tx);
                        continue;
                    }
                    log.info("{} block have {} vout", i, vouts.length);
                    for (Tx.Vout vout : vouts) {
                        Tx.ScriptPubKey tempScript = vout.getScriptPubKey();
                        if (tempScript == null) {
                            continue;
                        }
                        String[] addressesTemp = tempScript.getAddresses();
                        if (addressesTemp == null || addressesTemp.length == 0) {
                            continue;
                        }
                        addresses.addAll(Arrays.asList(addressesTemp));
                        log.info("addresses size is {}", addresses.size());
                        if (addresses.size() % 100 == 0 && addresses.size() > 0) {
                            saveAddresses(addresses);
                            addresses.clear();
                        }
                    }
                }
                blockRepository.updateLastBlock(NetworkType.DUC_SAVE, (long) i);
                Thread.sleep(1000);
            }
        } catch (IOException ex) {
            if (ex.getMessage().contains("429")) {
                try {
                    Thread.sleep(timerToSleep);
                    String errMessage = getErrMessage();
                    log.error(errMessage);
                    if (timerToSleep > timerMaxValue) {
                        timerToSleep = timerMaxValue;
                    } else if (timerToSleep < timerMaxValue) {
                        timerToSleep *= 2;
                    }
                    fillAddresses();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        saveAddresses(addresses);
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
        balanceFiller.fillBalances();
    }

    String getErrMessage() {
        double hours = timerToSleep / 1000 / 60 / 60;
        double minutes = timerToSleep / 1000 / 60 % 60;
        double seconds = timerToSleep / 1000 % 60;
        return String.format("Exception, error 429, wait for %.0f:%.0f:%.0f", hours, minutes, seconds);
    }
}
