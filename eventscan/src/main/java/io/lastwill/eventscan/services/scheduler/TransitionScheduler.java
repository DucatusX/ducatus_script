package io.lastwill.eventscan.services.scheduler;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.lastwill.eventscan.utils.CurrencyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Slf4j
//@Component
public class TransitionScheduler {
    private static final BigInteger TRANSFER_FEE = BigInteger.valueOf(100);

    @Autowired
    private DucatusTransitionEntryRepository transitionRepository;

    @Autowired
    private BtcdClient btcdClient;

//    @EventListener(NewBlockEvent.class)
    public synchronized void sendToAllWaitingTransitions() throws InterruptedException {
        log.info("start transition function");

        List<DucatusTransitionEntry> waitingForSend = transitionRepository
                .findAllByTransferStatus(TransferStatus.WAIT_FOR_SEND);
        int count = 0;
        for (DucatusTransitionEntry transitionEntry : waitingForSend) {
            send(transitionEntry);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            count++;
            if (count >= 8) {
                break;
            }
        }
    }

    private void send(DucatusTransitionEntry transitionEntry) {
        if (!checkTransition(transitionEntry)) return;

        String address = transitionEntry.getAddress();
        BigDecimal amount = new BigDecimal(transitionEntry.getAmount());
        try {
            log.info("Trying to send {} DAPS to {}", amount, address);
            String txHash = btcdClient.sendToAddress(address, amount);
            transitionEntry.setTransferStatus(TransferStatus.WAIT_FOR_CONFIRM);
            transitionEntry.setTxHash(txHash);
            log.info("DAPS coins transferred for {}: {}", transitionEntry.getAddress(), txHash);
        } catch (BitcoindException e) {
            transitionEntry.setTransferStatus(TransferStatus.ERROR);
        } catch (CommunicationException e) {
            log.warn("Communication exception", e);
        }

        transitionRepository.save(transitionEntry);
    }

    private boolean checkTransition(DucatusTransitionEntry transitionEntry) {

        BigInteger amount = CurrencyUtil.convertEthToDaps(transitionEntry.getAmount());

        if (amount.equals(BigInteger.ZERO)) {
            log.warn("Zero transition amount");
            return false;
        }

        BigInteger balance;
        try {
            balance = getBalance();
        } catch (BitcoindException | CommunicationException e) {
            log.warn("Bitcoind library exception when getting balance", e);
            return false;
        }

        BigInteger need = amount.add(TRANSFER_FEE);
        if (balance.compareTo(need) < 0) {
            log.warn("Insufficient balance: {}, but needed {}", balance, need);
            return false;
        }

        return true;
    }

    private BigInteger getBalance() throws BitcoindException, CommunicationException {
        return btcdClient.getBalance()
                .multiply(BigDecimal.TEN.pow(8))
                .toBigInteger();
    }
}
