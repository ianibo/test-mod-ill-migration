package com.k_int.ill.timers;

import com.k_int.ill.NetworkStatus;
import com.k_int.ill.PatronRequest;

/**
 * Checks to see if There are any requests we need to validate that were received and if they were not, mark it as a resend
 *
 * @author Chas
 *
 */
public class TimerRequestNetworkTimeoutService extends AbstractTimerRequestNetworkService {

    TimerRequestNetworkTimeoutService() {
        super(NetworkStatus.Timeout);
    }

    @Override
    public void performNetworkTask(PatronRequest request) {
        // Attempt to send the message again
		// should attempt to check if the message was received in the first place, otherwise the message maybe received twice
		protocolService.sendMessage(request);
    }
}
