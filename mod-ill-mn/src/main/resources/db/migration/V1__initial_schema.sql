CREATE TABLE patron_request (
    pr_id UUID PRIMARY KEY,
    pr_date_created TIMESTAMP WITH TIME ZONE,
    pr_last_updated TIMESTAMP WITH TIME ZONE,
    pr_patron_identifier VARCHAR(255),
    pr_patron_reference VARCHAR(255),
    pr_is_requester BOOLEAN,
    pr_number_of_retries INTEGER,
    pr_delay_performing_action_until TIMESTAMP WITH TIME ZONE,
    pr_awaiting_protocol_response BOOLEAN NOT NULL,
    pr_rota_position BIGINT,
    pr_title VARCHAR(255),
    pr_author VARCHAR(255),
    pr_sub_title VARCHAR(255),
    pr_sponsoring_body VARCHAR(255),
    pr_publisher VARCHAR(255),
    pr_place_of_pub VARCHAR(255),
    pr_volume VARCHAR(255),
    pr_issue VARCHAR(255),
    pr_start_page VARCHAR(255),
    pr_num_pages VARCHAR(255),
    pr_pub_date VARCHAR(255),
    pr_pubdate_of_component VARCHAR(255),
    pr_edition VARCHAR(255),
    pr_issn VARCHAR(255),
    pr_isbn VARCHAR(255),
    pr_doi VARCHAR(255),
    pr_coden VARCHAR(255),
    pr_sici VARCHAR(255),
    pr_bici VARCHAR(255),
    pr_eissn VARCHAR(255),
    pr_oclc_number VARCHAR(255),
    pr_stitle VARCHAR(255),
    pr_part VARCHAR(255),
    pr_artnum VARCHAR(255),
    pr_ssn VARCHAR(255),
    pr_quarter VARCHAR(255),
    pr_bib_record_id VARCHAR(255),
    pr_supplier_unique_record_id VARCHAR(255),
    pr_system_instance_id VARCHAR(255),
    pr_selected_item_barcode VARCHAR(255),
    pr_title_of_component VARCHAR(255),
    pr_author_of_component VARCHAR(255),
    pr_sponsor VARCHAR(255),
    pr_information_source VARCHAR(255),
    pr_patron_surname VARCHAR(255),
    pr_patron_name VARCHAR(255),
    pr_patron_type VARCHAR(255),
    pr_send_to_patron BOOLEAN,
    pr_bib_record TEXT,
    pr_needed_by DATE,
    pr_local_call_number VARCHAR(255),
    pr_hrid VARCHAR(255),
    pr_req_inst_symbol VARCHAR(255),
    pr_sup_inst_symbol VARCHAR(255),
    pr_peer_request_identifier VARCHAR(255),
    pr_pick_shelving_location VARCHAR(255),
    pr_patron_email VARCHAR(255),
    pr_patron_note VARCHAR(255),
    pr_pref_service_point VARCHAR(255),
    pr_pref_service_point_code VARCHAR(255),
    pr_pickup_location_slug VARCHAR(255),
    pr_pickup_url VARCHAR(255),
    pr_active_loan BOOLEAN,
    pr_due_date_from_lms VARCHAR(64),
    pr_parsed_due_date_lms TIMESTAMP WITH TIME ZONE,
    pr_due_date_rs VARCHAR(64),
    pr_parsed_due_date_rs TIMESTAMP WITH TIME ZONE,
    pr_overdue BOOLEAN,
    pr_needs_attention BOOLEAN,
    pr_network_status VARCHAR(32),
    pr_last_send_attempt TIMESTAMP WITH TIME ZONE,
    pr_next_send_attempt TIMESTAMP WITH TIME ZONE,
    pr_last_protocol_data TEXT,
    pr_number_of_send_attempts INTEGER,
    pr_last_sequence_sent INTEGER,
    pr_last_sequence_received INTEGER,
    pr_last_audit_no INTEGER,
    pr_sent_iso18626_request_response BOOLEAN,
    pr_request_to_continue BOOLEAN NOT NULL,
    pr_previous_states VARCHAR(255)
);

CREATE TABLE patron_request_audit (
    pra_id UUID PRIMARY KEY,
    pra_patron_request_fk UUID REFERENCES patron_request(pr_id),
    pra_date_created TIMESTAMP WITH TIME ZONE,
    pra_from_status_fk VARCHAR(255),
    pra_to_status_fk VARCHAR(255),
    pra_duration BIGINT,
    pra_message TEXT,
    pra_audit_data TEXT,
    pra_user VARCHAR(255),
    pra_audit_no INTEGER,
    pra_action_event VARCHAR(255),
    pra_rota_position BIGINT,
    pra_undo_performed BOOLEAN,
    pra_message_sequence_no INTEGER
);

