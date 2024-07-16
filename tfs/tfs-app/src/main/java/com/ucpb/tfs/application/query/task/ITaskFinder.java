package com.ucpb.tfs.application.query.task;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 8/13/12
 */
@Finder
public interface ITaskFinder {

    Map<String, ?> findTask(@Param("taskReferenceNumber") String taskReferenceNumber);
    
    List<Map<String, ?>> findAllLcBranchTasks();

    List<Map<String, ?>> findAllLcBranchTasksByStatus(@Param("inclusiveStatus") String[] inclusiveStatus);

//    List<Map<String, ?>> findAllLcBranchTasksByUser(@Param("userid") String userid);
    List<Map<String, ?>> findAllLcBranchTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllLcMainTasks();

    List<Map<String, ?>> findAllLcMainTasksByStatus(@Param("inclusiveStatus") String[] inclusiveStatus);

    List<Map<String, ?>> findAllLcMainTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllAuxillaryBranchTasks();

//    List<Map<String, ?>> findAllAuxillaryBranchTasksByUser(@Param("userid") String userid);
    List<Map<String, ?>> findAllAuxillaryBranchTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllAuxillaryMainTasks();

    List<Map<String, ?>> findAllAuxillaryMainTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);


    List<Map<String, ?>> findAllIncomingMtTsd();

//    TODO: add validation for userActiveDirectoryId
    List<Map<String, ?>> findAllIncomingMtRouted(@Param("userActiveDirectoryId") String userActiveDirectoryId);

    List<Map<String, ?>> findAllOutgoingMt();
    
    List<Map<String, ?>> findAllOutgoingMtByStatus(@Param("status")String status);
    // unacted non-lc
    List<Map<String, ?>> findAllNonLcBranchTasks();

    List<Map<String, ?>> findAllNonLcBranchTasksByStatus(@Param("inclusiveStatus") String[] inclusiveStatus);

//    List<Map<String, ?>> findAllNonLcBranchTasksByUser(@Param("userid") String userid);
    List<Map<String, ?>> findAllNonLcBranchTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllNonLcMainTasks();

    List<Map<String, ?>> findAllNonLcMainTasksByStatus(@Param("inclusiveStatus") String[] inclusiveStatus);

    List<Map<String, ?>> findAllNonLcMainTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

//    List<Map<String, ?>> findAllCashAdvanceBranchTasksByUser(@Param("userid") String userid);
    List<Map<String, ?>> findAllCashAdvanceBranchTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllCashAdvanceMainTasksByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllExportAdvisingByUser(@Param("userid") String userid, @Param("userrole") String userrole);

//    List<Map<String, ?>> findAllExportBillsBranchByUser(@Param("userid") String userid);
    List<Map<String, ?>> findAllExportBillsBranchByUser(@Param("userid") String userid, @Param("userrole") String userrole);

    List<Map<String, ?>> findAllExportBillsTsdByUser(@Param("userid") String userid, @Param("userrole") String userrole);
}
