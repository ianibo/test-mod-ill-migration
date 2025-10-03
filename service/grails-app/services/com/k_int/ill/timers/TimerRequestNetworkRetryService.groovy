package com.k_int.ill.timers;

import com.k_int.ill.NetworkStatus;
import com.k_int.ill.PatronRequest;

/**
 * Checks to see if There are any requests we need to retry
 *
 * @author Chas
 *
 */
public class TimerRequestNetworkRetryService extends AbstractTimerRequestNetworkService {

    TimerRequestNetworkRetryService() {
        super(NetworkStatus.Retry);
    }

    @Override
    public void performNetworkTask(PatronRequest request) {
        // Now lets us attempt to resend
		protocolService.sendMessage(request);
    }
}
