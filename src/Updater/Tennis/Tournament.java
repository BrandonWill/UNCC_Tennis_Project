package Updater.Tennis;

import java.util.ArrayList;

/**
 * Created by Brandon on 1/20/2017.
 */
public class Tournament {
    private String name;
    private String date;
    private String venue;
    private ArrayList<Game> games = new ArrayList<>();
    private static int tournamentID = 0;
    private int ID;

    public Tournament() {
        tournamentID++;
        ID = tournamentID;
    };

    public Tournament(String tournamentName) {
        this.name = tournamentName;
        tournamentID++;
        ID = tournamentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public Game addNewGame() {
        games.add(new Game());
        return games.get(games.size()-1);
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public Game getGame(int gameID) {
        for (Game game : games) {
            if (game.getID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public int getID() {
        return ID;
    }
}
