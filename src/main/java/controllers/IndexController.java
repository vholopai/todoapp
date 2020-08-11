package controllers;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Constants;

public class IndexController extends Controller {
    private static final Logger lgr = LogManager.getLogger(IndexController.class);

    private static String readTodoFile() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.DATAPATH + "1.todo"))) {
            String line;
            if ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            lgr.error("Unable to read 1.todo");
        }
        return sb.toString();
    }
    
    public static String handle(String message) {
        String template = readTemplate("index.html");
        String todoItem = readTodoFile();
        template = template.replaceAll("\\$\\{todoitem\\}", todoItem);
        return template;
    }

}
