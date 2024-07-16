package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.application.service.AuditRegenService
import com.ucpb.tfs.batch.job.MasterFileReportGeneratorJob
import com.ucpb.tfs.batch.job.SpringJob
import org.joda.time.Days
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*
import java.text.SimpleDateFormat
/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 */
@Path("/regenAudit")
@Component
public class RegenerateAuditRestServices {

    @Autowired
    private AuditRegenService auditRegenService;

    @Autowired
    @Resource(name = "dailyBalanceRecorderJob")
    private SpringJob dailyBalanceRecorderJob;

    @Autowired
    private MasterFileReportGeneratorJob masterFileReportGeneratorJob;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/executeMigrated")
    public Response executeMigrated(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            // THIS PROCESS MUST RUN ONLY ONCE - IT IS HARD TO REVERT AFTER REGENERATING _AUDIT

            // PART 1 - migrated data

            println "\n---START: executeMigrated ---\n"

            auditRegenService.regenerateMigrated();

            println "\n---END: executeMigrated ---\n"

            returnMap.put("status", "ok");
            returnMap.put("details", "success");

        } catch(Exception e) {

            e.printStackTrace();

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/executeNonMigrated")
    public Response executeNonMigrated(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            // THIS PROCESS MUST RUN ONLY ONCE - IT IS HARD TO REVERT AFTER REGENERATING _AUDIT

            // PART 2 - Non-migrated data and TradeProduct records which did not have _AUDIT.
            // 1. Get all TradeService records with STATUS = APPROVED that are not LC, which are the starting
            //    lifecycle for each TradeProduct record.
            // 2. Create all in _AUDIT, depending on the specific TradeProduct record's lifecycle.

            println "\n---START: executeNonMigrated ---\n"

            auditRegenService.regenerateNonMigrated();

            println "\n---END: executeNonMigrated ---\n"

            returnMap.put("status", "ok");
            returnMap.put("details", "success");

        } catch(Exception e) {

            e.printStackTrace();

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/executeRegenAllDailyBalance")
    public Response executeRegenAllDailyBalance(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            // THIS PROCESS JUST CALLS THE DailyBalance METHOD IN BatchRestService.groovy

            println "\n---START: executeRegenAllDailyBalance ---\n"

            println "jsonParams = ${jsonParams}"

            String from = jsonParams?.get("from")?.toString();
            String to = jsonParams?.get("to")?.toString();

            GregorianCalendar fromCal = (GregorianCalendar)GregorianCalendar.getInstance();
            println "getDate(from) = ${getDate(from)}";
            fromCal.setTime(getDate(from));

            GregorianCalendar toCal = (GregorianCalendar)GregorianCalendar.getInstance();
            println "getDate(to) = ${getDate(to)}";
            toCal.setTime(getDate(to));

            println "fromCal = ${fromCal.getTime().toString()}"
            println "toCal = ${toCal.getTime().toString()}\n"

            LocalDate fromLocalDate = LocalDate.fromCalendarFields(fromCal);
            LocalDate toLocalDate = LocalDate.fromCalendarFields(toCal);

            // Build dates array
            List<LocalDate> dates = new ArrayList<LocalDate>();
            int days = Days.daysBetween(fromLocalDate, toLocalDate).getDays();
            for (int i = 0; i < days; i++) {
                LocalDate d = fromLocalDate.withFieldAdded(DurationFieldType.days(), i);
                dates.add(d);
            }

            for (LocalDate localDate : dates) {

                println "localDate.toString() = ${localDate.toString()}"

                Date date = localDate.toDateTimeAtStartOfDay().toDate();

                dailyBalanceRecorderJob.execute(getDateString(date));
            }

            println "\n---END: executeRegenAllDailyBalance ---\n"

            returnMap.put("status", "ok");
            returnMap.put("details", "success");

        } catch(Exception e) {

            e.printStackTrace();

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/executeRegenAllMaster")
    public Response executeRegenAllMaster(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            // THIS PROCESS JUST CALLS THE Master METHOD IN BatchRestService.groovy

            println "\n---START: executeRegenAllMaster ---\n"

            println "jsonParams = ${jsonParams}"

            String from = jsonParams?.get("from")?.toString();
            String to = jsonParams?.get("to")?.toString();

            GregorianCalendar fromCal = (GregorianCalendar)GregorianCalendar.getInstance();
            println "getDate(from) = ${getDate(from)}";
            fromCal.setTime(getDate(from));

            GregorianCalendar toCal = (GregorianCalendar)GregorianCalendar.getInstance();
            println "getDate(to) = ${getDate(to)}";
            toCal.setTime(getDate(to));

            println "fromCal = ${fromCal.getTime().toString()}"
            println "toCal = ${toCal.getTime().toString()}\n"

            LocalDate fromLocalDate = LocalDate.fromCalendarFields(fromCal);
            LocalDate toLocalDate = LocalDate.fromCalendarFields(toCal);

            // Build dates array
            List<LocalDate> dates = new ArrayList<LocalDate>();
            int days = Days.daysBetween(fromLocalDate, toLocalDate).getDays();
            for (int i = 0; i < days; i++) {
                LocalDate d = fromLocalDate.withFieldAdded(DurationFieldType.days(), i);
                dates.add(d);
            }

            for (LocalDate localDate : dates) {

                println "localDate.toString() = ${localDate.toString()}"

                Date date = localDate.toDateTimeAtStartOfDay().toDate();

                String masterDate = getDateStringMasterFormat(date) + "-00.00.00";
                println "masterDate = ${masterDate}"
                masterFileReportGeneratorJob.execute(masterDate);
            }

            println "\n---END: executeRegenAllMaster ---\n"

            returnMap.put("status", "ok");
            returnMap.put("details", "success");

        } catch(Exception e) {

            e.printStackTrace();

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    private static Date getDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = dateFormat.parse(dateString)
        return date;
    }

    private static String getDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String dateString = dateFormat.format(date)
        return dateString;
    }

    private static String getDateStringMasterFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date)
        return dateString;
    }
}
