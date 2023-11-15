package edu.fscj.cop2805c.calendar;

// BirthdayCardLogger.java
// D. Singletary
// 3/29/23
// Class to handle network log requests

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CalenderReminderLogger extends Thread  {
    // allow clients to access our port
    final static int LOGPORT = 9000;

    private static final String LOGFILE = "reminderlog.txt";
    private ServerSocket server;
    private Socket socket;
    private ObjectInputStream inputStream;
    private BufferedWriter reminderLog;

    // constructor opens the log file and starts the server thread
    public CalenderReminderLogger() {
        try {
            reminderLog = Files.newBufferedWriter(Path.of(LOGFILE),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            start();
        } catch (IOException e) {
            System.err.println("could not open log file.");
            e.printStackTrace();
        }
    }

    // server thread
    public void run() {

        try {
            server = new ServerSocket(LOGPORT);

            socket = server.accept();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // read until client closes the connection
            while (true) {
                String logMsg = (String) inputStream.readObject();
                LocalDateTime local =  LocalDateTime.from(
                        Instant.now().atZone(ZoneId.systemDefault()));
                String msg = local.truncatedTo(ChronoUnit.MILLIS) + logMsg;
                reminderLog.write(msg);
                reminderLog.newLine();
            }
        } catch (EOFException e) {
            // client closed the connection, we are shutting down
            System.out.println("logger connection closed.");
            // only flush once, so need a nested try/catch here
            try { reminderLog.flush(); } catch (IOException e1) { }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
