package io.lastwill.eventscan.services.monitors.dapsswap;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;

public abstract class SingleNetworkMonitor extends AbstractMonitor {
    private final NetworkType network;

    public SingleNetworkMonitor(NetworkType network) {
        this.network = network;
    }

    @Override
    protected boolean checkCondition(NewBlockEvent newBlockEvent) {
        return newBlockEvent.getNetworkType() == this.network;
    }
}
