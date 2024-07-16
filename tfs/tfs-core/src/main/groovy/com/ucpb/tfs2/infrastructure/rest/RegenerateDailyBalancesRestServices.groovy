package com.ucpb.tfs2.infrastructure.rest

import org.joda.time.LocalDate
import org.joda.time.Days
import org.joda.time.DurationFieldType
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson
import com.ucpb.tfs.batch.job.SpringJob;

import javax.annotation.Resource
import javax.ws.rs.GET;
import javax.ws.rs.Path
import javax.ws.rs.core.*
import javax.ws.rs.Produces
import java.text.SimpleDateFormat
import java.util.Date;

@Path("/regenDailyBalances")
@Component
class RegenerateDailyBalancesRestServices {

	@Autowired
	@Resource(name = "dailyBalanceRecorderJob")
	private SpringJob dailyBalanceRecorderJob;
	
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
			for (int i = 0; i <= days; i++) {
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
}
