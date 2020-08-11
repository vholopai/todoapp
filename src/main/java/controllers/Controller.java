package controllers;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Constants;

public class Controller {
    private static final Logger lgr = LogManager.getLogger(Controller.class);
    protected static String readTemplate(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.VIEWPATH + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            lgr.error("Unable to read template, fileName=" + fileName);
        }
        return sb.toString();
    }
}