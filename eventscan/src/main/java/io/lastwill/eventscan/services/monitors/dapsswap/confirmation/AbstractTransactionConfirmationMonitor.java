package io.lastwill.eventscan.services.monitors.dapsswap.confirmation;

import io.lastwill.eventscan.model.AbstractTransactionEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.AbstractTransactionEntryRepository;
import io.lastwill.eventscan.services.monitors.dapsswap.SingleNetworkMonitor;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractTransactionConfirmationMonitor<T extends AbstractTransactionEntry>
        extends SingleNetworkMonitor {
    protected final AbstractTransactionEntryRepository<T> entryRepository;

    public AbstractTransactionConfirmationMonitor(
            NetworkType network,
            AbstractTransactionEntryRepository<T> entryRepository
    ) {
        super(network);
        this.entryRepository = entryRepository;
    }

    @Override
    protected void processBlockEvent(NewBlockEvent newBlockEvent) {
        List<String> txHashes = getTxHashes(newBlockEvent);
        entryRepository
                .findAllByTransferStatus(TransferStatus.WAIT_FOR_CONFIRM)
                .stream()
                .filter(entry -> txHashes.contains(entry.getTxHash()))
                .forEach(entry -> {
                    log.info("{}: Transaction {} confirmed", this.getClass().getSimpleName(), entry.getTxHash());
                    entry.setTransferStatus(TransferStatus.OK);
                    entryRepository.save(entry);
                    processEntryAfterConfirm(entry);
                });
    }

    protected void processEntryAfterConfirm(T entry) {
    }
}
