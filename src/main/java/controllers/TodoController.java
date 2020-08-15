package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

public class TodoController extends Controller {
    private static final Logger lgr = LogManager.getLogger(TodoController.class);

    static int readItemCount() {
        try (BufferedReader br = new BufferedReader(
                new FileReader(Constants.TODOPATH + "counter.txt"))) 
        {
            String line;
            if ((line = br.readLine()) != null) {
                return Integer.parseInt(line);
            }
        } catch (Exception e) {
            lgr.error("Unable to read counter.txt");
        }
        return -1;
    }
    
    static void writeItemCount(int todoItemCount) {
        try (BufferedWriter wr = new BufferedWriter(
                new FileWriter(Constants.TODOPATH + "counter.txt", false))) 
        {
            wr.append(Integer.toString(todoItemCount)); // wr will be auto-closed in Java SE 7 and later
        } catch (IOException e) {
            lgr.error("Unable to write counter.txt");
        }
    }
    
    private static void writeTodoItemText(String text, int todoItemCount) {
        try (BufferedWriter wr = new BufferedWriter(
                new FileWriter(Constants.TODOPATH + todoItemCount + ".todo", false))) 
        {
            wr.append(text); // wr will be auto-closed in Java SE 7 and later
        } catch (IOException e) {
            lgr.error("Unable to write {}.todo", todoItemCount);
        }
    }
    
    private static JSONObject readItemFromFile(File file, String itemType) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            lgr.error("readItemFromFile unable to read {}", file.getAbsolutePath());
            return null;
        }
        JSONObject item = new JSONObject();
        item.put("id", file.getName().split("\\.")[0]);
        item.put("msg", sb.toString());
        item.put("type", itemType);        
        return item;
    }
    
    private static void getTodoItems(JSONArray items) {
        File dir = new File(Constants.TODOPATH);
        List<String> todoFileNames = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".todo")) {
                todoFileNames.add(file.getName());
            }
        }
        // We want to show todo items in order always, so that OLDEST todo items (lowest ID number 
        // in file name) are shown first in list. Hence, sort the file name list 
        // (natural order, since item file names are of format 1.todo, 2.todo, ...),
        // and then read the file contents, and put to JSONArray in that order.
        List<String> sortedTodoFileNames = todoFileNames
                .stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (String fileName : sortedTodoFileNames) {
            JSONObject todoItem = readItemFromFile(new File(Constants.TODOPATH + fileName), "todo");
            if (todoItem != null) {
                items.put(todoItem);
            }
        }
    }
    
    private static void getDoneItems(JSONArray items) {
        File dir = new File(Constants.DONEPATH);
        List<String> doneFileNames = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".todo")) {
                doneFileNames.add(file.getName());
            }
        }
        // We want to show done items in order, so that NEWEST done items are shown first.
        // -> use reverseOrder
        List<String> sortedDoneFileNames = doneFileNames
                .stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for (String fileName : sortedDoneFileNames) {
            JSONObject doneItem = readItemFromFile(new File(Constants.DONEPATH + fileName), "done");
            if (doneItem != null) {
                items.put(doneItem);
            }
        }
    }
    
    public static JSONArray getAllTodoAndDoneItems() {
        JSONArray items = new JSONArray();
        getTodoItems(items);
        getDoneItems(items);
        return items;
    }
    
    public static JSONArray createNewTodoItem(HttpServletRequest request) throws IOException {
        int todoItemCount = readItemCount() + 1;
        if (todoItemCount < 1) { // unable to read item count?
            throw new IOException();
        }
        String text = request.getParameter("text");
        writeTodoItemText(text, todoItemCount);
        writeItemCount(todoItemCount);
        return getAllTodoAndDoneItems();
    }
    
    public static JSONArray moveItemFromTodoToDone(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("text"));
        Files.move(Paths.get(Constants.TODOPATH + id + ".todo"),  
                   Paths.get(Constants.DONEPATH + id + ".todo"));
        return getAllTodoAndDoneItems();
    }
    
    public static JSONArray moveItemFromDoneToTodo(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("text"));
        Files.move(Paths.get(Constants.DONEPATH + id + ".todo"),  
                   Paths.get(Constants.TODOPATH + id + ".todo"));
        return getAllTodoAndDoneItems();
    }    
    
    public static JSONArray moveItemFromTodoToRemoved(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("text"));
        Files.move(Paths.get(Constants.TODOPATH + id + ".todo"),  
                   Paths.get(Constants.REMOVEDPATH + id + ".todo"));
        return getAllTodoAndDoneItems();
    }       
}
