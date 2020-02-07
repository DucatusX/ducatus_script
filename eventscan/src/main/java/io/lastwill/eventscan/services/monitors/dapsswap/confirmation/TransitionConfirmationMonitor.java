package io.lastwill.eventscan.services.monitors.dapsswap.confirmation;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import org.springframework.stereotype.Component;

@Component
public class TransitionConfirmationMonitor extends AbstractTransactionConfirmationMonitor<DucatusTransitionEntry> {
    public TransitionConfirmationMonitor(DucatusTransitionEntryRepository transitionRepository) {
        super(NetworkType.DAPS_MAINNET, transitionRepository);
    }
}
