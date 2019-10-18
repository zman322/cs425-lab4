package edu.jsu.mcis.cs425.Lab4;

import com.opencsv.CSVReader;
import com.mysql.cj.protocol.Resultset;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Rates {
    
    public static final String RATE_FILENAME = "rates.csv";
    
    public static List<String[]> getRates(String path) {
        
        StringBuilder s = new StringBuilder();
        List<String[]> data = null;
        String line;
        
        try {
            
            /* Open Rates File; Attach BufferedReader */

            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            /* Get File Data */
            
            while((line = reader.readLine()) != null) {
                s.append(line).append('\n');
            }
            
            reader.close();
            
            /* Attach CSVReader; Parse File Data to List */
            
            CSVReader csvreader = new CSVReader(new StringReader(s.toString()));
            data = csvreader.readAll();
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return List */
        
        return data;
        
    }
    
    public static String getRatesAsTable(List<String[]> csv) {
        
        StringBuilder s = new StringBuilder();
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create HTML Table */
            
            s.append("<table>");
            
            while (iterator.hasNext()) {
                
                /* Create Row */
            
                row = iterator.next();
                s.append("<tr>");
                
                for (int i = 0; i < row.length; ++i) {
                    s.append("<td>").append(row[i]).append("</td>");
                }
                
                /* Close Row */
                
                s.append("</tr>");
            
            }
            
            /* Close Table */
            
            s.append("</table>");
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return Table */
        
        return (s.toString());
        
    }
    
    public static String getRatesAsJson(List<String[]> csv) {
        
        String results = "";
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create JSON Containers */
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();            
            
            /* 
             * Add rate data to "rates" container and add "date" and "base"
             * values to "json" container.  See the "getRatesAsTable()" method
             * for an example of how to get the CSV data from the list, and
             * don't forget to skip the header row!
             *
             * *** INSERT YOUR CODE HERE ***
             */
            String[] header = iterator.next();
            
            while(iterator.hasNext()) {
                row = iterator.next();
                rates.put(row[1], Double.parseDouble(row[2]));
                
                
            }
            json.put("rates", rates);
            json.put("base", "USD");
            json.put("date", "2019-09-30");
            
            /* Parse top-level container to a JSON string */
            
            results = JSONValue.toJSONString(json);
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return JSON string */
        System.err.println(results);
        return (results.trim());
        
    }
    
    public static String getRatesAsJson(String code) throws NamingException, SQLException{
        
        Context envContext = null, initContext = null;
        DataSource ds = null;
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;
        
        JSONObject json = new JSONObject();
        JSONObject rates = new JSONObject();
        String query;
        String results = "";
        boolean hasResults;
        
        try {
            
            envContext = new InitialContext();
            initContext  = (Context)envContext.lookup("java:/comp/env");
            ds = (DataSource)initContext.lookup("jdbc/db_pool");
            connection = ds.getConnection();
        }
        catch (SQLException e) {}
        
        
            if (code == null){
                query = "SELECT * FROM rates";
            }
            else{
                query = "SELECT * FROM rates WHERE code = ?";
      
            }
           
            pStatement = connection.prepareStatement(query);
            
            hasResults = pStatement.execute();
            
            
            
            if(hasResults){
                resultSet = pStatement.getResultSet();
                
                while(resultSet.next()){
                    
                    String codee = resultSet.getString("code");
                    Double rate = resultSet.getDouble("rates");
                    
                    rates.put(codee, rate);
                    
                }
            }
            
            json.put("rates", rates);
            json.put("date", "2019-09-30");
            json.put("base", "USD");
            
            results = JSONValue.toJSONString(json);
                
            
            
            
        
        
            
        
       
            connection.close();
               
            pStatement.close();
            
            resultSet.close();
            
        
        
        
        
        return results.trim();

    }
        
        
        
    

}
    
