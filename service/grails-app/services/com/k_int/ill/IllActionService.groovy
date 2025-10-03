package com.k_int.ill;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.k_int.ill.patronStore.PatronStoreActions;
import com.k_int.ill.statemodel.EventResultDetails;
import com.k_int.institution.Institution;

/**
 * Handle user events.
 *
 * whereas IllApplicationEventHandlerService is about detecting and handling
 * system generated events - incoming protocol messages etc this class is the
 * home for user triggered activities - checking an item into ill, marking
 * the pull slip as printed etc.
 */
public class IllActionService {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd[ ]['T']HH:mm[:ss][.SSS][z][XXX][Z]";

    HostLmsPatronProfileService hostLmsPatronProfileService;
    HostLmsService hostLmsService;
    IllApplicationEventHandlerService illApplicationEventHandlerService;
    PatronStoreService patronStoreService;

    /**
     * Looks up a patron identifier to see if it is valid for requesting or not
     * @param institution The institution the call is being made for
     * @param request the patron request that any auditing should be associated with
     * @param actionParams the parameters that we use to make our decision
     *      patronIdentifier ... the id to be checked
     *      override ... if the patron turns out to be invalid, this allows us to say they are valid
     * @return a map containing the result of the call that can contain the following fields:
     *      callSuccess ... was the call a success or not
     *      patronDetails ... the details of the patron if the patron is a valid user
     *      patronValid ... can the patron create requests
     *      problems ... An array of reasons that explains either a FAIL or the patron is not valid
     *      status ... the status of the patron (FAIL or OK)
     *
     */
    private Map lookupPatronInternal(Institution institution, PatronRequest request, Map actionParams) {
        // The result object
        Map result = [callSuccess: false, patronValid: false ];
        Map patronDetails = hostLmsService.lookupPatron(institution, request, actionParams.patronIdentifier);
        if (patronDetails != null) {
            if (patronDetails.result || actionParams.override) {
                result.callSuccess = true;

                // Set the institution on the patron details
                patronDetails.institution = request?.institution;

                // Ensure the patron details has a user id
                if (patronDetails.userid == null) {
                    patronDetails.userid = actionParams.patronIdentifier;
                }

                // Check the patron profile and record if we have not seen before
                HostLMSPatronProfile patronProfile = null
                if (patronDetails.userProfile != null) {
                    patronProfile = hostLmsPatronProfileService.ensureActive(request.institution, patronDetails.userProfile, patronDetails.userProfile);
                }

                // Is it a valid patron or are we overriding the fact it is valid
                if (isValidPatron(patronDetails, patronProfile) || actionParams.override) {
                    result.patronValid = true;
                }
            }

            // If there are problems with the patron let the caller know
            if (patronDetails.problems) {
                result.problems = patronDetails.problems.toString();
            }

            // Set the status in the result
            result.status = patronDetails.status;
        }

        // Let the caller know the patron details
        result.patronDetails = patronDetails;

        return(result);
    }

    /**
     * Looks up the patron without an active request
     * @param institution The institution the call is being made for
     * @param actionParams The parameters required for the lookup
     * @return a Map containing the result of the lookup
     */
    public Map lookupPatron(Institution institution, Map actionParams) {
        return(lookupPatronInternal(institution, null, actionParams));
    }

    /*
     * WARNING: this method is NOT responsible for saving or for managing state
     * changes. It simply performs the lookupAction and appends relevant info to the
     * patron request
     */
    public Map lookupPatron(PatronRequest pr, Map actionParams) {
        // Ensure actionParams exists as an object
        Map params = actionParams;
        if (params == null) {
            // Allocate an empty object so we can set the patronIdentifier
            params = [ : ];
        }

        // before we call lookupPatron we need to set the patronIdentifier on the actionParams
        params.patronIdentifier = pr.patronIdentifier;
        Map result = lookupPatronInternal(pr.institution, pr, params);

        if (result.patronDetails != null) {
            if (result.patronDetails.userid != null) {
                pr.resolvedPatron = lookupOrCreatePatronProxy(pr.institution, result.patronDetails);
                if (pr.patronSurname == null) {
                    pr.patronSurname = result.patronDetails.surname;
                }
                if (pr.patronGivenName == null) {
                    pr.patronGivenName = result.patronDetails.givenName;
                }
                if (pr.patronEmail == null) {
                    pr.patronEmail = result.patronDetails.email;
                }
            }

            // Is the patron is valid, add an audit entry
            if (result.patronValid) {
                String reason = result.patronDetails.reason == 'spoofed' ? '(No host LMS integration configured for borrower check call)' : 'Host LMS integration: borrower check call succeeded.';
                String outcome = actionParams?.override ? 'validation overriden' : 'validated';
                String message = "Patron ${outcome}. ${reason}";
                illApplicationEventHandlerService.auditEntry(pr, pr.state, pr.state, message, null);
            }
        }

        // Do not pass the actual patron details back
        result.remove('patronDetails');
        return(result);
    }

