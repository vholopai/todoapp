package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                new FileReader(Constants.TODOPATH + Constants.COUNTER_FILE))) 
        {
            return Integer.parseInt(br.readLine());
        } catch (Exception e) {
            lgr.error("Unable to read Constants.COUNTER_FILE");
        }
        return -1;
    }
    
    static void writeItemCount(int todoItemCount) {
        try (BufferedWriter wr = new BufferedWriter(
                new FileWriter(Constants.TODOPATH + Constants.COUNTER_FILE, false))) 
        {
            wr.append(Integer.toString(todoItemCount)); // wr will be auto-closed in Java SE 7 and later
        } catch (IOException e) {
            lgr.error("Unable to write Constants.COUNTER_FILE");
        }
    }
    
    private static void writeTodo(String text, int todoItemCount) {
        try (BufferedWriter wr = new BufferedWriter(
                new FileWriter(Constants.TODOPATH + todoItemCount + Constants.ITEM_FILE_EXT, false))) 
        {
            wr.append(text); // wr will be auto-closed in Java SE 7 and later
        } catch (IOException e) {
            lgr.error("Unable to write {}.todo", todoItemCount);
        }
    }
    
    private static JSONObject itemToJson(Path file) {
        JSONObject item = new JSONObject();
        item.put("id", file.getFileName().toString().split("\\.")[0]); // items are in files, whose names are like "56.todo"
        try {
            item.put("msg", new String(Files.readAllBytes(file), System.getProperty("file.encoding")));
        } 
        catch (Exception e) {
            lgr.error("readItemFromFile unable to read {}", file.toString());
            return null;
        }
        return item;
    }
    
    private static void getTodos(JSONArray items) {
        File dir = new File(Constants.TODOPATH);
        List<String> todoFileNames = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(Constants.ITEM_FILE_EXT)) {
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
            JSONObject todoItem = itemToJson(Paths.get(Constants.TODOPATH + fileName));
            if (todoItem != null) {
                todoItem.put("type", "todo");
                items.put(todoItem);
            }
        }
    }
    
    private static void getDones(JSONArray items) {
        File dir = new File(Constants.DONEPATH);
        List<String> doneFileNames = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(Constants.ITEM_FILE_EXT)) {
                doneFileNames.add(file.getName());
            }
        }
        // We want to show done items in order, so that NEWEST done items are shown first.
        // -> use reverseOrder
        List<String> sortedDoneFileNames = doneFileNames
                .stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for (String fileName : sortedDoneFileNames) {
            JSONObject doneItem = itemToJson(Paths.get(Constants.DONEPATH + fileName));
            if (doneItem != null) {
                doneItem.put("type", "done");
                items.put(doneItem);
            }
        }
    }
    
    public static JSONArray getTodosDones() {
        JSONArray items = new JSONArray();
        getTodos(items);
        getDones(items);
        return items;
    }
    
    public static JSONArray addTodo(HttpServletRequest request) throws IOException {
        int todoItemCount = readItemCount() + 1;
        if (todoItemCount < 1) { // unable to read item count?
            throw new IOException();
        }
        writeTodo(request.getParameter("text"), todoItemCount);
        writeItemCount(todoItemCount);
        return getTodosDones();
    }
    
    public static JSONArray todoToDone(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("text"));
        Files.move(Paths.get(Constants.TODOPATH + id + Constants.ITEM_FILE_EXT),  
                   Paths.get(Constants.DONEPATH + id + Constants.ITEM_FILE_EXT));
        return getTodosDones();
    }
    
    public static JSONArray doneToTodo(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("text"));
        Files.move(Paths.get(Constants.DONEPATH + id + Constants.ITEM_FILE_EXT),  
                   Paths.get(Constants.TODOPATH + id + Constants.ITEM_FILE_EXT));
        return getTodosDones();
    }    
    
    public static JSONArray todoToRemoved(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("text"));
        Files.move(Paths.get(Constants.TODOPATH + id + Constants.ITEM_FILE_EXT),  
                   Paths.get(Constants.REMOVEDPATH + id + Constants.ITEM_FILE_EXT));
        return getTodosDones();
    }       
}
