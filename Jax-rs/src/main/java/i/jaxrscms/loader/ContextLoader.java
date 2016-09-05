package i.jaxrscms.loader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class ContextLoader implements ServletContextListener {

	private static AnnotationConfigApplicationContext context;

	public static <T> T getBean(Class<T> type) {
		return context.getBean(type);
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		context = new AnnotationConfigApplicationContext();
		context.scan("i.jpacms");
		context.refresh();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		context.destroy();
	}

}
