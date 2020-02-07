package io.lastwill.eventscan.services.monitors.dapsswap;

import io.lastwill.eventscan.model.NetworkType;
import org.springframework.stereotype.Component;

@Component
public class DucatusScanAddressMonitor extends AbstractScanAddressMonitor {
    protected DucatusScanAddressMonitor() {
        super(NetworkType.DUC_MAINNET);
    }
}