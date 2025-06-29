package co.xenastudios.utilities.utilities;

/**
 * Utility class for detecting if the server is running Folia.
 */
public final class FoliaUtility {

	// Private constructor to prevent instantiation
	private FoliaUtility() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Checks if the server is running Folia by attempting to load a Folia-specific class.
	 *
	 * @return true if running on Folia, false otherwise
	 */
	public static boolean isFolia() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}