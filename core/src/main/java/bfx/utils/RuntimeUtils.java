package bfx.utils;

public class RuntimeUtils {

	/**
	 * @return total memory in use
	 */
	public static long usedMemory() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
