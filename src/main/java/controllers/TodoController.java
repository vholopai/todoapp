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

    private static int readTodoItemCounter() {
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.TODOPATH + "counter.txt"))) {
            String line;
            if ((line = br.readLine()) != null) {
                return Integer.parseInt(line);
            }
        } catch (Exception e) {
            lgr.error("Unable to read counter.txt");
        }
        return -1;
    }
    
    private static void writeTodoItemCounter(int indexNr) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.TODOPATH + "counter.txt", false))) {
            writer.append(Integer.toString(indexNr));
            writer.close();
        } catch (IOException e) {
            lgr.error("Unable to write counter.txt");
        }
    }
    
    private static void writeTodoItemText(String text, int indexNr) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.TODOPATH + indexNr + ".todo", false))) {
            writer.append(text);
            writer.close();
        } catch (IOException e) {
            lgr.error("Unable to write {}.todo", indexNr);
        }
    }
    
    private static JSONObject readItemFromFile(File file, boolean isTodoItem) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + " ");
            }
        } catch (Exception e) {
            lgr.error("Unable to read {}", file.getAbsolutePath());
        }
        JSONObject todoItem = new JSONObject();
        todoItem.put("id", file.getName().split("\\.")[0]);
        todoItem.put("msg", sb.toString());
        todoItem.put("type", (isTodoItem ? "todo" : "done"));        
        return todoItem;
    }
    
    public static JSONArray getAllTodoAndDoneItems() {
        JSONArray items = new JSONArray();
        File folder = new File(Constants.TODOPATH);
        List<String> todoFileNames = new ArrayList<>();
        // We want to show TODO items in order always.
        // Oldest TODO items (lowest ID number in file name) are shown first in list.
        // Hence, first collect the file names to list, sort it, and then read the file contents.
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".todo")) {
                todoFileNames.add(file.getName());
            }
        }
        List<String> sortedTodoFileNames = todoFileNames.stream()
                                            .sorted(Comparator.naturalOrder())
                                            .collect(Collectors.toList());
        for (String fileName : sortedTodoFileNames) {
            items.put(readItemFromFile(new File(Constants.TODOPATH + fileName), true));
        }
        folder = new File(Constants.DONEPATH);
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".todo")) {
                items.put(readItemFromFile(file, false));
            }
        }        
        return items;
    }
    
    public static JSONArray createNewTodoItem(HttpServletRequest request) {
        int nextItemIndex = readTodoItemCounter() + 1;
        String text = request.getParameter("text");
        writeTodoItemText(text, nextItemIndex);
        writeTodoItemCounter(nextItemIndex);
        return getAllTodoAndDoneItems();
    }
    
    public static JSONArray moveItemFromTodoToDone(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("text"));
            Files.move 
                   (Paths.get(Constants.TODOPATH + id + ".todo"),  
                    Paths.get(Constants.DONEPATH + id + ".todo"));
        } catch (IOException e) {
            lgr.error("Unable to set as DONE", e);
        } 
        return getAllTodoAndDoneItems();
    }
    
    public static JSONArray moveItemFromDoneToTodo(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("text"));
            Files.move 
                   (Paths.get(Constants.DONEPATH + id + ".todo"),  
                    Paths.get(Constants.TODOPATH + id + ".todo"));
        } catch (IOException e) {
            lgr.error("Unable to set as TODO", e);
        } 
        return getAllTodoAndDoneItems();
    }    
    
    public static JSONArray moveItemFromTodoToRemoved(HttpServletRequest request) {
        int id = -1;
        try {
            id = Integer.parseInt(request.getParameter("text"));
            Files.move 
                   (Paths.get(Constants.TODOPATH + id + ".todo"),  
                    Paths.get(Constants.REMOVEDPATH + id + ".todo"));
        } catch (IOException e) {
            lgr.error("Unable to remove TODO id={}", id, e);
        } 
        return getAllTodoAndDoneItems();
    }       
}
