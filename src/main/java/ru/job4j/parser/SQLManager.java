package ru.job4j.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;


public class SQLManager {
    private Connection connect;
    private HashSet<String> dbNames;
    private long lastTime;
    private static final Logger LOG = LoggerFactory.getLogger(SQLManager.class.getName());
    private final String url;
    private final String username;
    private final String password;

    /**
     * Returns the time of the last program launch
     * @return
     */
    public long getLastTime() {
        return lastTime;
    }

    public SQLManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * adds the map with vacations to the database
     * @param map
     */
    public void addNewVacations(Map<String, Vacation> map) {
        this.dbNames = getDataBaseVacationNames();
        map.forEach(this::addRecord);
    }

    /**
     * returns the list of the vacations' names which are in the database
     * then it is used for comparing with new vacations to avoid duplicates
     * @return
     */
    private HashSet<String> getDataBaseVacationNames() {
        HashSet<String> result = new HashSet<>();
        String query = "SELECT name FROM vacancy";
        try (PreparedStatement statement = this.connect.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * adds the vacation to the data base if there is no name duplication
     * @param name
     * @param vacation
     */
    private void addRecord(String name, Vacation vacation) {
        String query = "INSERT INTO vacancy(name, text, link) VALUES (?, ?, ?)";
        try (PreparedStatement ps = this.connect.prepareStatement(query)) {
            if (!this.dbNames.contains(name)) {
                ps.setString(1, vacation.getName());
                ps.setString(2, vacation.getText());
                ps.setString(3, vacation.getLink());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * initializes the connection to the data base and creates table if they do not exist
     * assigns the value of the last time program launch.
     * @return
     */
    public Connection init() {
        try {
            this.connect = DriverManager.getConnection(this.url, this.username, this.password);
            String query = "SELECT time FROM lastTime";
            PreparedStatement ps1 = this.connect.prepareStatement(query);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                this.lastTime = rs.getLong(1);
            } else {
                this.lastTime = readYearBegin();
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return this.connect;
    }

    /**
     * if the program has launched first time, the method assigns the date of the last
     * time program start as the year beginning
     * @return date in millis
     */
    private long readYearBegin() {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        date.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        return date.getTimeInMillis();
    }

    /**
     * writes the date and time of the current program launch as the last time program start.
     * @param time
     */
    public void writeLastTime(long time) {
        String yearBeginning = "UPDATE lastTime SET time = ?";
        try (PreparedStatement ps2 = this.connect.prepareStatement(yearBeginning)) {
            ps2.setLong(1, time);
            ps2.execute();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}


