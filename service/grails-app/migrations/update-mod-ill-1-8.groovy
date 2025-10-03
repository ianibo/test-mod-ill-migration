databaseChangeLog = {
    changeSet(author: "chas (generated)", id: "1746113688245-1") {
		dropColumn(tableName: "refdata_value", columnName: "class")
    }

	changeSet(author: "chas (generated)", id: "1752510449900-1") {
		createTable(tableName: "mail_queue") {
			column(name: "mq_id", type: "VARCHAR(36)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "mail_queuePK")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "mq_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

			column(name: "mq_recipient", type: "VARCHAR(256)") {
				constraints(nullable: "false")
			}

			column(name: "mq_subject", type: "VARCHAR(256)") {
				constraints(nullable: "false")
			}

			column(name: "mq_patron_request", type: "VARCHAR(36)")

			column(defaultValue: "text/html", name: "mq_format", type: "VARCHAR(64)")

			column(name: "mq_body", type: "TEXT") {
				constraints(nullable: "false")
			}
		}
		
		addForeignKeyConstraint(baseColumnNames: "mq_patron_request", baseTableName: "mail_queue", constraintName: "FKf4b9svutg3skovx0q4470kgkl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pr_id", referencedTableName: "patron_request", validate: "true")
	}

    changeSet(author: "chas (generated)", id: "1752767908686-1") {
        createTable(tableName: "copyright_message") {
            column(name: "cm_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "copyright_messagePK")
            }

            column(name: "cm_value", type: "TEXT") {
                constraints(nullable: "false")
            }

            column(name: "cm_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "cm_country", type: "VARCHAR(2)") {
                constraints(nullable: "false")
            }

            column(name: "cm_description", type: "VARCHAR(512)") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "cm_country, cm_code", constraintName: "UK4eac2cc99886a8b559af4167d43e", tableName: "copyright_message")
    }

    changeSet(author: "chas", id: "1752767912345-1") {
		// We need to ensure the institution names are unique
        grailsChange {
            change {
                // Iterate through each of the distinct institution names
                sql.eachRow("""SELECT i_name
                               FROM ${database.defaultSchemaName}.institution
							   GROUP BY i_name
							   having count(*) > 1""".toString(), { uniqueInstitutionName ->
					Integer counter = 1;
					sql.eachRow("""SELECT i_id
								   FROM ${database.defaultSchemaName}.institution
								   where i_name = '${uniqueInstitutionName.i_name}'""".toString(), { institution ->
						// Update the name so that they are unique
						String name = uniqueInstitutionName.i_name + "_update_" + counter.toString();
						counter++;
						sql.execute("""update ${database.defaultSchemaName}.institution
                               		   set i_name = '${name}'
									   where i_id = '${institution.i_id}'""".toString());
					});
                });
            }
        }

		// Now everything is unique, add the unique constraint
		addUniqueConstraint(columnNames: "i_name", constraintName: "UC_INSTITUTIONI_NAME_COL", tableName: "institution")
    }

    changeSet(author: "chas (generated)", id: "1753791589136-1") {
        createTable(tableName: "patron_request_document") {
            column(name: "prd_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "patron_request_documentPK")
            }

            column(name: "prd_position", type: "INTEGER") {
                constraints(nullable: "false")
            }

            column(name: "prd_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "prd_patron_request", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "prd_url", type: "VARCHAR(512)")

            column(name: "prd_file_definition", type: "VARCHAR(36)")
        }

        addUniqueConstraint(columnNames: "prd_patron_request, prd_position", constraintName: "UK48051217b531cb80046271c06c73", tableName: "patron_request_document")
		addForeignKeyConstraint(baseColumnNames: "prd_file_definition", baseTableName: "patron_request_document", constraintName: "FKakoy6omwr698bkn6ruo1maa3s", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "fd_id", referencedTableName: "file_definition", validate: "true")
		addForeignKeyConstraint(baseColumnNames: "prd_patron_request", baseTableName: "patron_request_document", constraintName: "FKfalbwm8emyr8ek2qr6o47m24o", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pr_id", referencedTableName: "patron_request", validate: "true")
		
        createTable(tableName: "patron_request_document_audit") {
            column(name: "prd_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "patron_request_document_auditPK")
            }

            column(name: "prda_user_id", type: "VARCHAR(64)") {
                constraints(nullable: "false")
            }

            column(name: "prda_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "patron_request_document_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }

        addForeignKeyConstraint(baseColumnNames: "patron_request_document_id", baseTableName: "patron_request_document_audit", constraintName: "FKn0ta3jvgqut4veec7dvrfsm8e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "prd_id", referencedTableName: "patron_request_document", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1753800342070-1") {
        createTable(tableName: "patron_request_copyright") {
            column(name: "prc_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "patron_request_copyrightPK")
            }

            column(name: "prc_user_id", type: "VARCHAR(64)")

            column(name: "prc_copyright_message", type: "VARCHAR(36)")

            column(name: "prc_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "prc_agreed_date", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "prc_copyright_text", type: "TEXT")
        }

        addColumn(tableName: "patron_request") {
            column(name: "pr_copyright", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "prc_copyright_message", baseTableName: "patron_request_copyright", constraintName: "FK3dubqcll1xk1i5hu3p1hq60c6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "cm_id", referencedTableName: "copyright_message", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "pr_copyright", baseTableName: "patron_request", constraintName: "FKceumqwp9qxwh3h8qdi38mr13v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "prc_id", referencedTableName: "patron_request_copyright", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1756205303387-1") {

		// droping the table, so we can recreate it
        dropTable(tableName: "patron_request_document_audit")
    }

    changeSet(author: "chas (generated)", id: "1756205303387-2") {
        createTable(tableName: "patron_request_document_audit") {
            column(name: "prda_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "patron_request_document_auditPK")
            }

            column(name: "prda_user_id", type: "VARCHAR(64)")

            column(name: "prda_message", type: "VARCHAR(1024)")

            column(name: "prda_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "patron_request_document_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }

        addForeignKeyConstraint(baseColumnNames: "patron_request_document_id", baseTableName: "patron_request_document_audit", constraintName: "FKn0ta3jvgqut4veec7dvrfsm8e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "prd_id", referencedTableName: "patron_request_document", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1757348895516-1") {
        addColumn(tableName: "copyright_message") {
            column(defaultValueBoolean: "false", name: "cm_hide", type: "boolean")
        }
    }

    changeSet(author: "chas (generated)", id: "1757348895517-1") {
        grailsChange {
            change {
				sql.execute("""update ${database.defaultSchemaName}.refdata_value
                              		   set rdv_value = 'iso18626-2017', rdv_label = 'ISO18626-2017'
									   where rdv_value = 'iso18626'""".toString());
            }
        }
    }

    changeSet(author: "chas (generated)", id: "1757348895517-2") {
        grailsChange {
            change {
				sql.execute("""update ${database.defaultSchemaName}.protocol
                              		   set p_code = 'ISO18626-2017', p_description = 'ISO18626 (2017) protocol for inter library loans'
									   where p_code = 'ISO18626'""".toString());
            }
        }
    }
}
