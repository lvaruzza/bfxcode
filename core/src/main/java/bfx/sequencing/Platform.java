package bfx.sequencing;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Platform {
	private static Logger log = LoggerFactory.getLogger(Platform.class);
	private static Map<String,Platform> platforms = new HashMap<String,Platform>();
	
	private static ServiceLoader<Platform> platformsService = ServiceLoader.load(Platform.class);
	
	static {
		for(Platform platform: platformsService) {
			log.debug("Registering Platform: " + platform.getClass().getName());
			for(String name: platform.getNames()) {
				platforms.put(name.toLowerCase(),platform);
			}
		}
	}
	
	
	public static Platform get(String platformName) {
		Platform platform = platforms.get(platformName.toLowerCase());
		if (platform == null)
			throw new RuntimeException(String.format("Platform '%s' not found",platformName));
		else
			return platform;
	}
	
	public Object getName() {
		return getNames()[0];
	}

	public abstract String getFragmentName(String seqname);
	public abstract String[] getNames();

	public abstract int compare(String id, String id2);
}
