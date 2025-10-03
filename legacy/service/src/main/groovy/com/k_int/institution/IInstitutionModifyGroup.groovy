package com.k_int.institution;

import com.k_int.OperationResult;

/**
 * Provides a genericinterface for modifying groups associated with an institution
 * @author Chas
 *
 */
public interface IInstitutionModifyGroup {


    /**
     * Adds or removes the supplied groups
     * @param id The id that the groups need to be added to or removed from
     * @param groupIds The list of group identifiers that need to be added or removed
     * @param add true if the groups are to be added, false if they are to be removed
     * @return The OperationResult object containing the details of what happened
     */
    public OperationResult modifyGroups(String id, List<String> groupIds, boolean add);

}
