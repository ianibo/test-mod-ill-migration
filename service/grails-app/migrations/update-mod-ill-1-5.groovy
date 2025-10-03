databaseChangeLog = {
    changeSet(author: "chas (generated)", id: "1737980654823-1") {
        createTable(tableName: "directory_group") {
            column(name: "dg_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "directory_groupPK")
            }

            column(name: "dg_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "dg_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "dg_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "dg_code", constraintName: "UC_DIRECTORY_GROUPDG_CODE_COL", tableName: "directory_group")
    }

    changeSet(author: "chas (generated)", id: "1737980654823-2") {
		createTable(tableName: "directory_group_member") {
			column(name: "dgm_directory_group_id", type: "VARCHAR(36)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "directory_group_memberPK")
			}

			column(name: "dgm_directory_entry_id", type: "VARCHAR(36)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "directory_group_memberPK")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}

		addForeignKeyConstraint(baseColumnNames: "dgm_directory_group_id", baseTableName: "directory_group_member", constraintName: "FKn0l9elwagappok25rwfxb3mh6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "dg_id", referencedTableName: "directory_group", validate: "true")
		addForeignKeyConstraint(baseColumnNames: "dgm_directory_entry_id", baseTableName: "directory_group_member", constraintName: "FKnwd0jan1jfk5n42y9jb6x1g8r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "de_id", referencedTableName: "directory_entry", validate: "true")
	}
	
	changeSet(author: "chas (generated)", id: "1737980654823-3") {
        createTable(tableName: "directory_groups") {
            column(name: "dgs_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "directory_groupsPK")
            }

            column(name: "dgs_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "dgs_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "dgs_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

		addUniqueConstraint(columnNames: "dgs_code", constraintName: "UC_DIRECTORY_GROUPSDGS_CODE_COL", tableName: "directory_groups")
	}
	
	changeSet(author: "chas (generated)", id: "1737980654823-4") {
        createTable(tableName: "directory_groups_member") {
            column(name: "dgsm_directory_groups_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "directory_groups_memberPK")
            }

            column(name: "dgsm_directory_group_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "directory_groups_memberPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "dgsm_rank", type: "INTEGER") {
                constraints(nullable: "false")
            }
        }

		addUniqueConstraint(columnNames: "dgsm_directory_groups_id, dgsm_rank", constraintName: "UKeed1339bb8e0a408a5ff306961ac", tableName: "directory_groups_member")
		addForeignKeyConstraint(baseColumnNames: "dgsm_directory_groups_id", baseTableName: "directory_groups_member", constraintName: "FK59j0buldh59qrjbr8hovuwy32", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "dgs_id", referencedTableName: "directory_groups", validate: "true")
		addForeignKeyConstraint(baseColumnNames: "dgsm_directory_group_id", baseTableName: "directory_groups_member", constraintName: "FKof1v8mn6nuy2wmx8804adyg4l", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "dg_id", referencedTableName: "directory_group", validate: "true")

	}
	
	changeSet(author: "chas (generated)", id: "1737980654823-5") {
		addColumn(tableName: "directory_entry") {
			column(name: "de_search_directory_groups", type: "varchar(36)")
		}

        addForeignKeyConstraint(baseColumnNames: "de_search_directory_groups", baseTableName: "directory_entry", constraintName: "FKn4unk6kg9m9qsbybs98dqyaso", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "dgs_id", referencedTableName: "directory_groups", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738066260331-1") {
        createTable(tableName: "search_attribute") {
            column(name: "sa_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_attributePK")
            }

            column(name: "sa_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sa_attribute", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "sa_request_attribute", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "sa_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "sa_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "sa_code", constraintName: "UC_SEARCH_ATTRIBUTESA_CODE_COL", tableName: "search_attribute")
        addForeignKeyConstraint(baseColumnNames: "sa_attribute", baseTableName: "search_attribute", constraintName: "FK3gaxyed4w9kr91escikdd9seg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "sa_request_attribute", baseTableName: "search_attribute", constraintName: "FKkaoslgebyvrftonl9y7h4wii4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738066260331-2") {
        createTable(tableName: "search_tree") {
            column(name: "st_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_treePK")
            }

            column(name: "st_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "st_lhs_search_attribute", type: "VARCHAR(36)")

            column(name: "st_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "st_lhs_search_tree", type: "VARCHAR(36)")

            column(name: "st_rhs_search_attribute", type: "VARCHAR(36)")

            column(name: "st_operator", type: "VARCHAR(36)")

            column(name: "st_rhs_search_tree", type: "VARCHAR(36)")

            column(name: "st_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "st_code", constraintName: "UC_SEARCH_TREEST_CODE_COL", tableName: "search_tree")
        addForeignKeyConstraint(baseColumnNames: "st_lhs_search_attribute", baseTableName: "search_tree", constraintName: "FK8eik57ph5v68e9o0bt6nfauv2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "search_attribute", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "st_lhs_search_tree", baseTableName: "search_tree", constraintName: "FKay5ntycqny411fui4sm8fkh6h", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "st_id", referencedTableName: "search_tree", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "st_rhs_search_attribute", baseTableName: "search_tree", constraintName: "FKnxema3123voc34ebf6dqhs1tj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "search_attribute", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "st_rhs_search_tree", baseTableName: "search_tree", constraintName: "FKideog0q000q49uy71bxgsfbfx", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "st_id", referencedTableName: "search_tree", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "st_operator", baseTableName: "search_tree", constraintName: "FKmmrelcb6qpo3nt6ino1newv5j", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738066260331-3") {
        createTable(tableName: "search") {
            column(name: "s_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "searchPK")
            }

            column(name: "s_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "s_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "s_search_tree", type: "VARCHAR(36)")

            column(name: "s_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "s_code", constraintName: "UC_SEARCHS_CODE_COL", tableName: "search")
        addForeignKeyConstraint(baseColumnNames: "s_search_tree", baseTableName: "search", constraintName: "FK71c1iwvotnf5arf9c1apoy1td", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "st_id", referencedTableName: "search_tree", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738066260331-4") {
        createTable(tableName: "search_group") {
            column(name: "sg_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_groupPK")
            }

            column(name: "sg_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sg_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "sg_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

		addUniqueConstraint(columnNames: "sg_code", constraintName: "UC_SEARCH_GROUPSG_CODE_COL", tableName: "search_group")
    }

    changeSet(author: "chas (generated)", id: "1738066260331-5") {
        createTable(tableName: "search_group_entry") {
            column(name: "sge_search_group_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_group_entryPK")
            }

            column(name: "sge_search", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_group_entryPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sge_rank", type: "INTEGER") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "sge_search_group_id, sge_rank", constraintName: "UK7f0e4e8740bf8401c717da010d0d", tableName: "search_group_entry")
        addForeignKeyConstraint(baseColumnNames: "sge_search", baseTableName: "search_group_entry", constraintName: "FKbv9lqpwfb5s6autvfa8hua6w8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "s_id", referencedTableName: "search", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "sge_search_group_id", baseTableName: "search_group_entry", constraintName: "FKp5pd7j4xq3f7508h1iti0ur5q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sg_id", referencedTableName: "search_group", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738066260331-6") {
        addColumn(tableName: "directory_entry") {
            column(name: "de_searches", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "de_searches", baseTableName: "directory_entry", constraintName: "FK4jxfffbu9gh3d3t5yhgw894ju", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sg_id", referencedTableName: "search_group", validate: "true")
    }
	
    changeSet(author: "chas (generated)", id: "1738173149854-1") {
        addColumn(tableName: "directory_entry") {
            column(name: "de_host_lms_type", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "de_host_lms_type", baseTableName: "directory_entry", constraintName: "FK9r1qg3ncj5knmw6n9fqiw0g9b", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738327606687-1") {
        addColumn(tableName: "search_attribute") {
            column(name: "sa_relation", type: "varchar(36)")
        }

        addColumn(tableName: "search_attribute") {
            column(name: "sa_completeness", type: "varchar(36)")
        }

        addColumn(tableName: "search_attribute") {
            column(name: "sa_position", type: "varchar(36)")
        }

        addColumn(tableName: "search_attribute") {
            column(name: "sa_structure", type: "varchar(36)")
        }

        addColumn(tableName: "search_attribute") {
            column(name: "sa_truncation", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "sa_relation", baseTableName: "search_attribute", constraintName: "FK2v072wj9kktmo9y9afjvcv5mg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "sa_completeness", baseTableName: "search_attribute", constraintName: "FKpdqv5sdd40df7gv6rnq0iitph", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "sa_position", baseTableName: "search_attribute", constraintName: "FKmi7jmbb87c6wm26kdkcse5j8r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "sa_structure", baseTableName: "search_attribute", constraintName: "FK9agr3frqoy31a3im7uga94a30", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "sa_truncation", baseTableName: "search_attribute", constraintName: "FKemb9ses7rm524hu4y4luypepa", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }
	
    changeSet(author: "chas (generated)", id: "1738603138231-1") {
        addColumn(tableName: "search") {
            column(defaultValueNumeric: "1", name: "s_maximum_hits", type: "int4") {
                constraints(nullable: "false")
            }
        }
    }

	changeSet(author: "chas", id: "1738687908469-1") {
		// We need to copy the z3950 proxy key back to the system settings 
		grailsChange {
			change {
				sql.execute("""
insert into ${database.defaultSchemaName}.app_setting
(st_id, st_version, st_section, st_key, st_setting_type,
       st_value, st_default_value, st_vocab, st_hidden)
select is_id, is_version, is_section, is_key, is_setting_type,
 is_value, is_default_value, is_vocab, is_hidden
from ${database.defaultSchemaName}.institution_setting
where is_institution_id = '00000000-0000-0000-0000-000000000000' and
      is_section = 'z3950' and
      is_key = 'z3950_proxy_address'
""".toString());
			}
		}
	}
	
	changeSet(author: "chas", id: "1738687908469-2") {
		// Delete the z3950 proxy from the instiution settings 
		grailsChange {
			change {
				sql.execute("""
delete from ${database.defaultSchemaName}.institution_setting
where is_section = 'z3950' and
      is_key = 'z3950_proxy_address'
""".toString());
			}
		}
	}
	
    changeSet(author: "chas (generated)", id: "1738851178482-1") {
        addColumn(tableName: "search") {
            column(name: "s_host_lms_type", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "s_host_lms_type", baseTableName: "search", constraintName: "FK2nqtjb5nk7wl865hxaivo0ng0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1738851178482-2") {
        createTable(tableName: "search_exclude_host_lms_type") {
            column(name: "sehlt_search", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_exclude_host_lms_typePK")
            }

            column(name: "sehlt_host_lms_type", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "search_exclude_host_lms_typePK")
            }

            column(name: "sehlt_version", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }

        addForeignKeyConstraint(baseColumnNames: "sehlt_host_lms_type", baseTableName: "search_exclude_host_lms_type", constraintName: "FK5hkhb8t0fkkh45v107mhi2nc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "sehlt_search", baseTableName: "search_exclude_host_lms_type", constraintName: "FKibh8odbkubgblrd3j1iqnxye9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "s_id", referencedTableName: "search", validate: "true")
    }
	
    changeSet(author: "chas (generated)", id: "1740585120671-1") {
        dropColumn(columnName: "version", tableName: "search_group_entry")
    }
	
    changeSet(author: "chas (generated)", id: "1741366576214-1") {
        createTable(tableName: "protocol") {
            column(name: "p_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "protocolPK")
            }

            column(name: "p_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "p_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        addForeignKeyConstraint(baseColumnNames: "pc_protocol", baseTableName: "protocol_conversion", constraintName: "FKr1jlxrtb4wla0pgkws0ruili7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "p_id", referencedTableName: "protocol", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1741366576214-2") {
        addColumn(tableName: "protocol_conversion") {
            column(name: "pc_protocol_value", type: "varchar(255)") {
                constraints(nullable: "false")
            }
        }

        addColumn(tableName: "protocol_conversion") {
            column(name: "pc_internal_value", type: "varchar(32)") {
                constraints(nullable: "false")
            }
        }

        dropForeignKeyConstraint(baseTableName: "protocol_conversion", constraintName: "FK7ap6nhx5h49y414ixd87v9yhd")
        dropForeignKeyConstraint(baseTableName: "protocol_conversion", constraintName: "FK9xvislmhbajmpsasxavur17o3")

        dropColumn(columnName: "pc_conversion_value", tableName: "protocol_conversion")
		dropColumn(columnName: "pc_reference_value", tableName: "protocol_conversion")
		dropColumn(columnName: "pc_version", tableName: "protocol_conversion")
    }

    changeSet(author: "chas (generated)", id: "1741604442778-1") {
		addColumn(tableName: "protocol_conversion") {
			column(name: "pc_context", type: "varchar(32)") {
				constraints(nullable: "false")
			}
		}

        addUniqueConstraint(columnNames: "pc_protocol, pc_context, pc_internal_value", constraintName: "UK6aaecf8f5ca151092c12934d5183", tableName: "protocol_conversion")
    }
	
	changeSet(author: "chas", id: "1741709680-1") {
		// Delete the available actions from the responder and the requester state models as the majority are now inherited 
		grailsChange {
			change {
				sql.execute("""
delete from ${database.defaultSchemaName}.available_action
where aa_model in (select sm_id from state_model where sm_shortcode in ('PatronRequest', 'Responder'))
""".toString());
			}
		}
	}
	
    changeSet(author: "chas (generated)", id: "1743152280015-1") {
        createTable(tableName: "protocol_action_event") {
            column(name: "pa_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "protocol_action_eventPK")
            }

            column(name: "pa_protocol", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pa_action_event", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
		
        addUniqueConstraint(columnNames: "pa_protocol, pa_action_event", constraintName: "UKc730cd9b7971c9a8e9db5d44405a", tableName: "protocol_action_event")
		
        addForeignKeyConstraint(baseColumnNames: "pa_protocol", baseTableName: "protocol_action_event", constraintName: "FKdy005jvem92gbt5dt0ik01gu5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "p_id", referencedTableName: "protocol", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "pa_action_event", baseTableName: "protocol_action_event", constraintName: "FKovklbu03i829ialaghceplyqk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ae_id", referencedTableName: "action_event", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1743152280015-2") {
        addColumn(tableName: "patron_request") {
            column(name: "current_protocol_id", type: "varchar(36)")
        }

		addForeignKeyConstraint(baseColumnNames: "current_protocol_id", baseTableName: "patron_request", constraintName: "FKjym9lbmwpr6ix3qfonvo6q9cd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "p_id", referencedTableName: "protocol", validate: "true")
	}

    changeSet(author: "chas (generated)", id: "1744110576582-1") {
        addColumn(tableName: "patron_request_rota") {
            column(name: "prr_protocol", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "prr_protocol", baseTableName: "patron_request_rota", constraintName: "FK44ax1fwojs3qvwucq901831sf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "p_id", referencedTableName: "protocol", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1745491771853-1") {
        createTable(tableName: "remote_action") {
            column(name: "ra_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "remote_actionPK")
            }

            column(name: "ra_date_created", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }

            column(name: "ra_action", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ra_last_accessed", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(defaultValueNumeric: "0", name: "ra_rota_position", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ra_parameters", type: "VARCHAR(10000)")

            column(name: "ra_patron_request", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ra_expires", type: "TIMESTAMP WITHOUT TIME ZONE")
        }

        addForeignKeyConstraint(baseColumnNames: "ra_patron_request", baseTableName: "remote_action", constraintName: "FKcvhwknckyhoptdv1qrhh4qpgo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pr_id", referencedTableName: "patron_request", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "ra_action", baseTableName: "remote_action", constraintName: "FKna81v32hxq0vfuj7putluve22", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ae_id", referencedTableName: "action_event", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1746113687462-1") {
        createTable(tableName: "ill_smtp_message") {
            column(name: "ism_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "ill_smtp_messagePK")
            }

            column(name: "ism_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ism_container_template", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ism_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "ism_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "ism_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ism_action_event", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(defaultValueBoolean: "true", name: "ism_active", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "ism_institution_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ism_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ism_service_type", type: "VARCHAR(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "ism_service_type", baseTableName: "ill_smtp_message", constraintName: "FKd6jvs9int2xnwv5iujaap24h0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "ism_action_event", baseTableName: "ill_smtp_message", constraintName: "FKl8xxcexnx3b7pe9db9ewlvodc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ae_id", referencedTableName: "action_event", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "ism_container_template", baseTableName: "ill_smtp_message", constraintName: "FKniavvc0a2nsx5dmg76uiumjxf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "tmc_id", referencedTableName: "template_container", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "ism_institution_id", baseTableName: "ill_smtp_message", constraintName: "FKswf27bpwp5l42fdrgqrs42bgo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }
}
