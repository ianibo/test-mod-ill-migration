package com.k_int.data.entity;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor(onConstructor_ = @Creator())
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Serdeable
@MappedEntity(value = "patron_request")
public class PatronRequest {
    @Id
    @Column(name = "pr_id")
    private UUID id;

    @DateCreated
    @Column(name = "pr_date_created")
    private ZonedDateTime dateCreated;

    @DateUpdated
    @Column(name = "pr_last_updated")
    private ZonedDateTime lastUpdated;

    @Column(name = "pr_patron_identifier")
    private String patronIdentifier;

    @Column(name = "pr_patron_reference")
    private String patronReference;

    @Column(name = "pr_is_requester")
    private Boolean isRequester;

    @Column(name = "pr_number_of_retries")
    private Integer numberOfRetries;

    @Column(name = "pr_delay_performing_action_until")
    private ZonedDateTime delayPerformingActionUntil;

    @Column(name = "pr_awaiting_protocol_response")
    private boolean awaitingProtocolResponse;

    @Column(name = "pr_rota_position")
    private Long rotaPosition;

    @Column(name = "pr_title")
    private String title;

    @Column(name = "pr_author")
    private String author;

    @Column(name = "pr_sub_title")
    private String subtitle;

    @Column(name = "pr_sponsoring_body")
    private String sponsoringBody;

    @Column(name = "pr_publisher")
    private String publisher;

    @Column(name = "pr_place_of_pub")
    private String placeOfPublication;

    @Column(name = "pr_volume")
    private String volume;

    @Column(name = "pr_issue")
    private String issue;

    @Column(name = "pr_start_page")
    private String startPage;

    @Column(name = "pr_num_pages")
    private String numberOfPages;

    @Column(name = "pr_pub_date")
    private String publicationDate;

    @Column(name = "pr_pubdate_of_component")
    private String publicationDateOfComponent;

    @Column(name = "pr_edition")
    private String edition;

    @Column(name = "pr_issn")
    private String issn;

    @Column(name = "pr_isbn")
    private String isbn;

    @Column(name = "pr_doi")
    private String doi;

    @Column(name = "pr_coden")
    private String coden;

    @Column(name = "pr_sici")
    private String sici;

    @Column(name = "pr_bici")
    private String bici;

    @Column(name = "pr_eissn")
    private String eissn;

    @Column(name = "pr_oclc_number")
    private String oclcNumber;

    @Column(name = "pr_stitle")
    private String stitle;

    @Column(name = "pr_part")
    private String part;

    @Column(name = "pr_artnum")
    private String artnum;

    @Column(name = "pr_ssn")
    private String ssn;

    @Column(name = "pr_quarter")
    private String quarter;

    @Column(name = "pr_bib_record_id")
    private String bibliographicRecordId;

    @Column(name = "pr_supplier_unique_record_id")
    private String supplierUniqueRecordId;

    @Column(name = "pr_system_instance_id")
    private String systemInstanceIdentifier;

    @Column(name = "pr_selected_item_barcode")
    private String selectedItemBarcode;

    @Column(name = "pr_title_of_component")
    private String titleOfComponent;

    @Column(name = "pr_author_of_component")
    private String authorOfComponent;

    @Column(name = "pr_sponsor")
    private String sponsor;

    @Column(name = "pr_information_source")
    private String informationSource;

    @Column(name = "pr_patron_surname")
    private String patronSurname;

    @Column(name = "pr_patron_name")
    private String patronGivenName;

    @Column(name = "pr_patron_type")
    private String patronType;

    @Column(name = "pr_send_to_patron")
    private Boolean sendToPatron;

    @Column(name = "pr_bib_record")
    private String bibRecord;

    @Column(name = "pr_needed_by")
    private LocalDate neededBy;

    @Column(name = "pr_local_call_number")
    private String localCallNumber;

    @Column(name = "pr_hrid")
    private String hrid;

    @Column(name = "pr_req_inst_symbol")
    private String requestingInstitutionSymbol;

    @Column(name = "pr_sup_inst_symbol")
    private String supplyingInstitutionSymbol;

    @Column(name = "pr_peer_request_identifier")
    private String peerRequestIdentifier;

    @Column(name = "pr_pick_shelving_location")
    private String pickShelvingLocation;

    @Column(name = "pr_patron_email")
    private String patronEmail;

    @Column(name = "pr_patron_note")
    private String patronNote;

    @Column(name = "pr_pref_service_point")
    private String pickupLocation;

    @Column(name = "pr_pref_service_point_code")
    private String pickupLocationCode;

    @Column(name = "pr_pickup_location_slug")
    private String pickupLocationSlug;

    @Column(name = "pr_pickup_url")
    private String pickupURL;

    @Column(name = "pr_active_loan")
    private Boolean activeLoan;

    @Column(name = "pr_due_date_from_lms")
    private String dueDateFromLMS;

    @Column(name = "pr_parsed_due_date_lms")
    private ZonedDateTime parsedDueDateFromLMS;

    @Column(name = "pr_due_date_rs")
    private String dueDateRS;

    @Column(name = "pr_parsed_due_date_rs")
    private ZonedDateTime parsedDueDateRS;

    @Column(name = "pr_overdue")
    private Boolean overdue;

    @Column(name = "pr_needs_attention")
    private Boolean needsAttention;

    @Column(name = "pr_network_status")
    private String networkStatus;

    @Column(name = "pr_last_send_attempt")
    private ZonedDateTime lastSendAttempt;

    @Column(name = "pr_next_send_attempt")
    private ZonedDateTime nextSendAttempt;

    @Column(name = "pr_last_protocol_data")
    private String lastProtocolData;

    @Column(name = "pr_number_of_send_attempts")
    private Integer numberOfSendAttempts;

    @Column(name = "pr_last_sequence_sent")
    private Integer lastSequenceSent;

    @Column(name = "pr_last_sequence_received")
    private Integer lastSequenceReceived;

    @Column(name = "pr_last_audit_no")
    private Integer lastAuditNo;

    @Column(name = "pr_sent_iso18626_request_response")
    private Boolean sentISO18626RequestResponse;

    @Column(name = "pr_request_to_continue")
    private boolean requestToContinue;

    @Column(name = "pr_previous_states")
    private String previousStates;

    @OneToMany(mappedBy = "patronRequest")
    private Set<PatronRequestAudit> audit;

    @ManyToMany(mappedBy = "patronRequests")
    private Set<Batch> batches;

    @OneToMany(mappedBy = "patronRequest")
    private Set<PatronRequestLoanCondition> conditions;

    @OneToMany(mappedBy = "patronRequest")
    private Set<PatronRequestDocument> documents;

    @OneToMany(mappedBy = "patronRequest")
    private Set<PatronRequestNotification> notifications;

    @OneToMany(mappedBy = "patronRequest")
    private Set<ProtocolAudit> protocolAudit;

    @OneToMany(mappedBy = "patronRequest")
    private Set<RequestIdentifier> requestIdentifiers;

    @OneToMany(mappedBy = "patronRequest")
    private Set<PatronRequestRota> rota;

    @OneToMany(mappedBy = "patronRequest")
    private Set<RequestVolume> volumes;
}