package com.arif.util;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Used to perform dates processing
 * 
 * @author arifakrammohammed
 *
 */
public class DateMechanic {

	private final static DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("d MMM, yyyy");

	/**
	 * Returns true if end date is greater than start date
	 * 
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 * @return <i>true</i> if end date greater than start date, false otherwise
	 * @throws ParseException
	 *             if error while processing dates
	 */
	public static boolean isEndDateGreater(String startDate, String endDate) throws ParseException {
		LocalDate startDateParsed = LocalDate.parse(startDate, dateTimeformatter);
		LocalDate endDateParsed = LocalDate.parse(endDate, dateTimeformatter);
		return endDateParsed.isAfter(startDateParsed) ? true : false;
	}

	/**
	 * Returns a list of all dates between start and end date; both dates being
	 * inclusive
	 * 
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 * @return {@link List} of all dates in {@link String} format
	 * @throws ParseException
	 *             if error while processing dates
	 */
	public static List<String> getAllDatesBetweenTwoDates(String startDate, String endDate) throws ParseException {

		List<String> dates = new ArrayList<String>();

		LocalDate startLocalDate = LocalDate.parse(startDate, dateTimeformatter);
		LocalDate endLocalDate = LocalDate.parse(endDate, dateTimeformatter);

		Consumer<LocalDate> processLocalDateToString = (localDate) -> {
			dates.add(localDate.format(dateTimeformatter));
		};
		Stream.iterate(startLocalDate, date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1).forEach(processLocalDateToString);

		return dates;
	}

}
