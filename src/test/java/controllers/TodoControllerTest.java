package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import utils.Constants;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TodoControllerTest {
    
    static {
        Constants.TODOPATH = "./" + File.separator + "data_test" + File.separator + "todo" + File.separator;
        Constants.DONEPATH = "./" + File.separator + "data_test" + File.separator + "done" + File.separator;    
        Constants.REMOVEDPATH = "./" + File.separator + "data_test" + File.separator + "removed" + File.separator;           
        File f = new File(Constants.REMOVEDPATH + "1.todo");
        f.delete();
        TodoController.writeItemCount(0);
    }

    @Test
    public void a_addTodoTest() {
        System.out.println(Constants.TODOPATH);
        HttpServletRequest mock = new MockRequest("text", "test todo content");
        try {
            TodoController.addTodo(mock);
        } catch (IOException e) {
            e.printStackTrace();
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
