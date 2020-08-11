package controllers;

public class IndexController extends Controller {
    public static String render(String message) {
        String template = readTemplate("index.html");
        template = template.replaceAll("\\$\\{username\\}", "default user");
        return template;
    }

}
