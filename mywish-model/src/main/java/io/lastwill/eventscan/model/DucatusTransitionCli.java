package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@Getter
@Setter
@Entity
@Table(name = "ducatus_cli")
public class DucatusTransitionCli extends AbstractTransactionEntry {

    private BigInteger amount;
    @Column(name = "address", unique = true)
    private String address;

    protected DucatusTransitionCli() {
        super(TransferStatus.WAIT_FOR_SEND);
    }

    public DucatusTransitionCli(BigInteger amount) {
        this();
        this.amount = amount;
    }

    public DucatusTransitionCli(String address) {
        this();
        this.address = address;
    }
}