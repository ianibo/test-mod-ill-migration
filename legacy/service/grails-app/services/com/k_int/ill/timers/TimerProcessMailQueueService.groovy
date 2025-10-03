package com.k_int.ill.timers;

import com.k_int.ill.EmailService;
import com.k_int.ill.IllApplicationEventHandlerService;
import com.k_int.ill.MailQueue;
import com.k_int.institution.Institution;

/**
 * Processes the mail queue, sending any emails if there any to be sent
 *
 * @author Chas
 *
 */
public class TimerProcessMailQueueService extends AbstractTimer {

    EmailService emailService;
	IllApplicationEventHandlerService illApplicationEventHandlerService;

    @Override
    public void performTask(String tenant, Institution institution, String config) {
        log.debug("Processing emails waiting to be sent");
		MailQueue.findAll().each { MailQueue mailQueueItem ->
			try {
				// Set up the mail parameters
				Map emailParams = [
					notificationId: mailQueueItem.id,
					to: mailQueueItem.recipient,
					header: mailQueueItem.subject,
					body: mailQueueItem.body,
					outputFormat: mailQueueItem.format
				];

				// Now try and send the email
				log.debug("Mail queue - Sending Email to \"" + emailParams.recipient + "\"");
				emailService.sendEmail(emailParams);

				// Do we have a patron request
				if (mailQueueItem.patronRequest != null) {
					// We do, so add an audit record to say the email has been sent
					illApplicationEventHandlerService.auditEntry(
						mailQueueItem.patronRequest,
						mailQueueItem.patronRequest.state,
						mailQueueItem.patronRequest.state,
						"Mail sent to \"" + mailQueueItem.recipient + "\"",
						null
					);

					// Not forgetting to save the audit entry					
					mailQueueItem.patronRequest.save(flush: true);
				}

				// Now delete this item, should we keep a history of what we have sent
				mailQueueItem.delete(flush: true);
			} catch (Exception e) {
				log.error("Exception thrown while trying to send an email to \"" + mailQueueItem.recipient + "\"", e);
			}
		}
        log.debug("Finished processing emails waiting to be sent");
    }
}
