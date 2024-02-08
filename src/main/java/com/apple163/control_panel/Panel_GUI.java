package com.apple163.control_panel;

import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Panel_GUI extends Application {
    private final double width = Screen.getPrimary().getBounds().getWidth();
    private final double height = Screen.getPrimary().getBounds().getHeight();
    ScrollPane root;
    Pane pane;
    public static Button start ;
    public static Button stop;
    public String ipValueString;
    public static Text statusValue;
    public static boolean restart = false;
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage primaryStage) {
        root = new ScrollPane();
        pane = new Pane();
        start = new Button();
        stop = new Button();
        statusValue = new Text();
        Image icon = new Image("logo.png");
        ImageView tickView = new ImageView(new Image("check_icon.png"));
        ImageView iconView = new ImageView(icon);
        ImageView copyView = new ImageView(new Image("copy_icon.png"));
        Text title = new Text();
        Text subTitle = new Text();
        Text serverTitle = new Text();
        Text ip = new Text();
        Text cpu = new Text();
        Text ram = new Text();
        Text disk = new Text();
        Text status = new Text();
        Text extra1 = new Text();
        Text extra2 = new Text();
        Text extra3 = new Text();
        Text extra4 = new Text();
        Text extra5 = new Text();
        Text extra6 = new Text();
        Text cpuDets = new Text();
        Text ramDets = new Text();
        Text ramDets2 = new Text();
        Text diskDets = new Text();
        Text netSentDets = new Text();
        Text netRecvDets = new Text();
        TextField commandField = new TextField();
        TextArea console = new TextArea();
        Text ipValue = new Text();
        Text cpuValue = new Text();
        Text ramValue = new Text();
        Text diskValue = new Text();
        Button fileManager = new Button();
        Button copyIP = new Button();
        Scene scene = new Scene(root, width/2, height/2);
        Rectangle rectangle1 = new Rectangle(0, 0, width, height);
        Rectangle rectangle2 = new Rectangle(0, 0, width, height);
        Rectangle rectangle3 = new Rectangle(0, 0, width, height);
        Rectangle rectangle4 = new Rectangle(0, 0, width, height);
        Timeline timeline = new Timeline();
        Timeline timeline2 = new Timeline();
        ResourceUsage.startUpdate();
        ipValueString = getSystemIP().concat(":25565");
// Styling
        //SCENE WIDTH AND HEIGHT -> 768, 432
        buttonStyle(start);
        buttonStyle(stop);
        buttonStyle(fileManager);
        fileManager.setText("File Manager");
        fileManager.setFont(Font.font("UNISPACE", 15));
        start.setFont(Font.font("UNISPACE", 14));
        stop.setFont(Font.font("UNISPACE", 14));
        start.setText("Start");
        stop.setText("Stop");
        start.setDisable(false);
        stop.setDisable(true);
        title.setFill(Color.BLACK);
        title.setFont(Font.font("UNISPACE", 20));
        title.setText("MC Admin Panel");
        subTitle.setFill(Color.BLACK);
        subTitle.setFont(Font.font("UNISPACE", 30));
        subTitle.setText("Server Panel");
        serverTitle.setFill(Color.BLACK);
        serverTitle.setFont(Font.font("UNISPACE", 15));
        serverTitle.setText("Server Title");
        ip.setFill(Color.BLACK);
        ip.setFont(Font.font("UNISPACE", 10));
        ip.setText("IP:");
        ipValue.setFill(Color.BLACK);
        ipValue.setFont(Font.font("UNISPACE", 10));
        copyIP.setGraphic(copyView);
        copyIP.setStyle("-fx-background-color: transparent;");
        copyIP.setCursor(Cursor.HAND);
        cpu.setFill(Color.BLACK);
        cpu.setFont(Font.font("UNISPACE", 14));
        cpu.setText("CPU USAGE:");
        cpuValue.setFill(Color.BLACK);
        cpuValue.setFont(Font.font("UNISPACE", 14));
        cpuValue.textProperty().bind(ResourceUsage.cpuUsage.concat("%"));
        ram.setFill(Color.BLACK);
        ram.setFont(Font.font("UNISPACE", 14));
        ram.setText("RAM USAGE:");
        ramValue.setFill(Color.BLACK);
        ramValue.setFont(Font.font("UNISPACE", 14));
        ramValue.textProperty().bind(ResourceUsage.ramUsed.concat("/").concat(ResourceUsage.ramTotal).concat("GB"));
        disk.setFill(Color.BLACK);
        disk.setFont(Font.font("UNISPACE", 14));
        disk.setText("DISK USAGE:");
        diskValue.setFill(Color.BLACK);
        diskValue.setFont(Font.font("UNISPACE", 14));
        diskValue.textProperty().bind(ResourceUsage.diskPercentage.concat("%"));
        ipValue.setFont(Font.font("UNISPACE", 12));
        ipValue.setText(ipValueString);
        status.setFill(Color.BLACK);
        status.setFont(Font.font("UNISPACE", 14));
        status.setText("STATUS:");
        statusValue.setFont(Font.font("UNISPACE", 14));
        statusValue.setFill(Color.RED);
        statusValue.setText("Offline");

// Static Positioning and Sizing
        subTitle.setLayoutX(14);
        iconView.setLayoutX(2);
        iconView.setLayoutY(2);

//Text Dimension Bindings
        DoubleBinding ipWidth = new DoubleBinding() {
            {
                super.bind(ipValue.layoutBoundsProperty());
            }
            @Override
            protected double computeValue() {
                return ipValue.getLayoutBounds().getWidth();
            }
        };
        DoubleBinding ipHeight = new DoubleBinding() {
            {
                super.bind(ipValue.layoutBoundsProperty());
            }
            @Override
            protected double computeValue() {
                return ipValue.getLayoutBounds().getHeight();
            }
        };
        DoubleBinding cpuWidth = new DoubleBinding() {
            {
                super.bind(cpu.layoutBoundsProperty());
            }
            @Override
            protected double computeValue() {
                return cpu.getLayoutBounds().getWidth();
            }
        };
        DoubleBinding diskWidth = new DoubleBinding() {
            {
                super.bind(disk.layoutBoundsProperty());
            }
            @Override
            protected double computeValue() {
                return disk.getLayoutBounds().getWidth();
            }
        };
        DoubleBinding statusWidth = new DoubleBinding() {
            {
                super.bind(status.layoutBoundsProperty());
            }
            @Override
            protected double computeValue() {
                return status.getLayoutBounds().getWidth();
            }
        };

// Scalable Sizing
        iconView.fitWidthProperty().bind(scene.heightProperty().divide(10.8));
        iconView.fitHeightProperty().bind(scene.heightProperty().divide(8.64));
        start.prefWidthProperty().bind(scene.widthProperty().divide(8.347826086956522));
        start.prefHeightProperty().bind(scene.heightProperty().divide(10.8));
        stop.prefWidthProperty().bind(scene.widthProperty().divide(8.347826086956522));
        stop.prefHeightProperty().bind(scene.heightProperty().divide(10.8));
        fileManager.prefWidthProperty().bind(scene.widthProperty().divide(5.688888888888889));
        fileManager.prefHeightProperty().bind(scene.heightProperty().divide(12));
        console.prefWidthProperty().bind(scene.widthProperty().divide(1.376344086021505));
        console.prefHeightProperty().bind(scene.heightProperty().divide(1.51048951048951));
        commandField.prefWidthProperty().bind(scene.widthProperty().divide(1.376344086021505));
        commandField.prefHeightProperty().bind(scene.heightProperty().divide(16.61538461538462));
        copyIP.prefWidthProperty().bind(scene.widthProperty().divide(48));
        copyIP.prefHeightProperty().bind(copyIP.prefWidthProperty());
        copyView.fitWidthProperty().bind(copyIP.prefWidthProperty());
        copyView.fitHeightProperty().bind(copyIP.prefHeightProperty());
        tickView.fitWidthProperty().bind(copyIP.prefWidthProperty());
        tickView.fitHeightProperty().bind(copyIP.prefHeightProperty());
        rectangle1.widthProperty().bind(scene.widthProperty().divide(2.782608695652174));
        rectangle1.heightProperty().bind(scene.heightProperty());
        rectangle2.widthProperty().bind(scene.widthProperty().divide(1.56734693877551));
        rectangle2.heightProperty().bind(scene.heightProperty().divide(3));
        rectangle3.widthProperty().bind(rectangle2.widthProperty());
        rectangle3.heightProperty().bind(rectangle2.heightProperty());
        rectangle4.widthProperty().bind(rectangle2.widthProperty());
        rectangle4.heightProperty().bind(rectangle2.heightProperty());
        ResourceUsage.areaChart_net.prefWidthProperty().bind(rectangle2.widthProperty());
        ResourceUsage.areaChart_net.prefHeightProperty().bind(rectangle2.heightProperty());
        ResourceUsage.areaChart_cpu.prefWidthProperty().bind(rectangle2.widthProperty());
        ResourceUsage.areaChart_cpu.prefHeightProperty().bind(rectangle2.heightProperty());
        ResourceUsage.areaChart_ram.prefWidthProperty().bind(rectangle2.widthProperty());
        ResourceUsage.areaChart_ram.prefHeightProperty().bind(rectangle2.heightProperty());

// Movable Location
        start.layoutXProperty().bind(scene.widthProperty().divide(1.33333333333333));
        start.layoutYProperty().bind(scene.heightProperty().divide(1.371428571428571));
        stop.layoutXProperty().bind(start.layoutXProperty().add(start.widthProperty()).add(2));
        stop.layoutYProperty().bind(start.layoutYProperty());
        fileManager.layoutXProperty().bind(scene.widthProperty().divide(1.222929936305732));
        fileManager.setLayoutY(0);
        console.layoutXProperty().bind(scene.widthProperty().divide(59.07692307692308));
        console.layoutYProperty().bind(scene.heightProperty().divide(4.695652173913043));
        commandField.layoutXProperty().bind(scene.widthProperty().divide(59.07692307692308));
        commandField.layoutYProperty().bind(console.layoutYProperty().add(console.heightProperty()));
        serverTitle.layoutXProperty().bind(scene.widthProperty().divide(1.308347529812606));
        serverTitle.layoutYProperty().bind(scene.heightProperty().divide(4.153846153846154));
        title.layoutXProperty().bind(iconView.fitWidthProperty().add(7));
        title.layoutYProperty().bind(iconView.fitHeightProperty().divide(1.7));
        subTitle.layoutYProperty().bind(scene.heightProperty().divide(5.61038961038961));
        ip.layoutXProperty().bind(scene.widthProperty().divide(1.337979094076655));
        ip.layoutYProperty().bind(scene.heightProperty().divide(3.692307692307692));
        ipValue.layoutXProperty().bind(scene.widthProperty().divide(1.290756302521008));
        ipValue.layoutYProperty().bind(ip.layoutYProperty());
        copyIP.layoutXProperty().bind(ipValue.layoutXProperty().add(ipWidth).subtract(4));
        copyIP.layoutYProperty().bind(ipValue.layoutYProperty().subtract(ipHeight.multiply(1.22)));
        cpu.layoutXProperty().bind(scene.widthProperty().divide(1.333333333333333));
        cpu.layoutYProperty().bind(scene.heightProperty().divide(2.88));
        cpuValue.layoutXProperty().bind(cpu.layoutXProperty().add(cpuWidth).add(2));
        cpuValue.layoutYProperty().bind(cpu.layoutYProperty());
        ram.layoutXProperty().bind(cpu.layoutXProperty());
        ram.layoutYProperty().bind(cpu.layoutYProperty().add(scene.heightProperty().divide(10.8)));
        ramValue.layoutXProperty().bind(cpuValue.layoutXProperty());
        ramValue.layoutYProperty().bind(ram.layoutYProperty());
        disk.layoutXProperty().bind(cpu.layoutXProperty());
        disk.layoutYProperty().bind(ram.layoutYProperty().add(scene.heightProperty().divide(10.8)));
        diskValue.layoutXProperty().bind(disk.layoutXProperty().add(diskWidth).add(2));
        diskValue.layoutYProperty().bind(disk.layoutYProperty());
        status.layoutXProperty().bind(cpu.layoutXProperty());
        status.layoutYProperty().bind(disk.layoutYProperty().add(scene.heightProperty().divide(10.8)));
        statusValue.layoutXProperty().bind(status.layoutXProperty().add(statusWidth).add(2));
        statusValue.layoutYProperty().bind(status.layoutYProperty());
        rectangle1.layoutXProperty().bind(scene.widthProperty().divide(768));
        rectangle1.layoutYProperty().bind(scene.heightProperty().divide(1.002320185614849));
        rectangle2.layoutXProperty().bind(scene.widthProperty().divide(2.772563176895307));
        rectangle2.layoutYProperty().bind(rectangle1.layoutYProperty());
        rectangle3.layoutXProperty().bind(rectangle2.layoutXProperty());
        rectangle3.layoutYProperty().bind(rectangle2.layoutYProperty().add(rectangle2.heightProperty()));
        rectangle4.layoutXProperty().bind(rectangle2.layoutXProperty());
        rectangle4.layoutYProperty().bind(rectangle3.layoutYProperty().add(rectangle3.heightProperty()));
        ResourceUsage.areaChart_net.layoutXProperty().bind(rectangle4.layoutXProperty());
        ResourceUsage.areaChart_net.layoutYProperty().bind(rectangle4.layoutYProperty());
        ResourceUsage.areaChart_cpu.layoutXProperty().bind(rectangle2.layoutXProperty());
        ResourceUsage.areaChart_cpu.layoutYProperty().bind(rectangle2.layoutYProperty());
        ResourceUsage.areaChart_ram.layoutXProperty().bind(rectangle3.layoutXProperty());
        ResourceUsage.areaChart_ram.layoutYProperty().bind(rectangle3.layoutYProperty());

// Font Size Bindings
        iconView.fitWidthProperty().addListener((obs, oldVal, newVal) -> { //iconView is 40px
            title.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2).get()));
            subTitle.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(1.333333333333333).get()));
            serverTitle.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            ip.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(4).get()));
            ipValue.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(4).get()));
            cpu.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            cpuValue.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            ram.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            ramValue.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            disk.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            diskValue.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            status.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
            statusValue.setFont(Font.font("UNISPACE", iconView.fitWidthProperty().divide(2.666666666666667).get()));
        });
        start.widthProperty().addListener((obs, oldVal, newVal) -> {
            start.setFont(Font.font("UNISPACE", start.widthProperty().divide(6.571428571428571).get()));
            stop.setFont(Font.font("UNISPACE", start.widthProperty().divide(6.571428571428571).get()));
        });

        fileManager.widthProperty().addListener((obs, oldVal, newVal) -> fileManager.setFont(Font.font("UNISPACE", fileManager.widthProperty().divide(9).get())));

