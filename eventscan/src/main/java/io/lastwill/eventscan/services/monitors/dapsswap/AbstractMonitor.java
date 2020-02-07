package io.lastwill.eventscan.services.monitors.dapsswap;

import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractMonitor {
    @EventListener
    public void onBlock(NewBlockEvent newBlockEvent) {
        if (checkCondition(newBlockEvent)) {
            processBlockEvent(newBlockEvent);
        }
    }

    protected boolean checkCondition(NewBlockEvent newBlockEvent) {
        return true;
    }

    protected abstract void processBlockEvent(NewBlockEvent newBlockEvent);

    protected Stream<WrapperTransaction> filterTransactionsByAddress(NewBlockEvent blockEvent, String filterAddress) {
        List<WrapperTransaction> filteredTransactions = blockEvent
                .getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> filterAddress.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info("{}: filtered {} transactions.", this.getClass().getSimpleName(), filteredTransactions.size());
        return filteredTransactions.stream();
    }

    protected List<String> getTxHashes(NewBlockEvent newBlockEvent) {
        return newBlockEvent
                .getTransactionsByAddress()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(WrapperTransaction::getHash)
                .distinct()
                .collect(Collectors.toList());
    }
}
