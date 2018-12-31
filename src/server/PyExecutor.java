package server;
import org.json.JSONArray;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PyExecutor {

    private static final String PY_SCRIPT_DIRECTORY = "pyScripts/";
    private String pyBin;
    private String script;

    public PyExecutor(String pyBin, String script) {
        this.pyBin = pyBin;
        this.script = script;
    }

    public String exec(String... args) throws IOException, InterruptedException {
        StringBuilder command = new StringBuilder();
        command.append(pyBin);
        command.append(" ");
        command.append(PY_SCRIPT_DIRECTORY);
        command.append(script);
        command.append(" ");
        for (int i = 0; i < args.length; i++) {
            command.append(args[i]);
            if (i != args.length - 1)
                command.append(" ");
        }

        Process process = Runtime.getRuntime().exec(command.toString());
        process.waitFor();

        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String err = error.readLine();

        if (err != null)
            System.out.println(error.readLine());

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = input.readLine();

        error.close();
        input.close();
        return result;
    }

    public JSONArray exec(ArrayList<Word> words) throws IOException, InterruptedException  {
        JSONArray arg = new JSONArray();
        for (Word w : words) {
            arg.put(w.word);
        }

        String toFile = arg.toString().replace("\"", "'").replace("\\r\\n", "");

        System.out.println(toFile);

        Writer fileWriter = new FileWriter("tempJSON.txt", false);
        fileWriter.write(toFile);
        fileWriter.close();

        StringBuilder command = new StringBuilder();
        command.append(pyBin);
        command.append(" ");
        command.append(PY_SCRIPT_DIRECTORY);
        command.append(script);
        command.append(" ");
        command.append("tempJSON.txt");

        Process process = Runtime.getRuntime().exec(command.toString());
        process.waitFor();

        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String err = error.readLine();

        if (err != null)
            System.out.println(error.readLine());

       /* BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String text = input.readLine();*/

        error.close();
        /*input.close();*/

        String text = "";
        try {
            text = readFileAsString("outJSON.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray(text);
    }

    private String readFileAsString(String fileName) throws Exception {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
}