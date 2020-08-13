package utils;

import java.io.File;

public class Constants {
    // never modified
    public static final String VIEWPATH = "./" + File.separator + "static" + File.separator;
    
    // modified from junit tests, hence not final
    public static String TODOPATH = "./" + File.separator + "data" + File.separator + "todo" + File.separator;
    public static String DONEPATH = "./" + File.separator + "data" + File.separator + "done" + File.separator;
    public static String REMOVEDPATH = "./" + File.separator + "data" + File.separator + "removed" + File.separator;  
}
