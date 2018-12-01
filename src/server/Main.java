package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author Ztan
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException, IOException {
        String jarName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
        if (args.length < 1) {
            System.out.println("Ex: --> java -jar " + jarName + " geckodriver debug");
            return;
        }
        
        System.setProperty("webdriver.gecko.driver", args[0]);
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "log.txt");
        
        System.out.println("» Server started.");
        Server server = new Server(new InetSocketAddress(9090), args.length == 2);
        server.setTcpNoDelay(true);
        server.setConnectionLostTimeout(0);
        server.run();
    }
}