package Updater.Util;

import Updater.Tennis.Game;
import Updater.Tennis.Player;
import Updater.Tennis.Tournament;
import Updater.sqlite.SQLiteJDBC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;

import static Updater.Util.MyLogger.log;
import static Updater.Util.MyLogger.logError;
import static Updater.Util.MyLogger.writeError;

/**
 * Created by Brandon on 1/20/2017.
 */
public class TableParser implements Runnable {

    private static String cookie;
    private Player player;

    public TableParser(Player player) {
        this.player = player;
    }

    public static void main(String args[]) {
        cookie = "visid_incap_178373=XmPUMP8NQu6zNPDQZxCQHxk/eVgAAAAAQkIPAAAAAACAAKZ5AUL9vR6Dy2KdIHqlCqR4F9awRDHk; incap_ses_544_178373=d7SBfZ5rUndNIT1xPK2MB38rllgAAAAANFzPHzDKchFw5eXODnMiQA==";
        Player player = new Player();
        player.setPlayerID(100216544);
        parseBiography(player);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        SQLiteJDBC.addPlayers(players);
    }

    public static void parseBiography(Player player) {
        try {

            CookieManager cm = new CookieManager();
            URL url = new URL("http://www.itftennis.com/juniors/players/player/profile.aspx?playerid=" +player.getPlayerID());
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            conn.setRequestProperty("Cookie", cookie);

            conn.connect();
            cm.storeCookies(conn);
            cm.setCookies(url.openConnection());




//            URL url2 = new URL("http://www.itftennis.com/juniors/players/player/profile.aspx?playerid=" +playerID +"/");
//            conn = url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String inputLine;
//            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
//
            String inputLine;
            StringBuilder biographySB = new StringBuilder();
            StringBuilder tournamentSB = new StringBuilder();
            boolean beginReading = false;
            boolean lookForTournamentData = false;

            int opened = 0;
            boolean possibleTournament = false;

            while ((inputLine = in.readLine()) != null) {


                if (inputLine.contains("class=\"pFemale\"")) {
                    player.setGender("Female");
                }

                if (inputLine.contains("class=\"pMale\"")) {
                    player.setGender("Male");
                }

//                System.out.println(inputLine);
                if (inputLine.contains("Name :")) {
                    inputLine = cleanHTML(inputLine);
                    String name = inputLine.split(":")[1];
                    name = removeHTML(name);
                    player.setName(name);
//                    System.out.println("Name: " +name);
                }

                if (inputLine.contains("Age:")) {
                    try {
                        inputLine = cleanHTML(inputLine);
                        String age = inputLine.split(":")[1];
                        age = removeHTML(age);
                        String data[] = age.split(" ");
                        int day = Integer.parseInt(data[0]);
                        int month = getMonth(data[1]);
                        int year = Integer.parseInt(data[2]);
//                        System.out.println("Age: " + month + "/" + day + "/" + year);
                        player.setAge(LocalDate.of(year, month, day));
                    } catch (Exception ignored) {}
                }

                if (inputLine.contains("Nationality:")) {
                    try {
                        String nationality = inputLine.split(":")[1];
                        nationality = removeHTML(nationality);
                        player.setNationality(nationality);
//                        System.out.println("Nationality: " +nationality);
                    } catch (Exception ignored) {}
                }

                if (inputLine.contains("Plays:")) {
                    try {
                        String plays = inputLine.split(":")[1];
                        plays = removeHTML(plays);
                        player.setPlays(plays);
//                        System.out.println("Plays: " +plays);
                    } catch (Exception ignored) {}
                }

                //This is used because seriously... who the hell makes HTML tables like this?
                if (inputLine.contains("pn-Gradient")) {
//                    System.out.println("Reading");
                    beginReading = true;
                }

                if (inputLine.contains("ActivityDiv")) {
                    lookForTournamentData = true;
                }

                if (lookForTournamentData && inputLine.contains("<ul")) {
                    possibleTournament = true;
                    opened++;
//                    System.out.println("Opened: " +opened);
                }

                if (opened >= 1) {
                    tournamentSB.append(inputLine).append("\n");
                }

                if (possibleTournament && inputLine.contains("</ul>")) {
                    opened--;
//                    System.out.println("Opened : " +opened);
                }

                if (beginReading) {
                    biographySB.append(inputLine).append("\n");
                }

                if (inputLine.contains("</li>")) {
                    String cleaned = removeHTML(biographySB.toString());
                    cleaned = cleaned.replaceAll("(?m)^[ \t]*\r?\n", "");
                    if (cleaned.length() > 1) {
//                        System.out.println("Cleaned: " + cleaned);
                        if (cleaned.contains("Ranking") && cleaned.contains("Current Combined")) {
//                            System.out.println("Cleaned: " + cleaned);
                            String currentCombined = cleaned.substring(cleaned.indexOf("Current Combined")+16, cleaned.indexOf("Career High Combined")).replaceAll("\n", "").replaceAll(" ", "");
                            String careerHighCombined = cleaned.substring(cleaned.indexOf("Career High Combined")+20, cleaned.indexOf("Career High Singles")).replaceAll("\n", "").replaceAll(" ", "");
                            String careerHighSingles = cleaned.substring(cleaned.indexOf("Career High Singles")+19, cleaned.indexOf("Career High Doubles")).replaceAll("\n", "").replaceAll(" ", "");
                            String careerHighDoubles = cleaned.substring(cleaned.indexOf("Career High Doubles")+19, cleaned.indexOf("Combined Year End")).replaceAll("\n", "").replaceAll(" ", "");
                            String careerYearEnd = cleaned.substring(cleaned.indexOf("Combined Year End")+17).replaceAll("\n", "").replaceAll(" ", "");
//                            System.out.println("Current Combined: " +currentCombined);
//                            System.out.println("career High Combined: " +careerHighCombined);
//                            System.out.println("Career High Singles: " +careerHighSingles);
//                            System.out.println("Career High Doubles: " +careerHighDoubles);
//                            System.out.println("Career Year End: " +careerYearEnd);

                            player.setCurrentCombined(currentCombined);
                            player.setCareerHighCombined(careerHighCombined);
                            player.setCareerHighSingles(careerHighSingles);
                            player.setCareerHighDoubles(careerHighDoubles);
                            player.setCareerYearEnd(careerYearEnd);
                        }

                        if (cleaned.contains("Win-Loss")) {
//                            System.out.println("Cleaned: " + cleaned);
                            //Have to check for array length because some people have nothing here. Not even 0
                            int currentYearSinglesWin;
                            int currentYearSinglesLose;
                            String currentYearSingles = cleaned.substring(cleaned.indexOf("Current Year Singles")+20, cleaned.indexOf("Current Year Doubles"));
                            String parsed[] = currentYearSingles.split("\n");
//                            System.out.println("Parsed: " + Arrays.toString(parsed));
                            if (parsed.length >= 3) {
                                currentYearSinglesWin = getIntegerFromString(parsed[1]);
                                currentYearSinglesLose = getIntegerFromString(parsed[2]);
                            } else {
                                currentYearSinglesWin = 0;
                                currentYearSinglesLose = 0;
                            }
                            int currentYearDoublesWin;
                            int currentYearDoublesLose;
                            String currentYearDoubles = cleaned.substring(cleaned.indexOf("Current Year Doubles")+20, cleaned.indexOf("Career Singles"));
                            parsed = currentYearDoubles.split("\n");
                            if (parsed.length >= 3) {
                                currentYearDoublesWin = getIntegerFromString(parsed[1]);
                                currentYearDoublesLose = getIntegerFromString(parsed[2]);
                            } else {
                                currentYearDoublesWin = 0;
                                currentYearDoublesLose = 0;
                            }
                            String careerSingles= cleaned.substring(cleaned.indexOf("Career Singles")+14, cleaned.indexOf("Career Doubles"));
                            parsed = careerSingles.split("\n");
                            int careerSinglesWin;
                            int careerSinglesLose;
                            if (parsed.length >= 3) {
                                careerSinglesWin = getIntegerFromString(parsed[1]);
                                careerSinglesLose = getIntegerFromString(parsed[2]);
                            } else {
                                careerSinglesWin = 0;
                                careerSinglesLose = 0;
                            }
                            String careerDoubles= cleaned.substring(cleaned.indexOf("Career Doubles"));
                            parsed = careerDoubles.split("\n");
                            int careerDoublesWin;
                            int careerDoublesLose;
                            if (parsed.length >= 3) {
                                careerDoublesWin = getIntegerFromString(parsed[1]);
                                careerDoublesLose = getIntegerFromString(parsed[2]);
                            } else {
                                careerDoublesWin = 0;
                                careerDoublesLose = 0;
                            }
                            player.setCurrentYearSinglesWin(currentYearSinglesWin);
                            player.setCurrentYearSinglesLose(currentYearSinglesLose);
                            player.setCurrentYearDoublesWin(currentYearDoublesWin);
                            player.setCurrentYearDoublesLose(currentYearDoublesLose);
                            player.setCareerSinglesWin(careerSinglesWin);
                            player.setCareerSinglesLose(careerSinglesLose);
                            player.setCareerDoublesWin(careerDoublesWin);
                            player.setCareerDoublesLose(careerDoublesLose);
//                            System.out.println("Current year singles win: " +currentYearSinglesWin);
//                            System.out.println("Current year singles lose: " +currentYearSinglesLose);
//                            System.out.println("Current year doubles win: " +currentYearDoublesWin);
//                            System.out.println("Current year doubles lose: " +currentYearDoublesLose);
//                            System.out.println("Career singles win: " +careerSinglesWin);
//                            System.out.println("Career singles lose: " +careerSinglesLose);
//                            System.out.println("Career doubles win: " +careerDoublesWin);
//                            System.out.println("Career doubles lose: " +careerDoublesLose);

                        }
                    }

                    biographySB = new StringBuilder();
                    beginReading = false;
                }

                if (possibleTournament && opened == 0) {
//                        System.out.println("Read: " +tournamentSB.toString());

                    String formatting = tournamentSB.toString();
                    formatting = formatting.replaceAll("<a id=\"lnkTournament\"", "Tournament:<");
                    formatting = formatting.replaceAll("<li class=\"liVenue\">\n", "Venue:");
                    formatting = formatting.replaceAll("<li class=\"liDates\">\n", "Date:");
                    formatting = formatting.replaceAll("<ul class=\"ulMatch\">\n", "MatchData:");
                    formatting = formatting.replaceAll("<th scope=\"col\">", "Column:");

                    String clean = removeHTML(formatting);
                    clean = clean.replaceAll("(?m)^[ \t]*\r?\n", "");

//                    if (clean.length() > 1 && clean.contains("Grade ")) {
//                        System.out.println("Clean: " +clean);
//                    }

                    Scanner tournamentData = new Scanner(clean);
                    Tournament currentTournament = new Tournament();
                    Game currentGame = null;

                    String line;
                    while (tournamentData.hasNextLine() && (line = tournamentData.nextLine()) != null) {

                        if (line.contains("Tournament:")) {
                            String text = line.split("Tournament:")[1].trim();
                            currentTournament = player.getTournament(text);
                        }
                        if (line.contains("Venue:")) {
                            String text = line.split("Venue:")[1].trim();
                            currentTournament.setVenue(text);
                        }
                        if (line.contains("Date:")) {
                            String text = line.split("Date:")[1].trim();
                            currentTournament.setVenue(text);
                        }

                        if (line.contains("MatchData:")) {
                            currentGame = currentTournament.addNewGame();
                            String numberOfPlayers = tournamentData.nextLine().trim();
                            String grade = tournamentData.nextLine().trim();
                            String entry = tournamentData.nextLine().trim();
                            String surface = tournamentData.nextLine().trim();
                            currentGame.setNumberOfPlayers(numberOfPlayers);
                            currentGame.setGrade(grade);
                            currentGame.setEntry(entry);
                            currentGame.setSurface(surface);

//                            System.out.println("Game added: ");
//                            System.out.println("Number of players:" +numberOfPlayers);
//                            System.out.println("Grade: " +grade);
//                            System.out.println("Entry: " +entry);
//                            System.out.println("Surface: " +surface);
                        }

                        if (line.contains("Main Draw")) {
                            boolean continueParsing = true;

//                            System.out.println("<=============== Started matches here ====================>");
                            String nextLine = "HOLDER";
                            while (!nextLine.contains("MatchData") && !nextLine.contains("Tournament")) {
                                String matchType = nextLine.equals("HOLDER") ? tournamentData.nextLine().trim() : nextLine.trim(); // 64, 32, 16, QF, SF, FR
                                String record = tournamentData.nextLine().trim(); //W for win, L for lose
                                if (!record.equals("BYE") && (record.equalsIgnoreCase("W") || record.equalsIgnoreCase("L"))) {
                                    String partnerData = null; //H2H if no partner
                                    String opponentName = null; //If doubles, both opponents here
                                    if (currentGame != null) {
                                        if (currentGame.getNumberOfPlayers().equals("Singles")) {
                                            partnerData = tournamentData.nextLine().trim();
                                            tournamentData.nextLine();
                                        } else if (currentGame.getNumberOfPlayers().equals("Doubles")) {
                                            tournamentData.nextLine();
                                            partnerData = tournamentData.nextLine().trim();
                                            tournamentData.nextLine();
                                            tournamentData.nextLine();
                                        }

                                        if (currentGame.getNumberOfPlayers().equals("Singles")) {
                                            opponentName = tournamentData.nextLine().trim();
                                            tournamentData.nextLine();
                                        } else if (currentGame.getNumberOfPlayers().equals("Doubles")) {
                                            opponentName = tournamentData.nextLine().trim();
                                            tournamentData.nextLine();
                                            opponentName += tournamentData.nextLine().trim();
                                            tournamentData.nextLine();
                                        }
                                    }
                                    String score = tournamentData.nextLine().trim(); //ex, 6-0 6-1

                                    if (currentGame != null) {
                                        currentGame.addMatch(matchType, record, partnerData, opponentName, score);
//                                        System.out.println("Match added:(" + currentGame.getNumberOfPlayers() +")");
//                                        System.out.println("Match type: " +matchType);
//                                        System.out.println("Record: " +record);
//                                        System.out.println("Partner Data: " +partnerData);
//                                        System.out.println("Opponent name: " +opponentName);
//                                        System.out.println("score: " +score);
                                    }
                                } else {
                                    if (record.equals("BYE")) {
                                        currentGame.addMatch(matchType, "", "", "", "BYE");
                                    } else if (record.equalsIgnoreCase("W") || record.equalsIgnoreCase("L")) {
                                        //Game hasn't been played yet!
                                        currentGame.addMatch(matchType, "TBA", "TBA", "TBA", "TBA");
                                    }
                                }

                                if (tournamentData.hasNextLine()) {
                                    nextLine = tournamentData.nextLine();

                                    if (nextLine.contains("Tournament:")) {
                                        String text = nextLine.split("Tournament:")[1].trim();
                                        currentTournament = player.getTournament(text);
                                    }
                                    if (nextLine.contains("Venue:")) {
                                        String text = nextLine.split("Venue:")[1].trim();
                                        currentTournament.setVenue(text);
                                    }
                                    if (nextLine.contains("Date:")) {
                                        String text = nextLine.split("Date:")[1].trim();
                                        currentTournament.setVenue(text);
                                    }

                                    if (nextLine.contains("MatchData:")) {
                                        currentGame = currentTournament.addNewGame();
                                        String numberOfPlayers = tournamentData.nextLine().trim();
                                        String grade = tournamentData.nextLine().trim();
                                        String entry = tournamentData.nextLine().trim();
                                        String surface = tournamentData.nextLine().trim();
                                        currentGame.setNumberOfPlayers(numberOfPlayers);
                                        currentGame.setGrade(grade);
                                        currentGame.setEntry(entry);
                                        currentGame.setSurface(surface);

//                                        System.out.println("Game added: ");
//                                        System.out.println("Number of players:" + numberOfPlayers);
//                                        System.out.println("Grade: " + grade);
//                                        System.out.println("Entry: " + entry);
//                                        System.out.println("Surface: " + surface);
                                    }
                                } else {
                                    break;
                                }

                            }

//                            System.out.println("<=============== Stopped matches here ====================>");

                        }

                    }

                    tournamentSB = new StringBuilder();
                    lookForTournamentData = false;
                    possibleTournament = false;
                }
            }
            in.close();

//            System.out.println("Tournament size: " +player.getTournaments().size());
//            for(Tournament tourny : player.getTournaments()) {
//                System.out.println("Game size: " +tourny.getGames().size());
//                for (Game game : tourny.getGames()) {
//                    System.out.println("Match size: " +game.getMatches().size());
//                }
//            }

        } catch (IOException e) {

            String message = e.getMessage();
            if (message.contains("reset") || message.contains("response code: 500") || message.contains("Connection timed out")) {
                //These can be ignored
                new Thread(new TableParser(player)).start();
            } else {
                log("Error on playerID: " +"http://www.itftennis.com/juniors/players/player/profile.aspx?playerid=" + player.getPlayerID());
                e.printStackTrace();
                logError(e);
                writeError(e);
            }
        }
    }

    private static String cleanHTML(String input) {
        input = input.replaceAll("/", "");
        input = input.replaceAll("<span>", "");
        input = input.replaceAll("<strong>" , "");
        return input;
    }

    private static String removeHTML(String input) {
        return input.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "");
    }

    private static int getMonth(String month) {
        switch (month) {
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
        }
        return 1;
    }

    private static  int getIntegerFromString(String text) {
        text = text.trim();
        if (isNumeric(text)) {
            return Integer.parseInt(text);
        }
        return 0;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.equals(""))
            return false;
        for (char c : str.toCharArray())
            if (c < '0' || c > '9')
                return false;
        return true;
    }

    public static String getCookie() {
        return cookie;
    }

    public static void setCookie(String cookie) {
        TableParser.cookie = cookie;
    }

    @Override
    public void run() {
        try {
            parseBiography(player);
        } catch (Exception e) {
            System.out.println("Error on player ID: " +player.getPlayerID());
            e.printStackTrace();
        }
    }
}
