databaseChangeLog = {
    changeSet(author: "chas (generated)", id: "1702900450313-1") {
        addColumn(tableName: "counter") {
            column(name: "ct_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "ct_institution_id", baseTableName: "counter", constraintName: "FKmbqq0o3l01c5w6bw8kiiklh8c", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1702900450313-2") {
        // We need to populate the institution field for existing timer records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.counter
                                   set ct_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where ct_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1702900450313-3") {
        dropUniqueConstraint(constraintName: "uc_counterct_context_col", tableName: "counter")

        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "ct_institution_id", tableName: "counter", validate: "true")
        
        addUniqueConstraint(columnNames: "ct_context, ct_institution_id", constraintName: "UK2eb51cfce66d885f7648c5366349", tableName: "counter")
    }
    
    changeSet(author: "chas (generated)", id: "1703167594857-1") {
        addColumn(tableName: "notice_policy") {
            column(name: "np_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "np_institution_id", baseTableName: "notice_policy", constraintName: "FKg0yfugw6exgjuen8cgrat1l1c", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1703167594857-2") {
        // We need to populate the institution field for existing notice policy records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.notice_policy
                                   set np_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where np_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1703167594857-3") {
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "np_institution_id", tableName: "notice_policy", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1703170748438-1") {
        addColumn(tableName: "notice_event") {
            column(name: "ne_institution_id", type: "varchar(36)")
        }

        addForeignKeyConstraint(baseColumnNames: "ne_institution_id", baseTableName: "notice_event", constraintName: "FK1onlnkp13n9q1nbswup01ut75", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }


    changeSet(author: "chas (generated)", id: "1703170748438-2") {
        // We need to populate the institution field for existing notice event records
        grailsChange {
            change {
                // First the requester requests
                sql.execute("""update ${database.defaultSchemaName}.notice_event
                                   set ne_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where ne_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1703170748438-3") {
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "ne_institution_id", tableName: "notice_event", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1704202966864-1") {
        // We are changinh the type of the id column so drop it first
        dropColumn(tableName: "notice_event", columnName: "ne_id")
        
        // Now add the column, we do not make it not null at this stage
        addColumn(tableName: "notice_event") {
            column(name: "ne_id", type: "varchar(36)")
        }
    }
    
    changeSet(author: "chas (generated)", id: "1704202966864-2") {
        // Populate ne_id with a random uuid
        grailsChange {
            change {
                sql.execute("""update ${database.defaultSchemaName}.notice_event
                               set ne_id = gen_random_uuid()
                               where ne_id is null""".toString());
            }
        }
        
        // The ne_id field should now be populated, so set it as primary and not null
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "ne_id", tableName: "notice_event", validate: "true")
        addPrimaryKey(columnNames: "ne_id", constraintName: "notice_eventPK", tableName: "notice_event")
    }
    
    changeSet(author: "chas (generated)", id: "1704378192482-1") {
        addColumn(tableName: "patron") {
            column(name: "pat_institution_id", type: "varchar(36)")
        }
        
        addForeignKeyConstraint(baseColumnNames: "pat_institution_id", baseTableName: "patron", constraintName: "FKs9cebn8htql9br1dd3ad93q3k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1704378192482-2") {
        // We need to populate the institution field for existing patron records
        grailsChange {
            change {
                sql.execute("""update ${database.defaultSchemaName}.patron
                                   set pat_institution_id = '00000000-0000-0000-0000-000000000000'
                                   where pat_institution_id is null""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1704378192482-3") {
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "pat_institution_id", tableName: "patron", validate: "true")
    }
    
    changeSet(author: "chas (generated)", id: "1704378192482-4") {
        dropUniqueConstraint(constraintName: "uc_patronpat_host_system_identifier_col", tableName: "patron")
        
        addUniqueConstraint(columnNames: "pat_host_system_identifier, pat_institution_id", constraintName: "UK960211f96f896f14553713bbcd97", tableName: "patron")
    }

    changeSet(author: "chas (generated)", id: "1704471678476-1") {
        createIndex(indexName: "IX_institution_group_institutionPK", tableName: "institution_group_institution", unique: "true") {
            column(name: "igi_institution_id")

            column(name: "igi_institution_group_id")
        }

        createIndex(indexName: "IX_institution_group_userPK", tableName: "institution_group_user", unique: "true") {
            column(name: "igu_institution_user_id")

            column(name: "igu_institution_group_id")
        }
    }
    
    changeSet(author: "chas (generated)", id: "1704471678476-2") {
        dropPrimaryKey(tableName: "institution_group_institution")

        addPrimaryKey(columnNames: "igi_institution_id, igi_institution_group_id", constraintName: "institution_group_institutionPK", tableName: "institution_group_institution")

        dropPrimaryKey(tableName: "institution_group_user")

        addPrimaryKey(columnNames: "igu_institution_user_id, igu_institution_group_id", constraintName: "institution_group_userPK", tableName: "institution_group_user")
    }
    
    changeSet(author: "chas (generated)", id: "1704802189139-1") {
        createTable(tableName: "institution_setting") {
            column(name: "is_id", type: "VARCHAR(36)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "institution_settingPK")
            }

            column(name: "is_version", type: "BIGINT") {
                constraints(nullable: "false")
            }
            
            column(name: "is_institution_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "is_section", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "is_key", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "is_setting_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "is_value", type: "VARCHAR(255)")
            
            column(name: "is_default_value", type: "VARCHAR(255)")

            column(name: "is_vocab", type: "VARCHAR(255)")

            column(name: "is_hidden", type: "BOOLEAN")
        }

        addUniqueConstraint(columnNames: "is_key, is_institution_id", constraintName: "UK8dcd586c25fdeeef35f3668a47cd", tableName: "institution_setting")

        addForeignKeyConstraint(baseColumnNames: "is_institution_id", baseTableName: "institution_setting", constraintName: "FKlai8p7bxusu9u5m2ds7oekms1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "i_id", referencedTableName: "institution", validate: "true")
    }

    changeSet(author: "chas (generated)", id: "1704802189139-2") {
        // We need to populate application settings from app settings
        grailsChange {
            change {
                sql.execute("""
insert into ${database.defaultSchemaName}.institution_setting
(is_id, is_version, is_institution_id, is_section, is_key, is_setting_type,
 is_value, is_default_value, is_vocab, is_hidden)
select st_id, st_version, '00000000-0000-0000-0000-000000000000', st_section, st_key, st_setting_type,
       st_value, st_default_value, st_vocab, st_hidden
from ${database.defaultSchemaName}.app_setting
where st_section not in ('directory', 'institution', 'sharedIndex')
""".toString());
            }
        }
    }
    
    changeSet(author: "chas (generated)", id: "1704802189139-3") {
        // Create a backup of app_settings
        grailsChange {
            change {
                sql.execute("""
create table ${database.defaultSchemaName}.app_setting_backup_1_4
as
select *
from ${database.defaultSchemaName}.app_setting
""".toString());
            }
        }
        
        // Now delete the institution settings from app settings
        grailsChange {
            change {
                sql.execute("""
delete from ${database.defaultSchemaName}.app_setting
where st_section not in ('directory', 'institution', 'sharedIndex')
""".toString());
            }
        }
    }
}
