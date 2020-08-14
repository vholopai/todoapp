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
            TodoController.createNewTodoItem(mock);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int itemCount = TodoController.readItemCount();
        assertEquals(itemCount, 1);
    }
    
    @Test
    public void b_checkThatAddedItemIsOk() {
        JSONArray items = TodoController.getAllTodoAndDoneItems();
        JSONObject o = (JSONObject) items.get(0);
        assertTrue(o.get("msg").toString().startsWith("test todo content"));
        assertEquals(o.get("type"), "todo");
    }
    
    @Test
    public void c_markItemAsDone() {
        HttpServletRequest mock = new MockRequest("text", "1");
        try {
            TodoController.moveItemFromTodoToDone(mock);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray items = TodoController.getAllTodoAndDoneItems();
        JSONObject o = (JSONObject) items.get(0);
        assertTrue(o.get("msg").toString().startsWith("test todo content"));
        assertEquals(o.get("type"), "done"); // should be changed
    }
    
    @Test
    public void d_markItemAsTodo() {
        HttpServletRequest mock = new MockRequest("text", "1");
        try {
            TodoController.moveItemFromDoneToTodo(mock);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray items = TodoController.getAllTodoAndDoneItems();
        JSONObject o = (JSONObject) items.get(0);
        assertTrue(o.get("msg").toString().startsWith("test todo content"));
        assertEquals(o.get("type"), "todo"); // should be changed again
    }
    
    @Test
    public void e_removeTodo() {
        HttpServletRequest mock = new MockRequest("text", "1");
        try {
            TodoController.moveItemFromTodoToRemoved(mock);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray items = TodoController.getAllTodoAndDoneItems();
        assertEquals(items.length(), 0);
    }
    
    @AfterClass
    public static void cleanup() {
        TodoController.writeItemCount(0);
    }

}
