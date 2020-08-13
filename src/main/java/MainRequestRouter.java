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
import controllers.TodoController;

public class MainRequestRouter extends AbstractHandler {
    
    static Logger lgr;
    private static final int PORT = 80;
    
    /**
     * HTTP requests coming from the user are handled in this method
     */
    @Override
    public void handle(String command, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        
        lgr.info("Got HTTP request {}", command);
        
        baseRequest.setHandled(true); // should not be passed to other handlers
        response.setStatus(HttpServletResponse.SC_OK); // default response is OK
        response.setContentType("application/json; charset=utf-8"); // default content type is JSON
        
        switch(command) {
            case "/":
                response.setContentType("text/html;"); // the landing page gives HTML
                response.getWriter().println(IndexController.handle());
                break;
                
            // JSON responses:
                
            case "/setAsTodo": // move a DONE task to TODO
                try { // in theory should never fail, but send 500 error if fails
                    response.getWriter().println(
                            TodoController.moveItemFromDoneToTodo(request).toString());
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);                    
                }
                break;                

            case "/removeTodo": // move a TODO task to REMOVED
                try {
                    response.getWriter().println(
                            TodoController.moveItemFromTodoToRemoved(request).toString());
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                break;
                
            case "/setAsDone": // move a TODO task to DONE
                try {
                    response.getWriter().println(
                            TodoController.moveItemFromTodoToDone(request).toString());
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                break;
                
            case "/addTodo":
                response.getWriter().println(
                        TodoController.createNewTodoItem(request).toString());
                break;
                
            case "/getAllTodos":
                response.getWriter().println(
                        TodoController.getAllTodoAndDoneItems().toString());
                break;
            
            default:
                response.setContentType("text/html;");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("404 Not found");
                return;
        }
        
        lgr.info("Finished processing {}", command);
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
     static void initLogger() {
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
        lgr = LogManager.getLogger(MainRequestRouter.class);
        lgr.info("Starting...");
        (new MainRequestRouter()).run();
    }
}
