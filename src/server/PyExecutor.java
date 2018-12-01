package server;

import java.io.*;

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

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = input.readLine();
        input.close();

        return result;
    }

}
