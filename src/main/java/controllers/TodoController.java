package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

public class TodoController extends Controller {
    private static final Logger lgr = LogManager.getLogger(TodoController.class);

    private static int readIndexFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.DATAPATH + "counter.txt"))) {
            String line;
            if ((line = br.readLine()) != null) {
                return Integer.parseInt(line);
            }
        } catch (Exception e) {
            lgr.error("Unable to read counter.txt");
        }
        return -1;
    }
    
    private static void writeIndexFile(int indexNr) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DATAPATH + "counter.txt", false))) {
            writer.append(Integer.toString(indexNr));
            writer.close();
        } catch (IOException e) {
            lgr.error("Unable to write counter.txt");
        }
    }
    
    private static void writeTodoItem(String text, int indexNr) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DATAPATH + indexNr + ".todo", false))) {
            writer.append(text);
            writer.close();
        } catch (IOException e) {
            lgr.error("Unable to write " + indexNr + ".todo");
        }
    }
    
    public static String addTodo(HttpServletRequest request) {
        int nextItemIndex = readIndexFile() + 1;
        String text = request.getParameter("text");
        writeTodoItem(text, nextItemIndex);
        writeIndexFile(nextItemIndex);
        return "ok";
    }
    
    private static JSONObject readTodoFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            if ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            lgr.error("Unable to read " + file.getAbsolutePath());
        }
        JSONObject todoItem = new JSONObject();
        todoItem.put("id", file.getName().split("\\.")[0]);
        todoItem.put("msg", sb.toString());
        return todoItem;
    }
    
    public static JSONArray getAllTodoItems() {
        final File folder = new File(Constants.DATAPATH);
        JSONArray todoItems = new JSONArray();
        for (final File file : folder.listFiles()) {
            if (file.getName().endsWith(".todo")) {
                todoItems.put(readTodoFile(file));
            }
        }
        return todoItems;
    }
    
    
}
