databaseChangeLog = {
    changeSet(author: "chas (generated)", id: "1701364755228-1") {
        createTable(tableName: "institution") {
            column(name: "i_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institutionPK")
            }

            column(name: "i_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "i_name", type: "VARCHAR(256)") {
                constraints(nullable: "false")
            }

            column(name: "directory_entry_id", type: "VARCHAR(36)")

            column(name: "i_description", type: "VARCHAR(256)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "chas (generated)", id: "1701364755228-2") {
        createTable(tableName: "institution_group") {
            column(name: "ig_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_groupPK")
            }

            column(name: "ig_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ig_name", type: "VARCHAR(256)") {
                constraints(nullable: "false")
            }

            column(name: "ig_description", type: "VARCHAR(256)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "chas (generated)", id: "1701364755228-3") {
        createTable(tableName: "institution_group_institution") {
            column(name: "igi_institution_group_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_group_institutionPK")
            }

            column(name: "igi_institution_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_group_institutionPK")
            }
        }
    }

    changeSet(author: "chas (generated)", id: "1701364755228-4") {
        createTable(tableName: "institution_group_user") {
            column(name: "igu_institution_group_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_group_userPK")
            }

            column(name: "igu_institution_user_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_group_userPK")
            }
        }
    }

    changeSet(author: "chas (generated)", id: "1701364755228-5") {
        createTable(tableName: "institution_user") {
            column(name: "u_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_userPK")
            }

            column(name: "u_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "u_name", type: "VARCHAR(256)") {
                constraints(nullable: "false")
            }

            column(name: "u_folio_user_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "u_institution_managing", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "chas (generated)", id: "1701364755228-6") {
        addColumn(tableName: "patron_request") {
            column(name: "pr_institution_id", type: "varchar(36)")
        }
    }

    changeSet(author: "chas (generated)", id: "1701364755228-7") {
        addUniqueConstraint(columnNames: "u_folio_user_id", constraintName: "UC_INSTITUTION_USERU_FOLIO_USER_ID_COL", tableName: "institution_user")

        addForeignKeyConstraint(baseColumnNames: "pr_institution_id", baseTableName: "patron_request", constraintName: "FK3yyw1wep6ajwg1t5suvlrntvh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "igi_institution_id", baseTableName: "institution_group_institution", constraintName: "FK60qhjqvm3gurjcw8b69u4ynkn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "directory_entry_id", baseTableName: "institution", constraintName: "FK7kdp3lc9dse19w2p94n4kqivy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "de_id", referencedTableName: "directory_entry", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "u_institution_managing", baseTableName: "institution_user", constraintName: "FKamnvixotwsh8w9c1t41d854w0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "igu_institution_user_id", baseTableName: "institution_group_user", constraintName: "FKgttgakhgs9iuvwou15e2v7jbr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "u_id", referencedTableName: "institution_user", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "igu_institution_group_id", baseTableName: "institution_group_user", constraintName: "FKteb2qcs3ir04t7mg9bvj7ma65", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ig_id", referencedTableName: "institution_group", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "igi_institution_group_id", baseTableName: "institution_group_institution", constraintName: "FKtn44yeqcku5akb5ylv1schyrn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ig_id", referencedTableName: "institution_group", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1701944551734-1") {
        addColumn(tableName: "host_lms_item_loan_policy") {
            column(name: "hlilp_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "hlilp_institution_id", baseTableName: "host_lms_item_loan_policy", constraintName: "FKlbdlmm2atvj9bn0ttv4sab79n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1701944551734-2") {
        addColumn(tableName: "host_lms_location") {
            column(name: "hll_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "hll_institution_id", baseTableName: "host_lms_location", constraintName: "FK6bv8mtbb0c4vbmtxx0he86ic7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1701944551734-3") {
        addColumn(tableName: "host_lms_patron_profile") {
            column(name: "hlpp_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "hlpp_institution_id", baseTableName: "host_lms_patron_profile", constraintName: "FKshiqplbu8vv2ciitfyoqb1q7p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1701944551734-4") {
        addColumn(tableName: "host_lms_shelving_loc") {
            column(name: "hlsl_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "hlsl_institution_id", baseTableName: "host_lms_shelving_loc", constraintName: "FKsobggts8vxfhlc93fpvlkaug6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }
    
    changeSet(author: "Chas / Ian (manual)", id: "1701944551734") {
        // We need to create the default institution, before we update any existing data
        grailsChange {
            change {
                if (sql.firstRow("""SELECT i_id
                                    FROM ${database.defaultSchemaName}.institution
                                    where i_id = '00000000-0000-0000-0000-000000000000'""".toString()) == null) {
                    // If we do not have a default institution then create one
                    sql.execute("""insert into ${database.defaultSchemaName}.institution(i_id,i_version,i_name,i_description)
                                   values ('00000000-0000-0000-0000-000000000000', 0 , 'Default', 'Default')""".toString());
                }
            }
        }
    }
  
    changeSet(author: "chas (generated)", id: "1701944551735-1") {
        // We need to populate the institution field for existing patron request records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.patron_request
                                   set pr_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where pr_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1701944551735-2") {
        // We need to populate the institution field for existing item loan policy records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.host_lms_item_loan_policy
                                   set hlilp_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where hlilp_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1701944551735-3") {
        // We need to populate the institution field for existing location records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.host_lms_location
                                   set hll_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where hll_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1701944551735-4") {
        // We need to populate the institution field for existing patron profile records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.host_lms_patron_profile
                                   set hlpp_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where hlpp_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1701944551735-5") {
        // We need to populate the institution field for existing shelving location records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.host_lms_shelving_loc
                                   set hlsl_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where hlsl_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1702303207533-1") {
        addColumn(tableName: "batch") {
            column(name: "b_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "b_institution_id", baseTableName: "batch", constraintName: "FKrpao005384tasfkosa9yq5m3o", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1702303207533-2") {
        // We need to populate the institution field for existing batch records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.batch
                                   set b_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where b_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1702459893960-1") {
        addColumn(tableName: "timer") {
            column(name: "tr_institution_id", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "tr_institution_id", baseTableName: "timer", constraintName: "FKbsqpntlbk2bvkjrljpu0w1fur", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1702459893960-2") {
        // We need to populate the institution field for existing timer records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.timer
                                   set tr_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where tr_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1702635148103-1") {
        dropUniqueConstraint(constraintName: "uc_host_lms_item_loan_policyhlilp_code_col", tableName: "host_lms_item_loan_policy")

        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "hlilp_institution_id", tableName: "host_lms_item_loan_policy", validate: "true")
        
        addUniqueConstraint(columnNames: "hlilp_code, hlilp_institution_id", constraintName: "UKfbbec660e5a75c61c12f14589483", tableName: "host_lms_item_loan_policy")
    }
    
    changeSet(author: "chas (generated)", id: "1702635148103-2") {
        dropUniqueConstraint(constraintName: "uc_host_lms_locationhll_code_col", tableName: "host_lms_location")

        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "hll_institution_id", tableName: "host_lms_location", validate: "true")
        
        addUniqueConstraint(columnNames: "hll_code, hll_institution_id", constraintName: "UK1d9c3a62adf758d7e4b5f73bd38b", tableName: "host_lms_location")
    }
    
    changeSet(author: "chas (generated)", id: "1702635148103-3") {
        dropUniqueConstraint(constraintName: "uc_host_lms_shelving_lochlsl_code_col", tableName: "host_lms_shelving_loc")
        
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "hlsl_institution_id", tableName: "host_lms_shelving_loc", validate: "true")
        
        addUniqueConstraint(columnNames: "hlsl_code, hlsl_institution_id", constraintName: "UK49b89840b3a97b621e2fdf381192", tableName: "host_lms_shelving_loc")
    }

    changeSet(author: "chas (generated)", id: "1702635148103-4") {
        dropUniqueConstraint(constraintName: "uc_host_lms_patron_profilehlpp_code_col", tableName: "host_lms_patron_profile")
        
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "hlpp_institution_id", tableName: "host_lms_patron_profile", validate: "true")
        
        addUniqueConstraint(columnNames: "hlpp_code, hlpp_institution_id", constraintName: "UKf7b863299e7931d7ab655ee0efcd", tableName: "host_lms_patron_profile")
    }
}
