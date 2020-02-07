package io.lastwill.eventscan.services.saver;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TransitionSaver implements DbSaver<DucatusTransitionEntry> {
    @Autowired
    private DucatusTransitionEntryRepository transitionRepository;

    @Override
    public synchronized DucatusTransitionEntry save(DucatusTransitionEntry entry) {
        DucatusTransitionEntry alreadyWaitEntry = transitionRepository
                .findFirstByTransferStatus(
                        TransferStatus.WAIT_FOR_SEND);
        if (alreadyWaitEntry != null) {
            alreadyWaitEntry.setTransferStatus(TransferStatus.DROPPED);
            entry.setAmount(entry.getAmount().add(alreadyWaitEntry.getAmount()));
            transitionRepository.save(alreadyWaitEntry);
        }
        return transitionRepository.save(entry);
    }
}
