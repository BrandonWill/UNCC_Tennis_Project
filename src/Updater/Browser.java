package Updater;

/**
 * Created by Brandon on 1/20/2017.
 */

import Updater.GUI.BrowserFrame;
import Updater.Util.MyLogger;
import Updater.Tennis.Player;
import Updater.Util.TableParser;
import Updater.sqlite.SQLiteJDBC;
import com.sun.javafx.application.PlatformImpl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;

import javax.swing.*;

import static Updater.Util.MyLogger.log;
import static Updater.Util.MyLogger.logError;
import static Updater.Util.MyLogger.writeError;

public class Browser extends JFrame {

    private static BrowserFrame browserFrame;
    private static WebView webView;
    private static JFXPanel jfxPanel;
    private static BOT_STATE state;
    private static BOT_LOCATION location;
    private static HashSet<Player> players = new HashSet<>();
    private static HashSet<Player> allPlayers = new HashSet<>();


    private static int currentChar = 65;
    private static int currentPlayerLocation = 0;


    public static void main(String... args) {
        double javaVersion = Double.parseDouble(Runtime.class.getPackage().getSpecificationVersion());
        //Checks to see if Java version is higher than 8.
        if (javaVersion >= 1.8) {
            state = BOT_STATE.FINDING_ALL_PLAYERS; //First thing it should do is update the ID list
            location = BOT_LOCATION.ENTER_LETTER; //First thing it should do is update the ID list
            new MyLogger(); //initiates the logger. This logs into the bot and the console
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Uses the system's look and feel. Without this, sometimes Nimbus theme appears for no reason and nothing is aligned.
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception ignored) {
                    }
                    browserFrame = new BrowserFrame();
                    browserFrame.setSize(new Dimension(1500, 800));
                    browserFrame.setVisible(true);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "Your current java version is: " + Runtime.class.getPackage().getSpecificationVersion() + "\n"
                    + "You need to have Java 8 or higher to run this program. In order to fix this: \n"
                    + "1. Uninstall older versions of Java\n"
                    + "2. Install the newest version of Java (Google download java)");
        }
    }

    public Browser() {
        initComponents();
    }

    private void initComponents() {
        new Thread(new TitleUpdater()).start(); //Updates the title accordingly
        jfxPanel = new JFXPanel();

        //Custom browser zooming. WebView doesn't currently have this.

        //Resets the zoom to 1:1 ratio
        Action resetZoom = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PlatformImpl.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (webView != null) {
                            webView.setScaleX(1);
                            webView.setScaleY(1);
                        }
                    }
                });
            }
        };

        jfxPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), "resetZoom");
        jfxPanel.getActionMap().put("resetZoom", resetZoom);

        //Zooms in by a factor of 1.1
        Action zoomIn = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PlatformImpl.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (webView != null) {
                            webView.setScaleX(webView.getScaleX() * 1.1);
                            webView.setScaleY(webView.getScaleY() * 1.1);
                        }
                    }
                });
            }
        };

        jfxPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "zoomIn");
        jfxPanel.getActionMap().put("zoomIn", zoomIn);

        //zooms out by a factor of 1.1
        Action zoomOut = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PlatformImpl.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (webView != null) {
                            webView.setScaleX(webView.getScaleX() / 1.1);
                            webView.setScaleY(webView.getScaleY() / 1.1);
                        }
                    }
                });
            }
        };

        jfxPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "zoomOut");
        jfxPanel.getActionMap().put("zoomOut", zoomOut);

        createScene();

    }

    /**
     * createScene
     * <p/>
     * Note: Key is that Scene needs to be created and run on "FX user thread"
     * NOT on the AWT-EventQueue Thread
     */
    private void createScene() {
        //Browser code

        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                //adds in the XHRHTMLRequest listener
                webView = new WebView();

                webView.setPrefWidth(1000);
                final WebEngine engine = webView.getEngine();
                webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

                log("Browser agent changed to latest chrome version. It's now: " + webView.getEngine().getUserAgent());
                webView.getEngine().getHistory().setMaxSize(3);
                //Every request received is handled by alerts!
                addAlerts();
                engine.documentProperty().addListener(new ChangeListener<Document>() {
                    @Override
                    public void changed(ObservableValue<? extends Document> ov, Document oldDoc, Document doc) {
                        //Every time a new page is loaded, this XHR request listener is added.
                        //It does take a small amount of time to be attached sadly, which means it misses 3 requests on the initial login.
                        //Perhaps this could be fixed by writing my own WebView class?
                        engine.executeScript("var s_ajaxListener = new Object();\n" +
                                "s_ajaxListener.tempOpen = window.XMLHttpRequest.prototype.open;\n" +
                                "s_ajaxListener.tempSend = window.XMLHttpRequest.prototype.send;\n" +
                                "s_ajaxListener.callback = function () {\n" +
                                "}\n" +
                                "\n" +
                                "window.XMLHttpRequest.prototype.open = function(a,b) {\n" +
                                "  if (!a) var a='';\n" +
                                "  if (!b) var b='';\n" +
                                "  s_ajaxListener.tempOpen.apply(this, arguments);\n" +
                                "  s_ajaxListener.method = a;  \n" +
                                "  s_ajaxListener.url = b;\n" +
                                "  if (a.toLowerCase() == 'get') {\n" +
                                "    s_ajaxListener.data = b.split('?');\n" +
                                "    s_ajaxListener.data = s_ajaxListener.data[1];\n" +
                                "  }\n" +
                                "}\n" +
                                "\n" +
                                "window.XMLHttpRequest.prototype.send = function(a,b) {\n" +
                                "  if (!a) var a='';\n" +
                                "  if (!b) var b='';\n" +
                                "  s_ajaxListener.tempSend.apply(this, arguments);\n" +
                                "  if(s_ajaxListener.method.toLowerCase() == 'post')s_ajaxListener.data = a;\n" +
                                "  s_ajaxListener.callback();\n" +
                                "}\n" +
                                "function readBody(xhr) {\n" +
                                "    var data;\n" +
                                "    if (!xhr.responseType || xhr.responseType === \"text\") {\n" +
                                "        data = xhr.responseText;\n" +
                                "    } else if (xhr.responseType === \"document\") {\n" +
                                "        data = xhr.responseXML;\n" +
                                "    } else {\n" +
                                "        data = xhr.response;\n" +
                                "    }\n" +
                                "    return data;\n" +
                                "}\n" +
                                "\n" +
                                "var oldXHR = window.XMLHttpRequest;\n" +
                                "\n" +
                                "function newXHR() {\n" +
                                "    var realXHR = new oldXHR();\n" +
                                "    realXHR.addEventListener(\"readystatechange\", function() {\n" +
                                "        if(realXHR.readyState==4 && realXHR.status==200){\n" +
                                "            alert(\"XHR Reader:\" +realXHR.status +readBody(realXHR) +\" URL:\" +s_ajaxListener.url);\n" +
                                "        }\n" +
                                "    }, false);\n" +
                                "    return realXHR;\n" +
                                "}\n" +
                                "window.XMLHttpRequest = newXHR;");

                        if (doc != null) {


                            if (state.equals(BOT_STATE.FINDING_ALL_PLAYERS)) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //enter in search parameter
                                        if (location.equals(BOT_LOCATION.ENTER_LETTER)) {
                                            if (doc.getElementById("ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_txtName") != null) {
                                                engine.executeScript("if (document.getElementById(\"ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_txtName\")) {\n" +
                                                        "\tdocument.getElementById(\"ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_txtName\").value = '" + (char) currentChar + "';\n" +
                                                        "}");
                                                location = BOT_LOCATION.SEARCH_LETTER;
                                            }

                                        }

                                        //click the search button
                                        if (location.equals(BOT_LOCATION.SEARCH_LETTER)) {
                                            if (doc.getElementById("ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_btnSearch") != null) {
                                                engine.executeScript("if (document.getElementById(\"ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_btnSearch\")) {\n" +
                                                        "\tdocument.getElementById(\"ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_btnSearch\").click();\n" +
                                                        "}");
                                                location = BOT_LOCATION.CLICKING_MORE;
                                            }
                                        }

                                        //click the more button
                                        if (location.equals(BOT_LOCATION.CLICKING_MORE)) {
                                            if (doc.getElementById("ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_btnMore") != null) {
                                                webView.getEngine().executeScript("document.documentElement.outerHTML");
                                                engine.executeScript("document.getElementsByName(\"ctl00$ctl00$ctl00$ctl00$ContentPlaceHolderDefault$ContentContainerCentre$BaselineContainerCentre$PlayerSearch_6$hfRecordsDisplayed\")[0].value = 99999;\n" +
                                                        "document.getElementById(\"ContentPlaceHolderDefault_ContentContainerCentre_BaselineContainerCentre_PlayerSearch_6_btnMore\").click();");
                                                location = BOT_LOCATION.WAITING_FOR_PLAYERS_RESPONSE;
                                            }
                                        }


                                    }
                                });
                            }
                        }

                    }
                });
                //This is a developer tool. If we suck at JS and there's an error executing, it'll tell us.
                engine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
                    @Override
                    public void changed(ObservableValue<? extends Throwable> ov, Throwable oldException, Throwable exception) {
                        logError(exception);
                    }
                });

                //Java FX beginning. Self-explanatory buttons

                GridPane inputGrid = new GridPane();
                inputGrid.setHgap(10);
                inputGrid.setVgap(10);

                //Loads firebug directly from their website so we can see errors in real time.
                Button botDebugger = new Button("Debugger");
                botDebugger.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                    @Override
                    public void handle(javafx.event.ActionEvent t) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                webView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
                            }
                        });

                    }
                });
                inputGrid.addRow(0, botDebugger);

                ProgressIndicator fxLoadProgress = new ProgressIndicator(0);
                fxLoadProgress.progressProperty().bind(webView.getEngine().getLoadWorker().progressProperty());
                fxLoadProgress.visibleProperty().bind(webView.getEngine().getLoadWorker().runningProperty());

                HBox loginPane = new HBox(10);
                loginPane.getChildren().setAll(
                        fxLoadProgress,
                        botDebugger
                );

                final VBox layout = new VBox(10);
                //CSS styling for the login layout
                layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
                layout.getChildren().addAll(
                        loginPane,
                        webView
                );
                //Ensures it will resize with the frame
                VBox.setVgrow(webView, Priority.ALWAYS);
                Scene scene = new Scene(layout);

                jfxPanel.setScene(scene);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        browserFrame.getjTabbedPane1().addTab("Browser", jfxPanel);
                        log("Browser added");
                    }
                });

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //enter in search parameter
                        engine.load("http://www.itftennis.com/juniors/players/player-search.aspx");
                    }
                });


            }
        });
    }

    //This is a sub-enum of state so everything can be organized
    private enum BOT_LOCATION {
        //FINDING_ALL_PLAYERS
        ENTER_LETTER,
        SEARCH_LETTER,
        VIEW_PAGE,
        CLICKING_MORE,
        FINISHED_LOADING_PLAYERS,

        WAITING_FOR_PAGE_SOURCE,
        WAITING_FOR_PLAYERS_RESPONSE //This waits for the website to respond with lots of data!
    }

    private enum BOT_STATE {
        FINDING_ALL_PLAYERS,
        READING_ALL_PLAYERS
    }

    private class TitleUpdater implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (state.equals(BOT_STATE.FINDING_ALL_PLAYERS)) {
                            browserFrame.setTitle(state.name() + "(" + (char) currentChar + ") -->" + location);
                        } else if (state.equals(BOT_STATE.READING_ALL_PLAYERS)) {
                            browserFrame.setTitle(state.name() +  "-->" + currentPlayerLocation + "/" +allPlayers.size());
                        }
                    }
                });

            }
        }
    }

    private boolean loadedPlayerData = false;
    private void addAlerts() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
                    @Override
                    public void handle(WebEvent<String> event) {
                        String data = event.getData();

                        //Reads all XHR events
                        if (!loadedPlayerData) {
                            loadPlayerData(data);
                        }


//                        log(data);

                    }
                });
            }
        });
    }

    private static String cleanHTML(String html) {
        return html.replaceAll("\"", "");
    }

    private void loadPlayerData(String html) {
        Scanner fileReader = new Scanner(html);
        while (fileReader.hasNextLine()) {
            Player player = new Player();
            String line = fileReader.nextLine();
            if (line.contains("<span class=")) {
                //next line should tell have "alt=", which is their country
                if (fileReader.hasNextLine()) {
                    line = cleanHTML(fileReader.nextLine());
//                    String data[] = line.split(" ");
//                    for (String split : data) {
//                        if (split.contains("alt=")) {
//                            String country[] = split.split("=");
//                            if (country.length >= 2) { // some people don't have a country?
//                                player.setCountry(country[1]);
//                            } else {
//                                player.setCountry("");
//                            }
//
//                        }
//                    }
                    if (fileReader.hasNextLine()) {
                        line = cleanHTML(fileReader.nextLine());
                        if (line.contains("a href=") && line.contains("class=")) {
                            line = line.substring(8); //distance for <a href= to be gone
                            String lastLine[] = line.split(" ");
                            player.setPlayerID(Integer.parseInt(lastLine[0].substring(lastLine[0].indexOf("=") + 1)));
                            players.add(player);
                        }
                    }
                }
            }
        }
        allPlayers.addAll(players);
//                System.out.println((char) i + " size: " + players.size());



        if (location == BOT_LOCATION.WAITING_FOR_PAGE_SOURCE) {
            log("Player size for " + (char) currentChar + " is " + players.size());
            log("Player size for all players are: " + allPlayers.size());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (currentChar < 90) {
                        webView.getEngine().load("http://www.itftennis.com/juniors/players/player-search.aspx");
                        currentChar++;
                        location = BOT_LOCATION.ENTER_LETTER;
                    } else {
                        location = BOT_LOCATION.FINISHED_LOADING_PLAYERS;
                        state = BOT_STATE.READING_ALL_PLAYERS;
                        String cookie = (String) webView.getEngine().executeScript("document.cookie");
                        TableParser.setCookie(cookie);

//                         Player playerToParse = new Player();
                        ArrayList<Thread> threads = new ArrayList<Thread>();
                        for (Player player : allPlayers) {
//                            System.out.println("ID: " +player.getPlayerID());
//                            TableParser.parseBiography(player);
                            Thread threadHolder = new Thread(new TableParser(player));
                            threads.add(threadHolder);
                            threadHolder.start();
                            currentPlayerLocation++;

                            if (currentPlayerLocation % 500 == 0) {
                                for (Thread thread : threads) {
                                    try {
                                        thread.join();
                                    } catch (InterruptedException e) {
                                        writeError(e);
                                        e.printStackTrace();
                                    }
                                }
                                threads.clear();
                                try {
                                    Thread.sleep(randInt(1000, 2000));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
//                            playerToParse = player;
//                            break;
                        }
//                        System.out.println("ID: " +playerToParse.getPlayerID());
//                        TableParser.parseBiography(playerToParse);

                        SQLiteJDBC.addPlayers(new ArrayList<>(allPlayers));
                        writeToFile(allPlayers);
                    }
                }
            });
        }

        if (location.equals(BOT_LOCATION.WAITING_FOR_PLAYERS_RESPONSE)) {
            location = BOT_LOCATION.WAITING_FOR_PAGE_SOURCE;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadPlayerData((String) webView.getEngine().executeScript("document.documentElement.outerHTML"));
                }
            });
        }

        players.clear();

    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    private static void writeToFile(HashSet<Player> players) {
        try {
            File file = new File("TennisPlayers2.txt");

            // if file doesn't exists, then create it
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }

            // true = append file
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (Player player : players) {
                bw.write("http://www.itftennis.com/juniors/players/player/profile.aspx?PlayerID=" + player.getPlayerID() +"\n");
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}