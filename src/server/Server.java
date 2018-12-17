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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Ztan
 */
public class Server extends WebSocketServer {

    private final boolean DEBUG;
    Solver solver;

    JSONObject solveJSON;
    boolean today;
    
    public Server(InetSocketAddress address, boolean debug) {
        super(address);
        this.DEBUG = debug;
        this.solver = new Solver();
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
            today = true;
            solver.reset();
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

                            solver.finishedGrid[i][j] = scraper.getFullTable()[i][j];
                        }
                        else {
                            blackCells.add(pos);
                            solver.addBlackCellsToGrid(pos);
                        }
                    }
                }



                sendInfoMessage(conn, "Getting solutions.");

                ArrayList<JSONObject> acrossClues = new ArrayList<>();

                sendInfoMessage(conn, "Getting across clues.");
                for (int i : scraper.getQuestionSet("Across")) {
                    JSONObject obj = new JSONObject();
                    obj.put("no", i);
                    obj.put("clue", scraper.getQuestion("Across", i));
                    acrossClues.add(obj);
                    Clue clue = new Clue(scraper.getQuestion("Across", i), 0, Clue.ACROSS, i);
                    solver.addClue(clue);
                }
                
                ArrayList<JSONObject> downClues = new ArrayList<>();
                
                for (int i : scraper.getQuestionSet("Down")) {
                    JSONObject obj = new JSONObject();
                    obj.put("no", i);
                    obj.put("clue", scraper.getQuestion("Down", i));
                    downClues.add(obj);
                    Clue clue = new Clue(scraper.getQuestion("Down", i), 0, Clue.DOWN, i);
                    solver.addClue(clue);
                }
                sendInfoMessage(conn, "Getting down clues.");

                ArrayList<JSONObject> questionPos = new ArrayList<>();

                for (int i : scraper.getQuestionIDSet()) {
                    JSONObject obj = new JSONObject();
                    obj.put("no", i);
                    obj.put("pos", scraper.getQuestionPos(i) + 1);
                    questionPos.add(obj);
                    solver.addQPos(i, scraper.getQuestionPos(i) + 1);
                }
                sendInfoMessage(conn, "Getting question coordinates.");

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

                solveJSON = new JSONObject();
                solveJSON.put("type", "puzzle");
                solveJSON.put("grid", grid.toArray());
                solveJSON.put("questionPos", questionPos.toArray());
                solveJSON.put("acrossClues", acrossClues.toArray());
                solveJSON.put("downClues", downClues.toArray());
                solveJSON.put("circles", circles.toArray());
                solveJSON.put("blackCells", blackCells.toArray());

                System.out.println(solveJSON.toString());
                conn.send(solveJSON.toString());
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
            today = false;
            String puzzleDate = msgObject.getString("date");
            solveJSON = new JSONObject(readFileAsString("puzzleArchive/" + puzzleDate + ".json"));
            solveJSON.put("type", "puzzleFromArchive");

            System.out.println(solveJSON.toString());
            conn.send(solveJSON.toString());

            sendInfoMessage(conn, "Getting puzzle from archive. (" + puzzleDate + ")");
        }
        else if (msgObject.getString("type").equals("getPuzzleArchiveList")) {
            JSONObject responseObject = new JSONObject();
            responseObject.put("type", "puzzleArchiveList");
            responseObject.put("puzzles", listPuzzles("puzzleArchive").toArray());

            System.out.println(responseObject.toString());
            conn.send(responseObject.toString());
        }
        else if(msgObject.getString("type").equals("solve")) {
            if(!today)
                initializeClues(solveJSON);

            String[][] finalGrid = solver.solve();
            solveJSON = new JSONObject();
            ArrayList<JSONObject> grid = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    int pos = (i * 5) + j + 1;
                    if (!finalGrid[i][j].equals("#") && !finalGrid[i][j].equals("0")) {
                        JSONObject obj = new JSONObject();
                        obj.put("pos", pos);
                        obj.put("char", finalGrid[i][j]);
                        grid.add(obj);
                    }
                }
            }
            solveJSON.put("type", "solution");
            solveJSON.put("grid", grid.toArray());
            System.out.println(">> Pushing grid to frontend.");
            conn.send(solveJSON.toString());
        }

        else if (msgObject.getString("type").equals("test")) {
            JSONObject responseObject = new JSONObject();
            responseObject.put("type", "testResponse");
            responseObject.put("test", "\uD83D\uDE09\uD83D\uDE09\uD83D\uDE09");

            System.out.println(responseObject.toString());
            conn.send(responseObject.toString());
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

    private void initializeClues(JSONObject object){
        solver.reset();
        System.out.println("> ");
        JSONArray downClues = object.getJSONArray("downClues");
        JSONArray acrossClues = object.getJSONArray("acrossClues");
        JSONArray blackCells = object.getJSONArray("blackCells");


        JSONArray qPosArray = object.getJSONArray("questionPos");
        JSONArray grid = object.getJSONArray("grid");

        for(Object o : downClues){
            int no = (int) ((JSONObject) o).get("no");
            String question = (String) ((JSONObject) o).get("clue");
            Clue clue = new Clue(question, 0, Clue.DOWN, no);
            solver.addClue(clue);
        }

        for(Object o : acrossClues){
            int no = (int) ((JSONObject) o).get("no");
            String question = (String) ((JSONObject) o).get("clue");
            Clue clue = new Clue(question, 0, Clue.ACROSS, no);
            solver.addClue(clue);
        }

        String[][] fullGrid = new String[5][5];

        for(int i = 0; i < blackCells.length(); i++){
            int pos = blackCells.getInt(i);
            int col = (pos - 1) % 5;
            int row = (pos - 1) / 5;
            solver.addBlackCellsToGrid(pos);
            fullGrid[row][col] = "#";
        }

        for(Object o : qPosArray){
            int no = (int) ((JSONObject) o).get("no");
            int pos = (int) ((JSONObject) o).get("pos");
            solver.addQPos(no, pos);
        }

        for(Object o : grid){
            int pos = (int) ((JSONObject) o).get("pos");
            String s = (String) ((JSONObject) o).get("char");
            int col = (pos - 1) % 5;
            int row = (pos - 1) / 5;
            fullGrid[row][col] = s;
        }
        solver.finishedGrid = fullGrid;

    }

}