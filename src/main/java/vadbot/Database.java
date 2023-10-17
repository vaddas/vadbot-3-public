package vadbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

  private static String url;

  public static void setLogin(String login) {

    url = login;

  }

  public static String getFirst(String table, String column) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
      rs.next();
      return rs.getString(column);

    } catch (SQLException e) {

      e.printStackTrace();
      return "An unexpected error occurred while accessing the database.";

    }

  }

  public static ArrayList<String>
      getAllMatches(String table, String column, String idColumn, String ID) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT DISTINCT " + column + " FROM " + table + " WHERE " + idColumn
              + " = '" + ID + "'"
      );
      ArrayList<String> results = new ArrayList<>();

      while (rs.next())
        results.add(rs.getString(column));

      return results;

    } catch (SQLException e) {

      e.printStackTrace();
      return null;

    }

  }

  public static String
      getByID(String table, String column, String idColumnName, String ID) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM " + table + " WHERE " + idColumnName + " = " + ID
      );
      rs.next();

      if (rs.wasNull())
        return null;

      return rs.getString(column);

    } catch (SQLException e) {

      e.printStackTrace();
      return "An unexpected error occurred while accessing the database.";

    }

  }

  public static ArrayList<String> getAll(String table, String column) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
      ArrayList<String> results = new ArrayList<>();

      while (rs.next())
        results.add(rs.getString(column));

      return results;

    } catch (SQLException e) {

      e.printStackTrace();
      return null;

    }

  }

  public static ArrayList<String>
      getAll(String table, String column, String orderColumn, boolean desc) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM " + table + " ORDER BY " + orderColumn
              + (desc ? " DESC" : " ASC")
      );

      ArrayList<String> results = new ArrayList<>();

      while (rs.next())
        results.add(rs.getString(column));

      return results;

    } catch (SQLException e) {

      e.printStackTrace();
      return null;

    }

  }

  public static ArrayList<ArrayList<String>>
      getMatrix(String table, String... columns) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
      ArrayList<ArrayList<String>> results = new ArrayList<>();

      while (rs.next()) {

        ArrayList<String> entryArray = new ArrayList<>();

        for (String column : columns)
          entryArray.add(rs.getString(column));

        results.add(entryArray);

      }

      return results;

    } catch (SQLException e) {

      e.printStackTrace();
      return null;

    }

  }

  public static ArrayList<ArrayList<String>> getMatrix(
      String table,
      String orderColumn,
      boolean desc,
      String... columns) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM " + table + " ORDER BY " + orderColumn
              + (desc ? " DESC" : " ASC")
      );

      ArrayList<ArrayList<String>> results = new ArrayList<>();

      while (rs.next()) {

        ArrayList<String> entryArray = new ArrayList<>();

        for (String column : columns)
          entryArray.add(rs.getString(column));

        results.add(entryArray);

      }

      return results;

    } catch (SQLException e) {

      e.printStackTrace();
      return null;

    }

  }

  public static boolean insert(String table, String columns, String values) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.execute(
          "INSERT INTO " + table + " " + columns + " VALUES " + values
      );
      return true;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean set(
      String table,
      String column,
      String idColumnName,
      String ID,
      String newValue) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      return stmt.execute(
          "UPDATE " + table + " SET " + column + " = '" + newValue + "' WHERE "
              + idColumnName + " = " + ID
      );

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean setBit(
      String table,
      String column,
      String idColumnName,
      String ID,
      String newValue) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      return stmt.execute(
          "UPDATE " + table + " SET " + column + " = " + newValue + " WHERE "
              + idColumnName + " = " + ID
      );

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean setInt(
      String table,
      String column,
      String idColumnName,
      String ID,
      int newValue) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      return stmt.execute(
          "UPDATE " + table + " SET " + column + " = " + newValue + " WHERE "
              + idColumnName + " = " + ID
      );

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean setAll(String table, String column, String newValue) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.execute(
          "UPDATE " + table + " SET " + column + " = '" + newValue + "'"
      );
      return true;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean
      containsUniqueKey(String table, String idColumn, String key) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM " + table + " WHERE " + idColumn + " = '" + key + "'"
      );
      if (rs.next())
        return true;
      else
        return false;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean
      containsEntry(String table, List<String> columns, List<String> keys) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();

      String sqlQuery = "SELECT * FROM " + table + " WHERE ";
      for (int i = 0; i < columns.size(); i++) {

        sqlQuery += columns.get(i) + " = '" + keys.get(i) + "'";
        if (i < columns.size() - 1)
          sqlQuery += " AND ";

      }

      ResultSet rs = stmt.executeQuery(sqlQuery);
      if (rs.next())
        return true;
      else
        return false;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean
      isNull(String table, String column, String idColumn, String id) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT * FROM " + table + " WHERE " + idColumn + " = " + id
      );
      rs.next();
      if (rs.getString(column) == null)
        return true;
      else
        return false;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean deleteEntry(
      String table,
      String idColumn,
      String ID,
      String column,
      String key) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.execute(
          "DELETE FROM " + table + " WHERE " + idColumn + " = '" + ID + "' AND "
              + column + " = '" + key + "'"
      );
      return true;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean
      deleteEntries(String table, String idColumn, String key) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.execute(
          "DELETE FROM " + table + " WHERE " + idColumn + " = '" + key + "'"
      );
      return true;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

  public static boolean executeSQL(String sql) {

    try {

      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      stmt.execute(sql);
      return true;

    } catch (SQLException e) {

      e.printStackTrace();
      return false;

    }

  }

}