CREATE TABLE batch (
    b_id UUID PRIMARY KEY,
    b_description VARCHAR(256),
    b_context VARCHAR(32),
    b_date_created TIMESTAMP WITH TIME ZONE,
    b_is_requester BOOLEAN,
    b_institution_id UUID
);

CREATE TABLE batch_patron_request (
    bpr_batch_id UUID REFERENCES batch(b_id),
    bpr_patron_request_id UUID REFERENCES patron_request(pr_id),
    PRIMARY KEY (bpr_batch_id, bpr_patron_request_id)
);

CREATE TABLE patron_request_loan_condition (
    prlc_id UUID PRIMARY KEY,
    prlc_patron_request_fk UUID REFERENCES patron_request(pr_id),
    prlc_date_created TIMESTAMP WITH TIME ZONE,
    prlc_last_updated TIMESTAMP WITH TIME ZONE,
    prlc_accepted BOOLEAN,
    prlc_code VARCHAR(255),
    prlc_note TEXT,
    prlc_relevant_supplier_fk UUID
);

CREATE TABLE patron_request_document (
    prd_id UUID PRIMARY KEY,
    prd_date_created TIMESTAMP WITH TIME ZONE,
    prd_position INTEGER,
    prd_file_definition UUID,
    prd_url VARCHAR(512),
    prd_patron_request UUID REFERENCES patron_request(pr_id)
);

CREATE TABLE patron_request_notification (
    prn_id UUID PRIMARY KEY,
    prn_patron_request_fk UUID REFERENCES patron_request(pr_id),
    prn_date_created TIMESTAMP WITH TIME ZONE,
    prn_last_updated TIMESTAMP WITH TIME ZONE,
    prn_timestamp TIMESTAMP WITH TIME ZONE,
    prn_seen BOOLEAN,
    prn_is_sender BOOLEAN,
    prn_attached_action VARCHAR(255),
    prn_action_status VARCHAR(255),
    prn_action_data VARCHAR(255),
    prn_message_sender_fk UUID,
    prn_message_receiver_fk UUID,
    prn_message_content TEXT
);

CREATE TABLE protocol_audit (
    pa_id UUID PRIMARY KEY,
    pa_date_created TIMESTAMP WITH TIME ZONE,
    pa_patron_request UUID REFERENCES patron_request(pr_id),
    pa_protocol_type VARCHAR(30),
    pa_protocol_method VARCHAR(30),
    pa_uri VARCHAR(255),
    pa_request_body TEXT,
    pa_response_status VARCHAR(30),
    pa_response_body TEXT,
    pa_duration BIGINT
);

CREATE TABLE request_identifier (
    ri_id UUID PRIMARY KEY,
    ri_identifier_type VARCHAR(255),
    ri_identifier VARCHAR(255),
    ri_patron_request UUID REFERENCES patron_request(pr_id)
);

CREATE TABLE patron_request_rota (
    prr_id UUID PRIMARY KEY,
    prr_date_created TIMESTAMP WITH TIME ZONE,
    prr_last_updated TIMESTAMP WITH TIME ZONE,
    prr_patron_request_fk UUID REFERENCES patron_request(pr_id),
    prr_rota_position BIGINT,
    prr_directory_id_fk VARCHAR(36),
    prr_system_identifier VARCHAR(255),
    prr_shelfmark VARCHAR(255),
    prr_availability VARCHAR(255),
    prr_normalised_availability VARCHAR(255),
    prr_available_from TIMESTAMP WITH TIME ZONE,
    prr_protocol_status BIGINT,
    prr_lb_score BIGINT,
    prr_lb_reason TEXT,
    prr_state_fk VARCHAR(255),
    prr_peer_symbol_fk UUID,
    prr_note TEXT,
    prr_instance_identifier VARCHAR(255),
    prr_copy_identifier VARCHAR(255),
    prr_protocol UUID
);

CREATE TABLE request_volume (
    rv_id UUID PRIMARY KEY,
    rv_name VARCHAR(255),
    rv_item_id VARCHAR(255),
    rv_patron_request_fk UUID REFERENCES patron_request(pr_id),
    rv_date_created TIMESTAMP WITH TIME ZONE,
    rv_last_updated TIMESTAMP WITH TIME ZONE,
    rv_temporary_item_barcode VARCHAR(255),
    rv_status_fk UUID
);