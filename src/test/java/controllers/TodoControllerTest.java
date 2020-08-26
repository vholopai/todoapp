package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import router.MainRequestRouter;
import router.MainRequestRouterTest;
import utils.Constants;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TodoControllerTest {
    private static Logger lgr;

    static {
        if (!router.MainRequestRouter.isRunning) {
            MainRequestRouter.initLogger();
            lgr = LogManager.getLogger(MainRequestRouterTest.class);
            MainRequestRouter.lgr = LogManager.getLogger(MainRequestRouter.class);
            lgr.info("Starting...");
            (new MainRequestRouter()).run();         
        }
        Constants.TODOPATH = "./" + File.separator + "data_test" + File.separator + "todo" + File.separator;
        Constants.DONEPATH = "./" + File.separator + "data_test" + File.separator + "done" + File.separator;    
        Constants.REMOVEDPATH = "./" + File.separator + "data_test" + File.separator + "removed" + File.separator;           
        File f = new File(Constants.REMOVEDPATH + "1.todo");
        f.delete();
        TodoController.writeItemCount(0);
    }

    @Test
    public void a_addTodoTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        try (final WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage("http://localhost/");
            assertEquals("TODO app", page.getTitleText());  
            webClient.getPage("http://localhost/addTodo?text=test todo content");
        }
        int itemCount = TodoController.readItemCount();
        assertEquals(1, itemCount);
    }
    
    @Test
    public void b_checkThatAddedItemIsOk() throws IOException {
        JSONArray items = TodoController.getTodosDones();
        JSONObject o = (JSONObject) items.get(0);
        assertTrue(o.get("msg").toString().startsWith("test todo content"));
        assertEquals("todo", o.get("type"));
    }
    
    @Test
    public void c_markItemAsDone() throws IOException {
        HttpServletRequest mock = new MockRequest("text", "1");
        try {
            TodoController.todoToDone(mock);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray items = TodoController.getTodosDones();
        JSONObject o = (JSONObject) items.get(0);
        assertTrue(o.get("msg").toString().startsWith("test todo content"));
        assertEquals("done", o.get("type")); // should be changed
    }
    
    @Test
    public void d_markItemAsTodo() throws IOException {
        HttpServletRequest mock = new MockRequest("text", "1");
        try {
            TodoController.doneToTodo(mock);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray items = TodoController.getTodosDones();
        JSONObject o = (JSONObject) items.get(0);
        assertTrue(o.get("msg").toString().startsWith("test todo content"));
        assertEquals("todo", o.get("type")); // should be changed again
    }
    
    @Test
    public void e_removeTodo() throws IOException {
        HttpServletRequest mock = new MockRequest("text", "1");
        try {
            TodoController.todoToRemoved(mock);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray items = TodoController.getTodosDones();
        assertEquals(0, items.length());
    }
    
    @AfterClass
    public static void cleanup() {
        TodoController.writeItemCount(0);
    }

}
