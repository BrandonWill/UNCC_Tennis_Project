package Updater.Tennis;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
/**
 * Created by Brandon on 1/13/2017.
 */
public class Player {
    private int playerID;
    private String name;
    private String gender;
    private String nationality;
    private String birthplace;
    private String plays;
    private String currentCombined;
    private String careerHighCombined;
    private String careerHighSingles;
    private String careerHighDoubles;
    private String careerYearEnd;

    private LocalDate age;


    private int currentYearSinglesWin;
    private int currentYearSinglesLose;
    private int currentYearDoublesWin;
    private int currentYearDoublesLose;
    private int careerSinglesWin;
    private int careerSinglesLose;
    private int careerDoublesWin;
    private int careerDoublesLose;

    private ArrayList<Tournament> tournaments = new ArrayList<>();

    public String getCurrentCombined() {
        return currentCombined;
    }

    public void setCurrentCombined(String currentCombined) {
        this.currentCombined = currentCombined;
    }

    public String getCareerHighCombined() {
        return careerHighCombined;
    }

    public void setCareerHighCombined(String careerHighCombined) {
        this.careerHighCombined = careerHighCombined;
    }

    public String getCareerHighSingles() {
        return careerHighSingles;
    }

    public void setCareerHighSingles(String careerHighSingles) {
        this.careerHighSingles = careerHighSingles;
    }

    public String getCareerHighDoubles() {
        return careerHighDoubles;
    }

    public void setCareerHighDoubles(String careerHighDoubles) {
        this.careerHighDoubles = careerHighDoubles;
    }

    public String getCareerYearEnd() {
        return careerYearEnd;
    }

    public void setCareerYearEnd(String careerYearEnd) {
        this.careerYearEnd = careerYearEnd;
    }


    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(ArrayList<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public LocalDate getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getActualAge() {
        LocalDate today = LocalDate.now();
        Period p = Period.between(age, today);
        return p.getYears();
    }

    public void setAge(LocalDate age) {
        this.age = age;
    }

    public String getPlays() {
        return plays;
    }

    public void setPlays(String plays) {
        this.plays = plays;
    }

    public int getCurrentYearSinglesWin() {
        return currentYearSinglesWin;
    }

    public void setCurrentYearSinglesWin(int currentYearSinglesWin) {
        this.currentYearSinglesWin = currentYearSinglesWin;
    }

    public int getCurrentYearSinglesLose() {
        return currentYearSinglesLose;
    }

    public void setCurrentYearSinglesLose(int currentYearSinglesLose) {
        this.currentYearSinglesLose = currentYearSinglesLose;
    }

    public int getCurrentYearDoublesWin() {
        return currentYearDoublesWin;
    }

    public void setCurrentYearDoublesWin(int currentYearDoublesWin) {
        this.currentYearDoublesWin = currentYearDoublesWin;
    }

    public int getCurrentYearDoublesLose() {
        return currentYearDoublesLose;
    }

    public void setCurrentYearDoublesLose(int currentYearDoublesLose) {
        this.currentYearDoublesLose = currentYearDoublesLose;
    }

    public int getCareerSinglesWin() {
        return careerSinglesWin;
    }

    public void setCareerSinglesWin(int careerSinglesWin) {
        this.careerSinglesWin = careerSinglesWin;
    }

    public int getCareerSinglesLose() {
        return careerSinglesLose;
    }

    public void setCareerSinglesLose(int careerSinglesLose) {
        this.careerSinglesLose = careerSinglesLose;
    }

    public int getCareerDoublesWin() {
        return careerDoublesWin;
    }

    public void setCareerDoublesWin(int careerDoublesWin) {
        this.careerDoublesWin = careerDoublesWin;
    }

    public int getCareerDoublesLose() {
        return careerDoublesLose;
    }

    public void setCareerDoublesLose(int careerDoublesLose) {
        this.careerDoublesLose = careerDoublesLose;
    }

    public Tournament getTournament(int tournamentID) {
        for (Tournament tourny : tournaments) {
            if (tourny.getID() == tournamentID) {
                return tourny;
            }
        }
        return null;
    }

    public Tournament getTournament(String tournamentName) {
        for (Tournament tourny : tournaments) {
            if (tourny.getName().equals(tournamentName)) {
                return tourny;
            }
        }
        tournaments.add(new Tournament(tournamentName));
        return getTournament(tournamentName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (playerID != player.playerID) return false;
        if (currentYearSinglesWin != player.currentYearSinglesWin) return false;
        if (currentYearSinglesLose != player.currentYearSinglesLose) return false;
        if (currentYearDoublesWin != player.currentYearDoublesWin) return false;
        if (currentYearDoublesLose != player.currentYearDoublesLose) return false;
        if (careerSinglesWin != player.careerSinglesWin) return false;
        if (careerSinglesLose != player.careerSinglesLose) return false;
        if (careerDoublesWin != player.careerDoublesWin) return false;
        if (careerDoublesLose != player.careerDoublesLose) return false;
        if (gender != null ? !gender.equals(player.gender) : player.gender != null) return false;
        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        if (nationality != null ? !nationality.equals(player.nationality) : player.nationality != null) return false;
        if (birthplace != null ? !birthplace.equals(player.birthplace) : player.birthplace != null) return false;
        if (plays != null ? !plays.equals(player.plays) : player.plays != null) return false;
        if (currentCombined != null ? !currentCombined.equals(player.currentCombined) : player.currentCombined != null)
            return false;
        if (careerHighCombined != null ? !careerHighCombined.equals(player.careerHighCombined) : player.careerHighCombined != null)
            return false;
        if (careerHighSingles != null ? !careerHighSingles.equals(player.careerHighSingles) : player.careerHighSingles != null)
            return false;
        if (careerHighDoubles != null ? !careerHighDoubles.equals(player.careerHighDoubles) : player.careerHighDoubles != null)
            return false;
        if (careerYearEnd != null ? !careerYearEnd.equals(player.careerYearEnd) : player.careerYearEnd != null)
            return false;
        if (age != null ? !age.equals(player.age) : player.age != null) return false;
        return tournaments != null ? tournaments.equals(player.tournaments) : player.tournaments == null;

    }

    @Override
    public int hashCode() {
        int result = gender != null ? gender.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
        result = 31 * result + (birthplace != null ? birthplace.hashCode() : 0);
        result = 31 * result + (plays != null ? plays.hashCode() : 0);
        result = 31 * result + (currentCombined != null ? currentCombined.hashCode() : 0);
        result = 31 * result + (careerHighCombined != null ? careerHighCombined.hashCode() : 0);
        result = 31 * result + (careerHighSingles != null ? careerHighSingles.hashCode() : 0);
        result = 31 * result + (careerHighDoubles != null ? careerHighDoubles.hashCode() : 0);
        result = 31 * result + (careerYearEnd != null ? careerYearEnd.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + playerID;
        result = 31 * result + currentYearSinglesWin;
        result = 31 * result + currentYearSinglesLose;
        result = 31 * result + currentYearDoublesWin;
        result = 31 * result + currentYearDoublesLose;
        result = 31 * result + careerSinglesWin;
        result = 31 * result + careerSinglesLose;
        result = 31 * result + careerDoublesWin;
        result = 31 * result + careerDoublesLose;
        result = 31 * result + (tournaments != null ? tournaments.hashCode() : 0);
        return result;
    }
}
