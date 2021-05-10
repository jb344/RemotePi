package remotepi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 *
 * @author Jack
 */
public class CameraControl {
    
    public static Process capture;
    public static Process mkdir;
    public static String s;
    public static int count = 0;

    public static void main(String[] args) throws IOException, InterruptedException {

        Date date = new Date();

        String stringDate = date.toString();
        String stringDateReplaced = stringDate.replaceAll("\\s", "_");
        File directory = new File("/home/pi/Camera/" + stringDateReplaced);

        mkdir = Runtime.getRuntime().exec("mkdir /home/pi/Camera/" + stringDateReplaced);
        mkdir.waitFor();
        int scs = mkdir.exitValue();

        if (scs == 0) {
            capture = Runtime.getRuntime().exec("raspivid -o video.h264 -t 5000", null, directory);
            BufferedReader br = new BufferedReader(new InputStreamReader(capture.getInputStream()));

            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
        }

        capture.waitFor();
        int cmdScs = capture.exitValue();

        if (cmdScs == 0) {
            System.out.println("Command executed and closed successfully");
            count++;
        }

        capture.destroy();
        mkdir.destroy();
    }
}
