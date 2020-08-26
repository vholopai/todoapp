package router;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MainRequestRouterTest {
    private static Logger lgr;
    // some necessary initializations to make logging and web server
    // work with junit
    static {
        if (!router.MainRequestRouter.isRunning) {
            MainRequestRouter.initLogger();
            lgr = LogManager.getLogger(MainRequestRouterTest.class);
            MainRequestRouter.lgr = LogManager.getLogger(MainRequestRouter.class);
            lgr.info("Starting...");
            (new MainRequestRouter()).run();  
        }
    }
    
    @Test 
    public void testThatIndexHTTPGetReturnsCorrectSizePayload() 
            throws UnsupportedOperationException, IOException 
    {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://localhost/");
            assertEquals("TODO app", page.getTitleText());
        }
    }
}
