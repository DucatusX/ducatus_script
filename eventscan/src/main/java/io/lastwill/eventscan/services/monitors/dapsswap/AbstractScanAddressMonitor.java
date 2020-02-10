package io.lastwill.eventscan.services.monitors.dapsswap;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.lastwill.eventscan.services.scheduler.FillBalanceMonitor;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractScanAddressMonitor {
    private final NetworkType networkType;
    @Autowired
    private FillBalanceMonitor fillBalanceMonitor;

    @Autowired
    private DucatusTransitionEntryRepository transitionEntryRepository;

    protected AbstractScanAddressMonitor(NetworkType networkType) {
        this.networkType = networkType;
    }

    @EventListener
    public void onNewBlockEvent(NewBlockEvent event) {
        // payments only in mainnet works
        if (event.getNetworkType() != networkType) {
            return;
        }

        Set<String> blockAddresses = event.getTransactionsByAddress().keySet();

        if (blockAddresses.isEmpty()) {
            return;
        }

        List<DucatusTransitionEntry> ethAddresses = transitionEntryRepository.findByAddressesList(blockAddresses);

        List<String> repeatAddresses = ethAddresses.stream().filter(entry -> blockAddresses.contains(entry.getAddress()))
                .map(DucatusTransitionEntry::getAddress)
                .collect(Collectors.toList());

        blockAddresses.removeAll(repeatAddresses);
        if (blockAddresses.isEmpty()) {
            log.debug("{}. Nothing to save in {} block.", networkType, event.getBlock().getNumber());
            return;
        }
        List<DucatusTransitionEntry> addressesToSave = new ArrayList<>();
        blockAddresses.forEach(e -> addressesToSave.add(new DucatusTransitionEntry(e)));
        transitionEntryRepository.save(addressesToSave);
        log.debug("{}. Save {} addresses in {} block.", networkType, addressesToSave.size(), event.getBlock().getNumber());
        if (event.getBlock().getNumber() % 100 == 0) {
            fillBalanceMonitor.fillBalances();
        }
    }
}
