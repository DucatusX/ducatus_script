package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.TransferStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface DucatusTransitionEntryRepository extends AbstractTransactionEntryRepository<DucatusTransitionEntry> {
    @Transactional
    @Query("update DucatusTransitionEntry e set e.txHash = :txHash")
    boolean setTxHash(@Param("txHash") String txHash);

    List<DucatusTransitionEntry> findAll();

    List<DucatusTransitionEntry> findByTransferStatusEquals(
            @Param("transferStatus") TransferStatus status);

    List<DucatusTransitionEntry> findAllByAmountIsNull();

    DucatusTransitionEntry findFirstByTransferStatus(@Param("transferStatus") TransferStatus status);

    @Query("select c from DucatusTransitionEntry c where c.address in :addresses")
    List<DucatusTransitionEntry> findByAddressesList(@Param("addresses") Collection<String> addresses);
}
