package co.xenastudios.utilities.utilities;

/**
 * Utility class for formatting time values.
 */
public final class TimeUtility {

	// Private constructor to prevent instantiation
	private TimeUtility() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Formats a duration in seconds as HH:mm:ss.
	 *
	 * @param rawSeconds The total number of seconds (non-negative).
	 * @return The formatted time string (HH:mm:ss).
	 */
	public static String formatSeconds(long rawSeconds) {
		if (rawSeconds < 0) {
			rawSeconds = 0;
		}
		long hours = rawSeconds / 3600;
		long minutes = (rawSeconds % 3600) / 60;
		long seconds = rawSeconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}