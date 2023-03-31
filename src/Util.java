import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static long start;

    public String getCurrentTime(long now) {
        DateFormat dateFormat = new SimpleDateFormat("[mm:ss.SSS] ");
        long elapsed = now - start;
        
        Date date = new Date(elapsed);
        return dateFormat.format(date);
    }

    public static void setStart(long s) {
        Util.start = s;
    }
}