// Min and Max Size
        primaryStage.setMinWidth(width/2);
        primaryStage.setMinHeight(height/2);

// Action Events
        start.setOnAction((ActionEvent event) -> {
            start.setDisable(true);
            if (start.getText().equalsIgnoreCase("start")) {
                if (Panel_GUI.restart) {
                    Panel_GUI.restart = false;
                }
                else {
                    console.clear();
                }
                stop.setText("Kill");
                stop.setDisable(false);
                statusValue.setText("Starting");
                statusValue.setFill(Color.rgb(255, 165, 0));
                new Thread(() -> new ServerInitialization(console, commandField)).start();
            }
            else if(start.getText().equalsIgnoreCase("restart")) {
                restart = true;
                commandField.clear();
                commandField.setText("stop");
                commandField.fireEvent(new ActionEvent());
            }
        });
        stop.setOnAction((ActionEvent event) -> {
            stop.setDisable(true);
            if (stop.getText().equalsIgnoreCase("kill")) {
                try {
                    Runtime.getRuntime().exec("taskkill /F /PID " + ServerInitialization.pid);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
            else if(stop.getText().equalsIgnoreCase("stop")) {
                commandField.clear();
                commandField.setText("stop");
                commandField.fireEvent(new ActionEvent());
            }
        });
        //TODO: Add file manager functionality
        fileManager.setOnAction((ActionEvent event) -> {
            System.out.println(serverTitle.layoutYProperty());
        });
        copyIP.setOnAction((ActionEvent event) -> {
            StringSelection stringSelection = new StringSelection(ipValue.getText());
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
            timeline.getKeyFrames().add(new javafx.animation.KeyFrame(javafx.util.Duration.millis(100),
                    new KeyValue(copyView.opacityProperty(), 0),
                    new KeyValue(tickView.opacityProperty(), 1)
            ));
            PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(1));
            timeline.setOnFinished((javafx.event.ActionEvent event1) -> {
                copyIP.setGraphic(tickView);
                pause.play();
            });
            pause.setOnFinished((javafx.event.ActionEvent event2) -> {
                timeline2.getKeyFrames().add(new javafx.animation.KeyFrame(javafx.util.Duration.millis(100),
                        new KeyValue(copyView.opacityProperty(), 1),
                        new KeyValue(tickView.opacityProperty(), 0)
                ));
                timeline2.play();
            });
            timeline2.setOnFinished((javafx.event.ActionEvent event3) -> {
                copyIP.setGraphic(copyView);
            });
            timeline.play();
        });

// Misc
        new ResourceUsage();
        console.editableProperty().set(false);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        iconView.setCache(true);
        copyView.setPreserveRatio(true);
        copyView.setSmooth(true);
        copyView.setCache(true);
        tickView.setPreserveRatio(true);
        tickView.setSmooth(true);
        tickView.setCache(true);
        tickView.setOpacity(0);
        root.setContent(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("MC Admin Panel");
        primaryStage.getIcons().add(icon);
        pane.getChildren().addAll(start, stop, title, iconView, subTitle, fileManager, console, commandField, serverTitle,ip, ipValue, cpu, cpuValue, ram, ramValue, disk, diskValue, status, statusValue, copyIP, rectangle1, rectangle2, rectangle3, rectangle4, ResourceUsage.areaChart_net, ResourceUsage.areaChart_cpu, ResourceUsage.areaChart_ram);
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent fxmlRoot;
        try {
            fxmlRoot = loader.load();
        } catch (IOException | IllegalStateException e) {
            System.out.println("Error loading FXML file" + e.getMessage() + " " + e.getCause());
            return;
        }
        pane.getChildren().add(fxmlRoot);
        */
        primaryStage.setOnCloseRequest((event) -> {
            ResourceUsage.stopUpdate();
            System.exit(0);
        });
    }
    public void buttonStyle(Button button) {
        button.setAlignment(javafx.geometry.Pos.CENTER);
        button.setTextFill(Color.rgb(82, 183, 136));
        button.setStyle("-fx-background-color: transparent; -fx-border-color: rgb(82, 183, 136); -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        button.setOnMouseEntered((javafx.scene.input.MouseEvent event) -> {
            button.setTextFill(Color.rgb(1, 33, 24));
            button.setStyle("-fx-background-color: rgb(45, 106, 79); -fx-border-color: rgb(45, 106, 79); -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        });
        button.setOnMouseExited((javafx.scene.input.MouseEvent event) -> {
            button.setTextFill(Color.rgb(82, 183, 136));
            button.setStyle("-fx-background-color: transparent; -fx-border-color: rgb(82, 183, 136); -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        });
        button.setCursor(Cursor.HAND);
    }
    public String getSystemIP() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown Host";
        }
    }
}