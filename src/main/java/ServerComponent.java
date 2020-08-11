import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.properties.PropertiesConfiguration;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationBuilder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import controllers.IndexController;

public class ServerComponent extends AbstractHandler {
    
    private static Logger lgr;
    private static final int PORT = 80;
    
    /**
     * HTTP requests coming from the user are handled in this method
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException 
    {
        lgr.info("Got HTTP request " + baseRequest.getOriginalURI());

        response.setStatus(HttpServletResponse.SC_OK); // by default give 200 OK response 

        /* main page */
        response.setContentType("text/html;"); // client requested HTML page
        response.getWriter().println(IndexController.render(""));
        baseRequest.setHandled(true);
        /* main page */
        
        lgr.info("Finished processing");
        return;
    }
    
    /**
     * Start the server
     */
    public void run() {
        final Server server = new Server(PORT);
        server.setHandler(this);
        try {
            server.start();
        } catch (Exception e) {
            lgr.error("Unable to start server", e);
        }
    }
    
    /**
     * Just an example logger configuration. Change to FileAppender if needed.
     */
    private static void initLogger() {
        Properties properties = new Properties();
        properties.setProperty("status", "INFO");
        properties.setProperty("appenders", "CONSOLE");
        properties.setProperty("appender.CONSOLE.name", "ConsoleAppender");
        properties.setProperty("appender.CONSOLE.type", "Console");
        properties.setProperty("appender.CONSOLE.layout.type", "PatternLayout");
        properties.setProperty("appender.CONSOLE.layout.pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} | %-5p | [%t] %c{2} - %m%n");
        properties.setProperty("rootLogger.level", "INFO");
        properties.setProperty("rootLogger.appenderRefs", "theConsoleRef");
        properties.setProperty("rootLogger.appenderRef.theConsoleRef.ref", "ConsoleAppender");
        PropertiesConfigurationBuilder pcb = new PropertiesConfigurationBuilder();
        pcb.setConfigurationSource(null).setRootProperties(properties);
        PropertiesConfiguration config = pcb.build();
        Configurator.initialize(config);        
    }
    
    public static void main(String[] args) {
        initLogger();
        lgr = LogManager.getLogger(ServerComponent.class);
        lgr.info("Starting...");
        (new ServerComponent()).run();
    }
}
