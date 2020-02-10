package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.DucatusTransitionCli;
import io.lastwill.eventscan.model.TransferStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface DucatusTransitionCliRepository extends AbstractTransactionEntryRepository<DucatusTransitionCli> {
    @Transactional
    @Query("update DucatusTransitionCli e set e.txHash = :txHash")
    boolean setTxHash(@Param("txHash") String txHash);

    List<DucatusTransitionCli> findAll();

    List<DucatusTransitionCli> findByTransferStatusEquals(
            @Param("transferStatus") TransferStatus status);

    List<DucatusTransitionCli> findAllByAmountIsNull();

    DucatusTransitionCli findFirstByTransferStatus(@Param("transferStatus") TransferStatus status);

    @Query("select c from DucatusTransitionCli c where c.address in :addresses")
    List<DucatusTransitionCli> findByAddressesList(@Param("addresses") Collection<String> addresses);
}
