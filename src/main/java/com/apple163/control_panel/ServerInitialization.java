package com.apple163.control_panel;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerInitialization {
    public static long pid;
    public ServerInitialization(TextArea console, TextField commandInput) {
        try {
            File serverFolder = new File(System.getProperty("user.home")+"\\Desktop\\test_server");
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "java", "-Xmx3G", "-Xms3G", "-jar", "server.jar", "--nogui");
            processBuilder.directory(serverFolder);
            processBuilder.redirectErrorStream(true); // R
            Process serverProcess = processBuilder.start();

            // Capture the output stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
            PrintWriter writer = new PrintWriter(serverProcess.getOutputStream(), true);
            SystemInfo si = new SystemInfo();
            OperatingSystem os = si.getOperatingSystem();
            List<OSProcess> procs = os.getProcesses();

            for (OSProcess proc : procs) {
                if (proc.getCommandLine().contains("-Xmx3G") && proc.getName().equals("java")) {
                    Runnable ramUsageTask = () -> {
                        long ramUsage = getProcessMemoryUsage(proc.getProcessID());
                        pid = proc.getProcessID();
                        ramUsage = ramUsage / 1024 / 1024; // Convert to MB change in main code
                    };
                    java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(1);
                    scheduler.scheduleAtFixedRate(ramUsageTask, 0, 1, TimeUnit.SECONDS);
                    break; // Stop after scheduling the first matching process
                }
            }
            commandInput.setOnAction(e -> {
                String command = commandInput.getText();
                writer.println(command);
                console.appendText(command + "\n");
                commandInput.clear();
            });
            new Thread(() -> {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        Platform.runLater(() -> console.appendText(finalLine + "\n"));
                        if(finalLine.contains("Starting")) {
                            Platform.runLater(() -> {
                                Panel_GUI.statusValue.setText("Starting");
                                Panel_GUI.statusValue.setFill(Color.rgb(255, 165, 0));
                                Panel_GUI.stop.setText("Kill");
                                Panel_GUI.start.setDisable(true);
                                Panel_GUI.stop.setDisable(false);
                            });
                        }
                        if (finalLine.contains("Done")) {
                            Platform.runLater(() -> {
                                Panel_GUI.statusValue.setText("Online");
                                Panel_GUI.statusValue.setFill(Color.LIMEGREEN);
                                Panel_GUI.start.setText("Restart");
                                Panel_GUI.stop.setText("Stop");
                                Panel_GUI.start.setDisable(false);
                                Panel_GUI.stop.setDisable(false);
                            });
                        }
                        if (finalLine.contains("Stopping the server")) {
                            Platform.runLater(() -> {
                                Panel_GUI.statusValue.setText("Stopping");
                                Panel_GUI.statusValue.setFill(Color.rgb(255, 165, 0));
                                Panel_GUI.stop.setText("Kill");
                                Panel_GUI.start.setDisable(true);
                                Panel_GUI.stop.setDisable(false);
                            });
                        }
                        if (finalLine.contains("All dimensions are saved")) {
                            Platform.runLater(() -> {
                                Panel_GUI.statusValue.setText("Offline");
                                Panel_GUI.statusValue.setFill(Color.RED);
                                Panel_GUI.stop.setText("Stop");
                                Panel_GUI.start.setDisable(false);
                                Panel_GUI.stop.setDisable(true);
                                if (Panel_GUI.start.getText().equalsIgnoreCase("Restart")&& Panel_GUI.restart) {
                                    console.appendText("Restarting the server.....\n");
                                    Panel_GUI.start.setText("Start");
                                    Panel_GUI.stop.setText("Kill");
                                    Panel_GUI.start.fire();
                                    Panel_GUI.stop.setDisable(false);
                                }
                                Panel_GUI.start.setText("Start");
                            });
                        }
                        if (finalLine.contains("Error") || finalLine.contains("Exception")) {
                            Platform.runLater(() -> {
                                Panel_GUI.statusValue.setText("Offline");
                                Panel_GUI.statusValue.setFill(Color.RED);
                                Panel_GUI.stop.setText("Stop");
                                Panel_GUI.start.setDisable(false);
                                Panel_GUI.stop.setDisable(true);
                                Panel_GUI.start.setText("Start");
                            });
                        }
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private long getProcessMemoryUsage(int pid) {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        OSProcess proc = os.getProcess(pid);

        if (proc != null && proc.getState() == OSProcess.State.RUNNING) {
            return proc.getResidentSetSize(); // This will return the resident set size of the process in bytes
        } else {
            return -1; // Return -1 if the process was not found or is not running
        }
    }
}