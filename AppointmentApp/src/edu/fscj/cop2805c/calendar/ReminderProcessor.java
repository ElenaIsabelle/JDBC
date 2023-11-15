// ReminderProcessor.java
// D. Singletary
// 3/12/23
// Class and thread to process appointments from queue

package edu.fscj.cop2805c.calendar;

import edu.fscj.cop2805c.log.Logger;
import edu.fscj.cop2805c.message.MessageProcessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReminderProcessor extends Thread
        implements MessageProcessor, Logger<Reminder> {

    private CalenderReminderLogger logger;
    private Socket logSocket;
    private ObjectOutputStream streamToServer;

    private ConcurrentLinkedQueue<Reminder> safeQueue;
    private boolean stopped = false;

    public ReminderProcessor(ConcurrentLinkedQueue<Reminder> safeQueue) {
        this.safeQueue = safeQueue;

        logger = new CalenderReminderLogger();

        // wait for a bit to allow eventlog server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        try {
            logSocket = new Socket("localhost", CalenderReminderLogger.LOGPORT);
            System.out.println("connected to log server");

            streamToServer = new ObjectOutputStream(logSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start polling (invokes run(), below)
        this.start();
    }

    // remove messages from the queue and process them
    public void processMessages() {
        System.out.println("before processing, queue size is " + safeQueue.size());
        safeQueue.stream().forEach(e -> {
            // Do something with each element
            e = safeQueue.remove();
            log(e);
        });
        System.out.println("after processing, queue size is now " + safeQueue.size());
    }

    // allow external class to stop us
    public void endProcessing() {
        this.stopped = true;
        try {
            logSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interrupt();
    }

    @Override
    public void log(Reminder r) {
        String msg = ":reminder:" + r.getContact().getName();
        try {
            streamToServer.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // poll queue for reminders
    public void run() {
        final int SLEEP_TIME = 1000; // ms
        while (true) {
            try {
                processMessages();
                Thread.sleep(SLEEP_TIME);
                System.out.println("polling");
            } catch (InterruptedException ie) {
                // see if we should exit
                if (this.stopped == true) {
                    System.out.println("poll thread received exit signal");
                    break;
                }
            }
        }
    }
}