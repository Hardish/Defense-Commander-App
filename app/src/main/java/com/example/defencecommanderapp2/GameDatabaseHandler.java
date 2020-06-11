package com.example.defencecommanderapp2;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GameDatabaseHandler extends AsyncTask<String, Void, String> {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String TAG = "GameDatabaseHandler";
    private static final String Table = "AppScores";
    private static String dbURL;
    private Connection conn;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private String Username = "chri5558_student";
    private String Password = "ABC.123";
    private EndActivity endActivity;
    private boolean operationFlag = false;


    GameDatabaseHandler(EndActivity endActivity)
    {
        this.endActivity = endActivity;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (operationFlag != true && s != null)
        {
            endActivity.setResult(s);
        }
        else
        {
            endActivity.updateScoreInTable();
        }
    }

    @Override
    protected String doInBackground(String... values) {

        int current_score = Integer.parseInt(values[0]);
        Log.d(TAG, "doInBackground: "+operationFlag);
        try {
            Class.forName(JDBC_DRIVER); //load the jdbc driver from jar file
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");
            StringBuilder sb = new StringBuilder();
            if(values.length == 3)
            {
                String initial_name = values[1];
                int current_level = Integer.parseInt(values[2]);
                Log.d(TAG, "doInBackground: " + initial_name + current_level) ;
                insertDataInTopTen(current_level,current_score,initial_name);
                sb.append(getTopTen(current_score));
                return  sb.toString();
            }
            else
            {
                if(values.length == 1)
                {
                    sb.append(getTopTen(current_score, values.length));
                    return  sb.toString();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertDataInTopTen(int current_level, int current_score, String initial_name) throws SQLException {
        //INSERT INTO AppScores VALUES (1582770779811, 'SRH', 11, 2)
        Statement stmt = conn.createStatement();
        String insertString = "insert into "+ Table + " values (" +  System.currentTimeMillis() +
                ", '" + initial_name + "', " +
                current_score + ", " +
                current_level + ")";

        Log.d(TAG, "insertDataInTopTen: "+insertString);

        int result = stmt.executeUpdate(insertString);
        stmt.close();
    }

    private String getTopTen(int current_score, int length) throws SQLException {
        //i am checking the score of the last 10th element
        Statement stmt = conn.createStatement();
        String sql = "select * from " + Table + " order by Score DESC LIMIT 10";
        StringBuilder sb = new StringBuilder();
        ResultSet rs = stmt.executeQuery(sql);
        rs.last();
        int lastScore = rs.getInt(3);
        if((lastScore >= current_score && length == 1) || length == 3)
        {
            // fetchRecordsFromTable();
            sb.append(String.format(Locale.getDefault(),"%-2s %-12s %-6s %-6s %-12s%n","#","Init","Level","Score","Date/Time"));
            int index = 0;
            //just show the exit screen
            Log.d(TAG, "getTopTen: Last Score is greater");
            ResultSet rs_10 = stmt.executeQuery(sql);
            while (rs_10.next()) {
                long millis = rs_10.getLong(1);
                String initial = rs_10.getString(2);
                int score = rs_10.getInt(3);
                int level = rs_10.getInt(4);
                index = index + 1;
                Log.d(TAG, "getTopTen: "+current_score);
                Log.d(TAG, "getTopTen: "+ index+ millis + initial + score + level );

                sb.append(String.format(Locale.getDefault(),"%-2d %-12s %-6d %-6d %-12s%n",index,initial,level,score,sdf.format(new Date(millis))));

            }
            rs_10.close();

            return sb.toString();
        }

        else
        {
            Log.d(TAG, "getTopTen: we have to add user");
            //turn on flag
            operationFlag = true;

        }

        rs.close();
        stmt.close();

        return null;
    }

    private void fetchRecordsFromTable() {
    }

    private String getTopTen(int current_score) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "select * from " + Table + " order by Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);

        sb.append(String.format(Locale.getDefault(),"%-2s %-12s %-6s %-6s %-12s%n","#","Init","Level","Score","Date/Time"));
        int index = 0;

        while (rs.next()) {
            long millis = rs.getLong(1);
            String initial = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            index = index + 1;
            Log.d(TAG, "getTopTen: "+current_score);
            Log.d(TAG, "getTopTen: "+ index+ millis + initial + score + level );

            sb.append(String.format(Locale.getDefault(),"%-2d %-12s %-6d %-6d %-12s%n",index,initial,level,score,sdf.format(new Date(millis))));
        }

        rs.close();
        stmt.close();
        Log.d(TAG, "getTopTen: "+  sb.toString());
        return sb.toString();
    }


}
