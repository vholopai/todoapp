package controllers;

public class IndexController extends Controller {
    public static String handle(String message) {
        String template = readTemplate("index.html");
        return template;
    }
}
