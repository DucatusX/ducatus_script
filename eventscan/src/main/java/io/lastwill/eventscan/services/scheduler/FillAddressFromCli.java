package io.lastwill.eventscan.services.scheduler;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.RedeemScript;
import io.lastwill.eventscan.model.DucatusTransitionCli;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.DucatusTransitionCliRepository;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FillAddressFromCli {
    private FillBalanceCli balanceCli;
    @Autowired
    BtcdClient client;

    @Autowired
    private DucatusTransitionCliRepository repository;

    @Autowired
    private LastBlockRepository blockRepository;

    public FillAddressFromCli() {
        this.balanceCli = new FillBalanceCli(repository);
    }

    @PostConstruct
    public void fillAddresses() {
        try {
            long startBlock = blockRepository.getLastBlockForNetwork(NetworkType.DUC_SAVE);
            if (startBlock == 0) {
                startBlock = 1;
            }
            Set<String> addresses = new HashSet<>();
            int lastBlock = client.getBlockCount();

            for (int i = (int) startBlock; i < lastBlock; i++) {
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
                if (addresses.size() % 100 == 0 && addresses.size() > 0) {
                    saveAddresses(addresses);
                    addresses.clear();
                }
                blockRepository.updateLastBlock(NetworkType.DUC_SAVE, (long) i);
                Thread.sleep(100);
            }
            saveAddresses(addresses);
        } catch (BitcoindException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveAddresses(Collection addresses) {
        Set<String> temp = new HashSet<>(addresses);
        List<DucatusTransitionCli> entries = repository.findByAddressesList(addresses);
        List<String> repeatAddresses = entries.stream().filter(entry -> addresses.contains(entry.getAddress()))
                .map(DucatusTransitionCli::getAddress)
                .collect(Collectors.toList());
        temp.removeAll(repeatAddresses);

        if (!temp.isEmpty()) {
            temp.forEach(e -> {
                repository.save(new DucatusTransitionCli(e));
            });
        } else {
            log.warn("addresses from insight are empty");
        }
        balanceCli.fillBalances();
    }
}
