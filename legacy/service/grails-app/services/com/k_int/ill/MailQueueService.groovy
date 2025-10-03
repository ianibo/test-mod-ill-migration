package com.k_int.ill;

/**
 * This service does the important things to do with a MailQueue record
 */
public class MailQueueService {

	/**
	 * Queues a mail to be sent
	 * @param recipient The email adress to whom the mail is to be sent
	 * @param subject The subject of the email
	 * @param body The body of the email
	 * @param format The format of the body of the email, defaults to test/html
	 * @return True if we were successful otherwise false
	 */
	public boolean save(
		String recipient,
		String subject,
		String body,
		PatronRequest patronRequest = null,
		String format = "text/html"
	) {
		boolean saved = true;
		MailQueue mailqueueItem = new MailQueue();
		mailqueueItem.recipient = recipient;
		mailqueueItem.subject = subject;
		mailqueueItem.body = body;
		mailqueueItem.format = format;
		mailqueueItem.patronRequest = patronRequest;
		
		// Save the record		
		saved = (mailqueueItem.save(flush: true) != null);

		// Were we successful
		if (saved) {
			// We were
			log.debug("EMail message queued for " + recipient + " with subject \"" + subject + "\"");
		} else {
			// We did not succeed, so log this fact
			log.error("Failed to save record to Mail queue, errors: " + mailqueueItem.errors.toString() + "\nRecord:\n" + mailqueueItem.toString());
		}

		// Let the caller know if we were successful or not
		return(saved);
	}
}
