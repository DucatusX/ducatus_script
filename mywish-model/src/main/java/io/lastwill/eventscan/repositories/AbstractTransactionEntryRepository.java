package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.AbstractTransactionEntry;
import io.lastwill.eventscan.model.DucatusTransitionCli;
import io.lastwill.eventscan.model.TransferStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

@NoRepositoryBean
public interface AbstractTransactionEntryRepository<T extends AbstractTransactionEntry>
        extends CrudRepository<T, Long> {
    List<T> findAllByTransferStatus(@Param("transferStatus") TransferStatus status);

    boolean existsByTransferStatus(@Param("transferStatus") TransferStatus transferStatus);

    List<T> findByAmountIsNull();

}