    public boolean sendSupplierCancelResponse(PatronRequest pr, Map actionParams, EventResultDetails eventResultDetails) {
        /* This method will send a cancellation response iso18626 message */

        log.debug("sendSupplierCancelResponse(${pr})");
        boolean result = false;
        String status;

        if (!actionParams.get('cancelResponse') != null) {
            switch (actionParams.cancelResponse) {
                case 'yes':
                    status = com.k_int.ill.iso18626.codes.closed.Status.CANCELLED;
                    break;

                case 'no':
                    break;

                default:
                    log.warn("sendSupplierCancelResponse received unexpected cancelResponse: ${actionParams.cancelResponse}")
                    break;
            }

            // Only the supplier should ever be able to send one of these messages, otherwise something has gone wrong.
            if (pr.isRequester == false) {
				eventResultDetails.sendProtocolMessage = true;
//                result = sendSupplyingAgencyMessage(pr, ReasonForMessage.MESSAGE_REASON_CANCEL_RESPONSE, status, actionParams, eventResultDetails);
            } else {
                log.warn('The requesting agency should not be able to call sendSupplierConditionalWarning.');
            }
        } else {
            log.error('sendSupplierCancelResponse expected to receive a cancelResponse');
        }

        return result;
    }

    public Map requestingAgencyMessageSymbol(PatronRequest request) {
        Map symbols = [ senderSymbol: request.requestingInstitutionSymbol ];

        PatronRequestRota patronRota = request.rota.find({ rotaLocation -> rotaLocation.rotaPosition == request.rotaPosition });
        if (patronRota != null) {
            symbols.receivingSymbol = "${patronRota.peerSymbol.authority.symbol}:${patronRota.peerSymbol.symbol}".toString();
        }
        return(symbols);
    }

    protected Date parseDateString(String dateString, String dateFormat = DEFAULT_DATE_FORMAT) {
        Date date;

        log.debug("Attempting to parse input date string '${dateString}' with format string '${dateFormat}'")

        if (dateString == null) {
            throw new Exception('Attempted to parse null as date')
        }

        // Ensure we have a date format
        String dateFormatToUse = dateFormat;
        if (!dateFormatToUse?.trim()) {
            // It is null or is all whitespace, so use the default format
            dateFormatToUse = DEFAULT_DATE_FORMAT;
        }

        // If the format is less than 12 characters we assume it is just a date and has no time
        if (dateFormatToUse.length() < 12) {
            // Failed miserably to just convert a date without time elements using LocalDate, ZonedDateTime or LocalDateTime
            // So have fallen back on the SimpleDateFormat
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatToUse);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
            date = simpleDateFormat.parse(dateString);
        } else {
            // See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
            // for the appropriate patterns
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatToUse);

            try {
                date = Date.from(ZonedDateTime.parse(dateString, formatter).toInstant());
            } catch (Exception e) {
                log.debug("Failed to parse ${dateString} as ZonedDateTime, falling back to LocalDateTime");
                date = Date.from(LocalDateTime.parse(dateString, formatter).toInstant(ZoneOffset.UTC));
            }
        }
        return date
    }

    private Patron lookupOrCreatePatronProxy(Institution institution, Map patronDetails) {
        Patron result = null;
        PatronStoreActions patronStoreActions;
        patronStoreActions = patronStoreService.getPatronStoreActions(institution);
        log.debug("patronStoreService is currently ${patronStoreService}");
        try {
            patronStoreActions.updateOrCreatePatronStore(institution, patronDetails.userid, patronDetails);
        } catch (Exception e) {
            log.error("Unable to update or create Patron Store: ${e}");
        }

        if ((patronDetails != null) &&
            (patronDetails.userid != null) &&
            (patronDetails.userid.trim().length() > 0)) {
            result = Patron.findByHostSystemIdentifierAndInstitution(patronDetails.userid, patronDetails.institution);
            if (result == null) {
                result = new Patron(
                    hostSystemIdentifier: patronDetails.userid,
                               givenname: patronDetails.givenName,
                                 surname: patronDetails.surname,
                             userProfile: patronDetails.userProfile,
                             institution: patronDetails.institution
                );
            } else {
                // update the patron record
                result.givenname = patronDetails.givenName;
                result.surname = patronDetails.surname;
                result.userProfile = patronDetails.userProfile;
            }

            // We have either created or updated, so save it now
            result.save(flush:true, failOnError:true);
        }
        return result;
    }

    private boolean isValidPatron(Map patronRecord, HostLMSPatronProfile patronProfile) {
        boolean result = false;
        log.debug("Check isValidPatron: ${patronRecord}");
        if (patronRecord != null) {
            /*
             *  They can request if
             * 1. It is a valid patron record (status = OK)
             * 2. There is no patron profile or the canCreateRequests field is not set or true
             */
            if (patronRecord.status == 'OK') {
                if ((patronProfile == null) ||
                    (patronProfile.canCreateRequests == null) ||
                    (patronProfile.canCreateRequests == true)) {
                    result = true;
                } else {
                    patronRecord.problems = ["Patron profile (${patronProfile.code}) is configured to not allow requesting in ill."];
                }
            } else if (patronRecord.problems == null) {
                patronRecord.problems = ['Record status is not valid.'];
            }
        }
        return result;
    }
}
