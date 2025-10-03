package com.k_int;

enum CanEdit {

    /** They cannot edit the record */
    No,

    /** They can edit the record */
    Yes,

    /** They can edit the record, but give a warning that their changes may get overwritten by the owner of the record */
    YesWarning
}
