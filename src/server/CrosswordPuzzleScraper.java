package server;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 *
 * @author Ztan
 */
public class CrosswordPuzzleScraper {
   
    private final WebDriver DRIVER; 
    private final CrosswordPuzzle PUZZLE;
    private final boolean DEBUG;
    
    private class CrosswordPuzzle {
        private final int length;
        private final String[][] empty_table;
        private final String[][] full_table;
        private final boolean[][] circles;
        private final HashMap<Integer, Integer> question_pos;
        private final HashMap<String, HashMap<Integer, String>> questions;

        private CrosswordPuzzle(int length) {
            this.length = length;
            this.empty_table = new String[length][length];
            this.full_table = new String[length][length];
            this.question_pos = new HashMap<>();
            this.questions = new HashMap<>();
            this.questions.put("Across", new HashMap<>());
            this.questions.put("Down", new HashMap<>());
            this.circles = new boolean[length][length];
            
            for (int i = 0; i < length; i++)
                for (int j = 0; j < length; j++)
                    this.circles[i][j] = false;
        }
    }
    
    public CrosswordPuzzleScraper(boolean debug) {
        this.DEBUG = debug;
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        if (!DEBUG)
            firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        this.DRIVER = new FirefoxDriver(firefoxOptions);
        this.PUZZLE = new CrosswordPuzzle(5);
    }
    
    public void initialize() {
        DRIVER.get("https://www.nytimes.com/crosswords/game/mini" + (DEBUG ? "/2016/10/10" : ""));

        //WebElement accept = DRIVER.findElement(By.xpath("//*[@id=\"a_\"]"));
        //accept.click();


        List<WebElement> button_list = DRIVER.findElements(By.tagName("button"));
        for (WebElement button : button_list) {
            if (button.getAttribute("class").contains("buttons-modalButton")) {
                WebElement span = button.findElement(By.tagName("span"));
                
                if (span.getText().equals("OK")) {
                    button.click();
                    System.out.println("» Start button is clicked!");
                    break;
                }
            }
        }
        
        button_list = DRIVER.findElements(By.tagName("button"));
        for (WebElement button : button_list) {
            if (button.getText().equals("Reveal")) {
                button.click();
                System.out.println("» Reveal Button is clicked!");
                break;
            }  
        }
        
        List<WebElement> ul_list = DRIVER.findElements(By.tagName("ul"));
        for (WebElement ul : ul_list) {
            if (ul.getAttribute("class").contains("HelpMenu-menu")) {
                List<WebElement> a_list = ul.findElements(By.tagName("a"));
                
                for (WebElement a : a_list) {
                    if (a.getText().equals("Puzzle")) {
                        a.click();
                        System.out.println("» Puzzle Button is clicked!");
                        break;
                    }
                }
            }
        }
        
        button_list = DRIVER.findElements(By.tagName("button"));
        for (WebElement button : button_list) {
            if (button.getAttribute("class").contains("buttons-modalButton")) {
                if (button.getText().equals("REVEAL")) {
                    button.click();
                    System.out.println("» Reveal Button is clicked!");
                    break;
                }
            }
        }
        
        List<WebElement> div_list = DRIVER.findElements(By.tagName("div"));
        for (WebElement div : div_list) {
            if (div.getAttribute("class").contains("ModalBody-closeX")) {
                div.click();
                System.out.println("» Cross is clicked!");
                break;
            }
        }
        
        System.out.println();
    }
    
    public void quit() {
        DRIVER.quit();
    }
    
    public void scrapePuzzle() {
        WebElement cells = DRIVER.findElement(By.cssSelector("g[data-group='cells']"));
        List<WebElement> g_list = cells.findElements(By.tagName("g"));
        
        for (int i = 0; i < g_list.size(); i++) {
            List<WebElement> middle_list = g_list.get(i).findElements(By.cssSelector("text[text-anchor='middle']"));
            List<WebElement> start_list = g_list.get(i).findElements(By.cssSelector("text[text-anchor='start']"));
            List<WebElement> circle_list = g_list.get(i).findElements(By.tagName("circle"));
            List<WebElement> path_list = g_list.get(i).findElements(By.tagName("path"));
            
            int row = i / PUZZLE.length;
            int col = i % PUZZLE.length;
            
            if (middle_list.isEmpty()) {
                PUZZLE.empty_table[row][col] = "#";
                PUZZLE.full_table[row][col] = "#";
            }
            else {
                PUZZLE.empty_table[row][col] = "-";
                PUZZLE.full_table[row][col] = middle_list.get(0).getText();
            }
            
            if (!start_list.isEmpty())
                PUZZLE.question_pos.put(Integer.parseInt(start_list.get(0).getText()), i);
            
            if (!circle_list.isEmpty() || !path_list.isEmpty())
                PUZZLE.circles[row][col] = true;
        }
        
        List<WebElement> div_list = DRIVER.findElements(By.tagName("div"));
        
        for (WebElement div : div_list) {
            
            String title = "";
            
            if (div.getAttribute("class").contains("ClueList-wrapper")) {
                WebElement h3 = div.findElement(By.tagName("h3"));
                
                if (h3.getAttribute("class").contains("ClueList-title"))
                    title = h3.getText();
                
                List<WebElement> li_list = div.findElements(By.tagName("li"));
        
                for (WebElement element : li_list) {
                    if (element.getAttribute("class").contains("Clue-li")) {
                        List<WebElement> spans = element.findElements(By.tagName("span"));

                        String label = "";
                        String clue = "";

                        for (WebElement span : spans) {
                            if (span.getAttribute("class").contains("Clue-label"))
                                label = span.getText();
                            else if (span.getAttribute("class").contains("Clue-text"))
                                clue = span.getText();
                        }

                        if (title.equals("ACROSS"))
                            PUZZLE.questions.get("Across").put(Integer.parseInt(label), clue);
                        else if (title.equals("DOWN"))
                            PUZZLE.questions.get("Down").put(Integer.parseInt(label), clue);
                    }
                }
            }
        }
    }
    
    public String[][] getEmptyTable() {
        return PUZZLE.empty_table;
    }
    
    public String[][] getFullTable() {
        return PUZZLE.full_table;
    }
    
    public boolean[][] getCircles() {
        return PUZZLE.circles;
    }
    
    public int getQuestionPos(int question) {
        return PUZZLE.question_pos.get(question);
    }
    
    public String getQuestion(String direction, int question) {
        return PUZZLE.questions.get(direction).get(question);
    }
    
    public int getTablesLength() {
        return PUZZLE.length;
    }
    
    public Set<Integer> getQuestionIDSet() {
        return PUZZLE.question_pos.keySet();
    }
    
    public Set<Integer> getQuestionSet(String direction) {
        return PUZZLE.questions.get(direction).keySet();
    }
}