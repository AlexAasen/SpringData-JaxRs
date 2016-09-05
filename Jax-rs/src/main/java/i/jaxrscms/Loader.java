package i.jaxrscms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/*")
public class Loader extends Application {

	@PostConstruct
	public void init() {
		// initialization
	}

	@PreDestroy
	public void destroy() {
		// clean up
	}

}
