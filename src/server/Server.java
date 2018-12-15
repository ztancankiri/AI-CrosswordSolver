package server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

/**
 *
 * @author Ztan
 */
public class Server extends WebSocketServer {

    private final boolean DEBUG;
    Solver solver;
    ArrayList<Clue> clues = new ArrayList<>();
    
    public Server(InetSocketAddress address, boolean debug) {
        super(address);
        this.DEBUG = debug;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(message);
        JSONObject msgObject = new JSONObject(message);
        
        if (msgObject.getString("type").equals("getPuzzle")) {
            sendInfoMessage(conn, "Connecting to nytimes.com");
            CrosswordPuzzleScraper scraper = new CrosswordPuzzleScraper(DEBUG);
            
            try {
                sendInfoMessage(conn, "Initializing puzzle scraper.");
                scraper.initialize();
                sendInfoMessage(conn, "Scraping puzzle from website.");
                scraper.scrapePuzzle();
                
                ArrayList<JSONObject> grid = new ArrayList<>();
                ArrayList<Integer> blackCells = new ArrayList<>();
                
                for (int i = 0; i < scraper.getTablesLength(); i++) {
                    for (int j = 0; j < scraper.getTablesLength(); j++) {
                        int pos = (i * 5) + j + 1;
                        
                        if (!scraper.getFullTable()[i][j].equals("#")) {
                            JSONObject obj = new JSONObject();
                            obj.put("pos", pos);
                            obj.put("char", scraper.getFullTable()[i][j]);
                            grid.add(obj);
                        }
                        else {
                            blackCells.add(pos);
                        }
                    }
                }

                sendInfoMessage(conn, "Getting solutions.");
                
                ArrayList<JSONObject> questionPos = new ArrayList<>();
                
                for (int i : scraper.getQuestionIDSet()) {
                    JSONObject obj = new JSONObject();
                    obj.put("no", i);
                    obj.put("pos", scraper.getQuestionPos(i) + 1);
                    questionPos.add(obj);
                }
                sendInfoMessage(conn, "Getting question coordinates.");
                ArrayList<JSONObject> acrossClues = new ArrayList<>();


                sendInfoMessage(conn, "Getting across clues.");
                for (int i : scraper.getQuestionSet("Across")) {
                    JSONObject obj = new JSONObject();
                    obj.put("no", i);
                    obj.put("clue", scraper.getQuestion("Across", i));
                    acrossClues.add(obj);
                    Clue clue = new Clue(scraper.getQuestion("Across", i), 0, Clue.ACROSS, i);
                    clues.add(clue);
                }
                
                ArrayList<JSONObject> downClues = new ArrayList<>();
                
                for (int i : scraper.getQuestionSet("Down")) {
                    JSONObject obj = new JSONObject();
                    obj.put("no", i);
                    obj.put("clue", scraper.getQuestion("Down", i));
                    Clue clue = new Clue(scraper.getQuestion("Down", i), 0, Clue.DOWN, i);
                    clues.add(clue);
                    downClues.add(obj);
                }
                sendInfoMessage(conn, "Getting down clues.");
                
                ArrayList<Integer> circles = new ArrayList<>();
                
                for (int i = 0; i < scraper.getTablesLength(); i++) {
                    for (int j = 0; j < scraper.getTablesLength(); j++) {
                        if (scraper.getCircles()[i][j]) {
                            int pos = (i * 5) + j + 1;
                            circles.add(pos);
                        }
                    }
                }
                sendInfoMessage(conn, "Checking for and getting circles on puzzle.");

                JSONObject responseObject = new JSONObject();
                responseObject.put("type", "puzzle");
                responseObject.put("grid", grid.toArray());
                responseObject.put("questionPos", questionPos.toArray());
                responseObject.put("acrossClues", acrossClues.toArray());
                responseObject.put("downClues", downClues.toArray());
                responseObject.put("circles", circles.toArray());
                responseObject.put("blackCells", blackCells.toArray());

                System.out.println(responseObject.toString());
                conn.send(responseObject.toString());
                
                scraper.quit();
            }
            catch (NotYetConnectedException e) {
                System.out.println(e.getMessage());
                scraper.quit();
            }
        }
        else if (msgObject.getString("type").equals("getDate")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            String currentDate = dtf.format(now);

            JSONObject responseObject = new JSONObject();
            responseObject.put("type", "date");
            responseObject.put("date", currentDate);

            System.out.println(responseObject.toString());
            conn.send(responseObject.toString());
        }
        else if (msgObject.getString("type").equals("getPuzzleFromArchive")) {
            String puzzleDate = msgObject.getString("date");
            JSONObject responseObject = new JSONObject(readFileAsString("puzzleArchive/" + puzzleDate + ".json"));
            responseObject.put("type", "puzzleFromArchive");

            System.out.println(responseObject.toString());
            conn.send(responseObject.toString());

            sendInfoMessage(conn, "Getting puzzle from archive. (" + puzzleDate + ")");
        }
        else if (msgObject.getString("type").equals("getPuzzleArchiveList")) {
            JSONObject responseObject = new JSONObject();
            responseObject.put("type", "puzzleArchiveList");
            responseObject.put("puzzles", listPuzzles("puzzleArchive").toArray());

            System.out.println(responseObject.toString());
            conn.send(responseObject.toString());
        }
        else if (msgObject.getString("type").equals("test")) {
            JSONObject responseObject = new JSONObject();
            responseObject.put("type", "testResponse");
            responseObject.put("test", "\uD83D\uDE09\uD83D\uDE09\uD83D\uDE09");

            System.out.println(responseObject.toString());
            conn.send(responseObject.toString());
        }

        else if(msgObject.getString("type").equals("solve")){
            System.out.println("Solving..." + clues.size());
            solver = new Solver(clues);
            System.out.println(solver.toString());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        
    }

    @Override
    public void onStart() {
        
    }

    private String readFileAsString(String fileName) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(fileName)));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private void sendInfoMessage(WebSocket conn, String msg) {
        JSONObject responseObject = new JSONObject();
        responseObject.put("type", "infoMessage");
        responseObject.put("msg", msg);
        conn.send(responseObject.toString());
        System.out.println();
        System.out.println("» " + msg);
    }

    private ArrayList<String> listPuzzles(String folderName) {
        File folder = new File(folderName);
        ArrayList<String> result = new ArrayList<>();

        for (final File fileEntry : folder.listFiles())
            result.add(fileEntry.getName().replace(".json", ""));

        return result;
    }
}