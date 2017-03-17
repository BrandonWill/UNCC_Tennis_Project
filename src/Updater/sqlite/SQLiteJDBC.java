package Updater.sqlite;

import Updater.Tennis.Game;
import Updater.Tennis.Match;
import Updater.Tennis.Player;
import Updater.Tennis.Tournament;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Brandon on 2/3/2017.
 */
public class SQLiteJDBC {

    private static void connectToDatabase() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:TennisPlayers.db");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    private static void createTable() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:TennisPlayers.db");

            DatabaseMetaData dbm = c.getMetaData();

            stmt = c.createStatement();
            String sql = "CREATE TABLE PLAYERS " +
                    "(PLAYERID              BLOB PRIMARY KEY NOT NULL," +
                    "PLAYERNAME             BLOB," +
                    "GENDER                 BLOB," +
                    "NATIONALITY            BLOB," +
                    "BIRTHPLACE             BLOB," +
                    "PLAYS                  BLOB," +
                    "CURRENTCOMBINED        BLOB," +
                    "CAREERHIGHCOMBINED     BLOB," +
                    "CAREERHIGHSINGLES      BLOB," +
                    "CAREERHIGHDOUBLES      BLOB," +
                    "CAREERYEAREND          BLOB," +
                    "AGE                    BLOB," +
                    "CURRENTYEARSINGLESWIN  INT," +
                    "CURRENTYEARSINGLESLOSE INT," +
                    "CURRENTYEARDOUBLESWIN  INT," +
                    "CURRENTYEARDOUBLESLOSE INT," +
                    "CAREERSINGLESWIN       INT," +
                    "CAREERSINGLESLOSE      INT," +
                    "CAREERDOUBLESWIN       INT," +
                    "CAREERDOUBLESLOSE      INT," +
                    "TOURNAMENTS            BLOB)";
            stmt.executeUpdate(sql);

            stmt = c.createStatement();
            sql =   "CREATE TABLE TOURNAMENTS " +
                    "(TOURNAMENTID    INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME             BLOB," +
                    "TOURNAMENTDATE   BLOB," +
                    "VENUE            BLOB," +
                    "GAMES            BLOB)";
            stmt.executeUpdate(sql);

            stmt = c.createStatement();
            sql =   "CREATE TABLE GAMES " +
                    "(GAMEID          INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NUMBEROFPLAYERS  BLOB," +
                    "GRADE            BLOB," +
                    "ENTRY            BLOB," +
                    "SURFACE          BLOB," +
                    "MATCHES          BLOB)";
            stmt.executeUpdate(sql);

            stmt = c.createStatement();
            sql =   "CREATE TABLE MATCHES " +
                    "(MATCHID       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "MATCHTYPE      BLOB," +
                    "RECORD         BLOB," +
                    "OPPONENTNAME   BLOB," +
                    "SCORE          BLOB)";
            stmt.executeUpdate(sql);

            stmt.close();

            c.close();
        } catch ( Exception e ) {
            if (!e.toString().contains("exists")) {
                System.exit(0);
            }
        }
        System.out.println("Table created successfully");
    }

    public static void dropTables() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:TennisPlayers.db");

            stmt = c.createStatement();
            String sql = "DROP TABLE PLAYERS";
            stmt.executeUpdate(sql);
            stmt = c.createStatement();
            sql = "DROP TABLE TOURNAMENTS";
            stmt.executeUpdate(sql);
            stmt = c.createStatement();
            sql = "DROP TABLE GAMEs";
            stmt.executeUpdate(sql);
            stmt = c.createStatement();
            sql = "DROP TABLE MATCHES";
            stmt.executeUpdate(sql);
            stmt.close();

            c.close();
        } catch ( Exception ignored ) {

        }
    }

    public static void addPlayers(ArrayList<Player> players) {
        dropTables();

        connectToDatabase();
        createTable();

        Connection c = null;
        Statement stmt = null;
        try {
            for (Player player : players) {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:TennisPlayers.db");
                PreparedStatement preparedStatement = null;

//                stmt = c.createStatement();
                String sql = "INSERT INTO PLAYERS"
                        + "(PLAYERID, PLAYERNAME, GENDER, NATIONALITY, BIRTHPLACE, PLAYS, CURRENTCOMBINED, CAREERHIGHCOMBINED," +
                        " CAREERHIGHSINGLES, CAREERHIGHDOUBLES, CAREERYEAREND, AGE, CURRENTYEARSINGLESWIN, " +
                        " CURRENTYEARSINGLESLOSE, CURRENTYEARDOUBLESWIN, CURRENTYEARDOUBLESLOSE," +
                        " CAREERSINGLESWIN, CAREERSINGLESLOSE, CAREERDOUBLESWIN, CAREERDOUBLESLOSE," +
                        " TOURNAMENTS) VALUES" +
                        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try {
//                    stmt = c.createStatement();
                    preparedStatement = c.prepareStatement(sql);

                    preparedStatement.setString(1, String.valueOf(player.getPlayerID()));
                    preparedStatement.setString(2, player.getName());
                    preparedStatement.setString(3, player.getGender());
                    preparedStatement.setString(4, player.getNationality());
                    preparedStatement.setString(5, player.getBirthplace());
                    preparedStatement.setString(6, player.getPlays());
                    preparedStatement.setString(7, player.getCurrentCombined());
                    preparedStatement.setString(8, player.getCareerHighCombined());
                    preparedStatement.setString(9, player.getCareerHighSingles());
                    preparedStatement.setString(10, player.getCareerHighDoubles());
                    preparedStatement.setString(11, player.getCareerYearEnd());
                    preparedStatement.setString(12, String.valueOf(player.getAge()));
                    preparedStatement.setInt(13, player.getCurrentYearSinglesWin());
                    preparedStatement.setInt(14, player.getCurrentYearSinglesLose());
                    preparedStatement.setInt(15, player.getCurrentYearDoublesWin());
                    preparedStatement.setInt(16, player.getCurrentYearDoublesLose());
                    preparedStatement.setInt(17, player.getCareerSinglesWin());
                    preparedStatement.setInt(18, player.getCareerSinglesLose());
                    preparedStatement.setInt(19, player.getCareerDoublesWin());
                    preparedStatement.setInt(20, player.getCareerDoublesLose());

                    ArrayList<Integer> tournamentIDS = new ArrayList<>();
                    for(Tournament tournament : player.getTournaments()) {
                        tournamentIDS.add(tournament.getID());
                    }

                    preparedStatement.setString(21, tournamentIDS.toString());
                    preparedStatement.executeUpdate();

                    if (tournamentIDS.size() > 0 ) {
                        for(Integer tournamentID : tournamentIDS) {
                            sql = "INSERT INTO TOURNAMENTS" +
                                    "(NAME, TOURNAMENTDATE, VENUE, GAMES) VALUES" +
                                    "(?,?,?,?,?)";
                            Tournament tournament = player.getTournament(tournamentID);
                            preparedStatement = c.prepareStatement(sql);
//                            preparedStatement.setInt(1, tournamentID);
                            preparedStatement.setString(1, tournament.getName());
                            preparedStatement.setString(2, tournament.getDate());
                            preparedStatement.setString(3, tournament.getVenue());

                            ArrayList<Integer> gameIDs = new ArrayList<>();
                            for (Game game : tournament.getGames()) {
                                gameIDs.add(game.getID());
                            }
                            preparedStatement.setString(4, gameIDs.toString());
                            preparedStatement.executeUpdate();

                            for (Integer gameID : gameIDs) {
                                sql = "INSERT INTO GAMES" +
                                        "(NUMBEROFPLAYERS, GRADE, ENTRY, SURFACE, MATCHES) VALUES" +
                                        "(?,?,?,?,?,?)";
                                Game game = tournament.getGame(gameID);
                                preparedStatement = c.prepareStatement(sql);
//                                preparedStatement.setInt(1, gameID);
                                preparedStatement.setString(1, game.getNumberOfPlayers());
                                preparedStatement.setString(2, game.getGrade());
                                preparedStatement.setString(3, game.getGrade());
                                preparedStatement.setString(4, game.getSurface());

                                ArrayList<Integer> matchIDs = new ArrayList<>();
                                for (Match match : game.getMatches()) {
                                    matchIDs.add(match.getID());
                                }
                                preparedStatement.setString(5, matchIDs.toString());
                                preparedStatement.executeUpdate();

                                for (Integer matchID : matchIDs) {
                                    sql = "INSERT INTO MATCHES" +
                                            "(MATCHTYPE, RECORD, OPPONENTNAME, SCORE) VALUES" +
                                            "(?,?,?,?,?)";
                                    Match match = game.getMatch(matchID);
                                    preparedStatement = c.prepareStatement(sql);
//                                    preparedStatement.setInt(1, matchID);
                                    preparedStatement.setString(1, match.getMatchType());
                                    preparedStatement.setString(2, match.getRecord());
                                    preparedStatement.setString(3, match.getOpponentName());
                                    preparedStatement.setString(4, match.getScore());

                                    preparedStatement.executeUpdate();
                                }
                            }
                        }


                    }
                } catch (Exception e) {
//                            System.out.println("Product error SKU: " +product.getSKU());
                    e.printStackTrace();
                }
//                stmt.close();
                c.close();
            }

            c.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            if (!e.toString().contains("exists")) {
                System.exit(0);
            }
        }
        System.out.println("Added players successfully");


//        readingTest();
    }

    private static void readingTest() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:TennisPlayers.db");

            stmt = c.createStatement();
            ResultSet rs;

            rs = stmt.executeQuery("SELECT * FROM PLAYERS");
            System.out.println("Reading players");
            while ( rs.next() ) {
                int playerID = rs.getInt("PLAYERID");
                String name = rs.getString("PLAYERNAME");
                String gender = rs.getString("GENDER");
                ArrayList<Integer> tournamentIDs = stringToIntegerArray(rs.getString("TOURNAMENTS"));
                String age = rs.getString("AGE");
                System.out.println(playerID);
                System.out.println(name);
                System.out.println(gender);
                System.out.println(tournamentIDs);
                System.out.println(age);

                for (Integer tournamentID : tournamentIDs) {
                    stmt = c.createStatement();
                    ResultSet tournamentResult = stmt.executeQuery("SELECT * FROM TOURNAMENTS WHERE TOURNAMENTID =" +tournamentID);

                    while (tournamentResult.next()) {
                        int tournyID = tournamentResult.getInt("TOURNAMENTID");
                        String tournyName = tournamentResult.getString("NAME");

                        System.out.println("Tournament found: " + tournyID + " name:" + tournyName);
                    }

                }
            }

            c.close();

        } catch (Exception e) {
//                            System.out.println("Product error SKU: " +product.getSKU());
            e.printStackTrace();
        }
    }

    private static ArrayList<Integer> stringToIntegerArray(String string) {
        string = string.replaceAll(" ", "").replace("[", "").replace("]", "");
        String[] sArray = string.split(",");
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for(String s : sArray) {
            integerArrayList.add(Integer.parseInt(s));
        }
        return integerArrayList;
    }
}
