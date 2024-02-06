package com.apple163.control_panel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Side;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class ResourceUsage {
    public static final StringProperty cpuUsage = new SimpleStringProperty();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static long[] prevTicks = null;
    public static final StringProperty ramUsed = new SimpleStringProperty();
    public static final StringProperty ramTotal = new SimpleStringProperty();
    public static final StringProperty diskUsed = new SimpleStringProperty();
    public static final StringProperty diskTotal = new SimpleStringProperty();
    public static final StringProperty diskPercentage = new SimpleStringProperty();
    public static final StringProperty networkSent = new SimpleStringProperty();
    public static final StringProperty networkReceived = new SimpleStringProperty();
    private static final NumberAxis yAxis_cpu = new NumberAxis();
    private static final NumberAxis xAxis_cpu = new NumberAxis();
    private static final NumberAxis yAxis_ram = new NumberAxis();
    private static final NumberAxis xAxis_ram = new NumberAxis();
    private static final NumberAxis yAxis_net = new NumberAxis();
    private static final NumberAxis xAxis_net = new NumberAxis();
    public static AreaChart<Number, Number> areaChart_cpu = new AreaChart<>(xAxis_cpu, yAxis_cpu);
    public static AreaChart<Number, Number> areaChart_ram = new AreaChart<>(xAxis_ram, yAxis_ram);
    public static AreaChart<Number, Number> areaChart_net = new AreaChart<>(xAxis_net, yAxis_net);
    private static final XYChart.Series<Number, Number> series_cpu = new XYChart.Series<>();
    private static final XYChart.Series<Number, Number> series_ram = new XYChart.Series<>();
    private static final XYChart.Series<Number, Number> series_sent_net = new XYChart.Series<>();
    private static final XYChart.Series<Number, Number> series_received_net = new XYChart.Series<>();
    private static long prevBytesSent = 0;
    private static long prevBytesReceived = 0;
    private static double sentSpeed;
    private static double receivedSpeed;
    private static long prevUpdateTime = System.currentTimeMillis();
    ResourceUsage() {
        initialiseChart(xAxis_cpu, yAxis_cpu, areaChart_cpu, series_cpu);
        initialiseChart(xAxis_ram, yAxis_ram, areaChart_ram, series_ram);
        initialiseChart(xAxis_net, yAxis_net, areaChart_net, series_sent_net);
        initialiseChart(xAxis_net, yAxis_net, areaChart_net, series_received_net);
        yAxis_cpu.setTickLabelsVisible(false);
        yAxis_ram.setTickLabelsVisible(false);
        areaChart_net.setTitle("NET");
        areaChart_cpu.setTitle("CPU        ");
        areaChart_ram.setTitle("RAM        ");
        areaChart_net.setTitleSide(Side.LEFT);
        areaChart_cpu.setTitleSide(Side.LEFT);
        areaChart_ram.setTitleSide(Side.LEFT);
    }
    public static void startUpdate() {
        Runnable updateTask = () -> {
            updateCPUUsage();
            updateRAMUsage();
            updateDiskUsage();
            updateNetworkUsage();
            updateCPUChart();
            updateRAMChart();
            updateNetworkChart();
        };
        scheduler.scheduleAtFixedRate(updateTask, 0, 500, TimeUnit.MILLISECONDS);
    }

    public static void stopUpdate() {
        scheduler.shutdown();
    }

    private static void updateCPUUsage() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();

        if (prevTicks == null) {
            prevTicks = processor.getSystemCpuLoadTicks();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception" + e);
            }
        }

        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        prevTicks = processor.getSystemCpuLoadTicks();
        cpuUsage.set(String.format("%.1f", cpuLoad * 100));
    }
    private static void updateRAMUsage() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        GlobalMemory memory = hal.getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        ramUsed.set(String.format("%.1f", bytesToGigabytes(usedMemory)));
        ramTotal.set(String.format("%.1f", bytesToGigabytes(totalMemory)));
    }
    private static void updateDiskUsage() {
        SystemInfo si = new SystemInfo();
        FileSystem fs = si.getOperatingSystem().getFileSystem();
        long totalDiskSpace = 0;
        long usedDiskSpace = 0;
        for (OSFileStore store : fs.getFileStores()) {
            long totalSpace = store.getTotalSpace();
            long usableSpace = store.getUsableSpace();
            long usedSpace = totalSpace - usableSpace;
            totalDiskSpace += totalSpace;
            usedDiskSpace += usedSpace;
        }
        diskUsed.set(String.format("%.1f", bytesToGigabytes(usedDiskSpace)));
        diskTotal.set(String.format("%.1f", bytesToGigabytes(totalDiskSpace)));
        diskPercentage.set(String.format("%.2f", bytesToGigabytes(usedDiskSpace) / bytesToGigabytes(totalDiskSpace) * 100));
    }
    private static void updateNetworkUsage() {
        long currentTime = System.currentTimeMillis();
        double timeInterval = (currentTime - prevUpdateTime) / 1000.0;  // Convert milliseconds to seconds

        // If the time interval is too small, skip this update
        if (timeInterval < 0.001) {
            return;
        }

        SystemInfo si = new SystemInfo();
        List<NetworkIF> networkIFsList = si.getHardware().getNetworkIFs();
        NetworkIF[] networkIFs = networkIFsList.toArray(new NetworkIF[0]);

        long bytesSent = 0;
        long bytesReceived = 0;

        for (NetworkIF net : networkIFs) {
            bytesSent += net.getBytesSent();
            bytesReceived += net.getBytesRecv();
        }

        // Initialize prevBytesSent and prevBytesReceived if they are 0
        if (prevBytesSent == 0) {
            prevBytesSent = bytesSent;
        }
        if (prevBytesReceived == 0) {
            prevBytesReceived = bytesReceived;
        }

        sentSpeed = (bytesSent - prevBytesSent) / timeInterval;
        receivedSpeed = (bytesReceived - prevBytesReceived) / timeInterval;

        String sentSpeedStr = formatNetworkSpeed(sentSpeed);
        String receivedSpeedStr = formatNetworkSpeed(receivedSpeed);

        networkSent.set(sentSpeedStr);
        networkReceived.set(receivedSpeedStr);

        prevBytesSent = bytesSent;
        prevBytesReceived = bytesReceived;
        prevUpdateTime = currentTime;
    }
    public static void updateCPUChart() {
        Platform.runLater(() -> {
            // Decrement the X values of the existing data points
            for (XYChart.Data<Number, Number> data : series_cpu.getData()) {
                Number oldXValue = data.getXValue();
                data.setXValue(oldXValue.intValue() - 1);
            }

            // Remove the oldest data point if necessary
            if (series_cpu.getData().size() > 60) {
                series_cpu.getData().removeFirst();
            }

            // Add the new data point at the current maximum X value
            series_cpu.getData().add(new XYChart.Data<>(60, toDouble(cpuUsage.get())));
        });
    }
    private static void updateRAMChart() {
        Platform.runLater(() -> {
            // Decrement the X values of the existing data points
            for (XYChart.Data<Number, Number> data : series_ram.getData()) {
                Number oldXValue = data.getXValue();
                data.setXValue(oldXValue.intValue() - 1);
            }

            // Remove the oldest data point if necessary
            if (series_ram.getData().size() > 60) {
                series_ram.getData().removeFirst();
            }

            // Add the new data point at the current maximum X value
            series_ram.getData().add(new XYChart.Data<>(60, toDouble(ramUsed.get())/toDouble(ramTotal.get())*100));
        });
    }
    private static void updateNetworkChart() {
        Platform.runLater(() -> {
            final double[] sentSpeed = {ResourceUsage.sentSpeed / 1024};
            final double[] receivedSpeed = {ResourceUsage.receivedSpeed / 1024};
            String roundedSentSpeed = String.format("%.1f", sentSpeed[0]);
            String roundedReceivedSpeed = String.format("%.1f", receivedSpeed[0]);
            sentSpeed[0] = toDouble(roundedSentSpeed);
            receivedSpeed[0] = toDouble(roundedReceivedSpeed);
            double highestSentSpeed = 0;
            double highestReceivedSpeed = 0;

            // Decrement the X values of the existing data points
            for (XYChart.Data<Number, Number> data : series_sent_net.getData()) {
                Number oldXValue = data.getXValue();
                data.setXValue(oldXValue.intValue() - 1);
                if (oldXValue.intValue() == 0) {
                    data.setYValue(0);
                }
            }
            for (XYChart.Data<Number, Number> data : series_received_net.getData()) {
                Number oldXValue = data.getXValue();
                data.setXValue(oldXValue.intValue() - 1);
                if (oldXValue.intValue() == 0) {
                    data.setYValue(0);
                }
            }

            // Remove the oldest data point if necessary
            if (series_sent_net.getData().size() > 60) {
                series_sent_net.getData().removeFirst();
            }
            // Calculate the maximum Y value for the current series
            for (XYChart.Data<Number, Number> data : series_sent_net.getData()) {
                Number oldYValue = data.getYValue();
                highestSentSpeed = Math.max(highestSentSpeed, oldYValue.doubleValue());
            }
            for (XYChart.Data<Number, Number> data : series_received_net.getData()) {
                Number oldYValue = data.getYValue();
                highestReceivedSpeed = Math.max(highestReceivedSpeed, oldYValue.doubleValue());
            }

            // Adjust the upper bound of the Y-axis based on the maximum Y value
            double maxSpeed = Math.max(Math.max(highestSentSpeed, highestReceivedSpeed), Math.max(sentSpeed[0], receivedSpeed[0]));
            yAxis_net.setUpperBound(maxSpeed + 10);

            yAxis_net.setTickLabelFormatter(new StringConverter<>() {
                @Override
                public String toString(Number object) {
                    if (object.doubleValue() == yAxis_net.getUpperBound()) {
                        return formatNetworkSpeed(object.doubleValue() * 1024);
                    } else if (object.doubleValue() == yAxis_net.getLowerBound()) {
                        return "0";
                    } else {
                        return "";
                    }
                }
                @Override
                public Number fromString(String string) {
                    return null;
                }
            });
            // Add the new data point at the current maximum X value
            series_received_net.getData().add(new XYChart.Data<>(xAxis_net.getUpperBound(), receivedSpeed[0]));
            series_sent_net.getData().add(new XYChart.Data<>(xAxis_net.getUpperBound(), sentSpeed[0]));
            yAxis_net.setTickUnit(yAxis_net.getUpperBound()/10);
        });
    }
    private void initialiseChart(NumberAxis xAxis, NumberAxis yAxis, AreaChart<Number, Number> areaChart, XYChart.Series<Number, Number> series) {
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() == xAxis.getLowerBound()) {
                    return "                  30 seconds";
                } else if (object.doubleValue() == xAxis.getUpperBound()) {
                    return "0";
                } else {
                    return "";
                }
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        xAxis.setAutoRanging(false);
        xAxis.setUpperBound(60);
        xAxis.setLowerBound(0);
        areaChart.setAnimated(false);
        areaChart.setCreateSymbols(false);
        areaChart.setLegendVisible(false);
        areaChart.setHorizontalGridLinesVisible(false);
        areaChart.setVerticalGridLinesVisible(false);
        areaChart.getData().add(series);
        areaChart.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/apple163/control_panel/chart.css")).toExternalForm());
        if (areaChart.equals(areaChart_net)) {
            areaChart.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/apple163/control_panel/networkChart.css")).toExternalForm());
        }

        for (int i = 0; i <= 60; i++){
            series.getData().add(new XYChart.Data<>(i, 0));
        }
    }
    private static String formatNetworkSpeed(double bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;

        while (bytes >= 1024 && unitIndex < units.length - 1) {
            bytes /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s/s", bytes, units[unitIndex]);
    }
    private static double bytesToGigabytes(long bytes) {
        return bytes / Math.pow(1024, 3);
    }
    private static double toDouble(String str) {
        return Double.parseDouble(str);
    }
}