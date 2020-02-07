package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigInteger;

@Getter
@Setter
@Entity
@Table(name = "ducatus_transition")
public class DucatusTransitionEntry extends AbstractTransactionEntry {

    private BigInteger amount;
    @Column(name = "address", unique = true)
    private String address;

    protected DucatusTransitionEntry() {
        super(TransferStatus.WAIT_FOR_SEND);
    }

    public DucatusTransitionEntry(BigInteger amount) {
        this();
        this.amount = amount;
    }

    public DucatusTransitionEntry(String address) {
        this();
        this.address = address;
    }
}
