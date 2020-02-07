package io.lastwill.eventscan.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class AbstractTransactionEntry extends AbstractPersistable {
    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    public AbstractTransactionEntry(String txHash) {
        this.txHash = txHash;
    }

    public AbstractTransactionEntry(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }
}
