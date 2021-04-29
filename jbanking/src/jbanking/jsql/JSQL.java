package jbanking.jsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jbanking.jsql.account.Account;
import jbanking.jsql.transacts.JsqlTransact;

import org.sqlite.SQLiteConnection;
import org.sqlite.SQLiteUpdateListener;


public class JSQL {

  Connection conn = null;

  public void open(final String dbPath) {
    try {
      // db parameters
      String url = "jdbc:sqlite:" + dbPath;
      // create a connection to the database
      conn = DriverManager.getConnection(url);
      
      System.out.println("Connection to SQLite has been established.");
        
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  
  public void close() {
    try {
      if (conn != null) {
          conn.close();
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }
  }
  
  private static void closeStatementSilently(Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  private void createTableAccounts () {
    String sql = "CREATE TABLE IF NOT EXISTS " + Account.TABLE_NAME +"  (\n "
        + Account.ColumnNames.ID +" integer PRIMARY KEY,\n "
        + Account.ColumnNames.NAME + " text NOT NULL,\n"
        + Account.ColumnNames.MONEY + " integer\n"
        + ");";

    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.execute(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      closeStatementSilently(stmt);
    }
    
  }
  
  private void createTableTransacts() {
    String sql = "CREATE TABLE IF NOT EXISTS " + JbTransact.TABLE_NAME +"  (\n "
        + JbTransact.Columns.ID +" integer PRIMARY KEY,\n "
        + JbTransact.Columns.ACCOUNT_ID_FROM + " integer,\n"
        + JbTransact.Columns.ACCOUNT_MONEY_FROM_BEFORE + " integer,\n"
        + JbTransact.Columns.ACCOUNT_MONEY_FROM_AFTER + " integer,\n"
        + JbTransact.Columns.ACCOUNT_ID_TO + " integer,\n"
        + JbTransact.Columns.ACCOUNT_MONEY_TO_BEFORE + " integer,\n"
        + JbTransact.Columns.ACCOUNT_MONEY_TO_AFTER + " integer,\n"
        + JbTransact.Columns.STATE + " text NOT NULL,\n"
        + JbTransact.Columns.MONEY + " integer\n"
        + ");";

    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.execute(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      closeStatementSilently(stmt);
    }
  }
  
  public void prepareTables() {
    createTableAccounts();
    createTableTransacts();
  }
  
  private static void closePreparedStatement(PreparedStatement pstmt) {
    if (pstmt != null) {
      try {
        pstmt.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public Account load(long accountId) {
    Account account = null;
    PreparedStatement pstmt = null;
    try {
      String sqlQuesry = "select " + Account.ColumnNames.NAME + ", " + Account.ColumnNames.MONEY +
          " from " + Account.TABLE_NAME + " where " + Account.ColumnNames.ID + " = ?";
      pstmt  = conn.prepareStatement(sqlQuesry);
      pstmt.setLong(1, accountId);
          
      ResultSet rs  = pstmt.executeQuery();
      while (rs.next()) {
        account = new Account(accountId);
        account.setName(rs.getString(Account.ColumnNames.NAME));
        account.setMoney(rs.getLong(Account.ColumnNames.MONEY));
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      closePreparedStatement(pstmt);
    }
    return account;
  }
  
  public boolean loadByName(Account account) {
    String name = account.getName();
    boolean found = false;
    PreparedStatement pstmt = null;
    if (!Utils.textIsEmpty(name)) {
      try {
        String sqlQuesry = "select " + Account.ColumnNames.ID + 
            " from " + Account.TABLE_NAME + " where " + Account.ColumnNames.NAME + " = ?";
        pstmt  = conn.prepareStatement(sqlQuesry);
        pstmt.setString(1, account.getName());
            
        ResultSet rs  = pstmt.executeQuery();
        while (rs.next()) {
          account.setId(rs.getLong(Account.ColumnNames.ID));
          found = true;
        }

      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        closePreparedStatement(pstmt);
        pstmt = null;
      }
    }
    return found;
  }
  
  private boolean updateAccount(Account account) {
    boolean isUpdated = false;
    String sqlQuesry = "update " + Account.TABLE_NAME + " set " +
        Account.ColumnNames.NAME + " = ? , " +
        Account.ColumnNames.MONEY + " = ? " +
        " where " + Account.ColumnNames.ID + " = ?";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sqlQuesry);
      pstmt.setString(1, account.getName());
      pstmt.setLong(2, account.getMoney() );
      pstmt.setLong(3, account.getId());
      if (pstmt.executeUpdate() > 0) {
        isUpdated = true;
      }
      //conn.commit();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      closePreparedStatement(pstmt);
    }
    return isUpdated;
  }
  
  public void addUpdateListener(SQLiteUpdateListener listener) {
    ((SQLiteConnection)conn).addUpdateListener(listener);
  }
  
  public void removeUpdateListener(SQLiteUpdateListener listener) {
    ((SQLiteConnection)conn).removeUpdateListener(listener);
  }
  
  public boolean save(Account account) {
    boolean isUpdated = false;
    boolean success = false;
    if (account.getId() > 0) {
      isUpdated = updateAccount(account);
    }
    PreparedStatement pstmt = null;
    if (false == isUpdated) {
      try {
        String sql = "INSERT INTO " + Account.TABLE_NAME + "(" +
            Account.ColumnNames.NAME + "," +Account.ColumnNames.MONEY + ") VALUES(?,?)";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, account.getName());
        pstmt.setLong(2, account.getMoney());
        if (pstmt.executeUpdate() > 0) {
          isUpdated = true;
        }
        conn.commit();
        success = true;
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      } finally {
        closePreparedStatement(pstmt);
        pstmt = null;
      }
    }
    
    if (success) {
      try {
        String sqlQuesry = "select " + Account.ColumnNames.ID + 
            " from " + Account.TABLE_NAME + " where " + Account.ColumnNames.NAME + " = ?";
        pstmt  = conn.prepareStatement(sqlQuesry);
        pstmt.setString(1, account.getName());
            
        ResultSet rs  = pstmt.executeQuery();
        while (rs.next()) {
          account.setId(rs.getLong(Account.ColumnNames.ID));
        }

      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        closePreparedStatement(pstmt);
        pstmt = null;
      }
      
    }
    return isUpdated;
  }
  
  /**
   * pair of accountId and transactId are always unique
   * and transactId is unique for every thread
   * @param accountId
   * @param transactId
   */
  synchronized public void lockAccount(long transactId, long accountId) {
    synchronized (mLockedAccounts) {
      boolean isLocked = mLockedAccounts.contains(accountId);
      if(isLocked) {
        synchronized (mWaitingAccounts) {
          mWaitingAccounts.put(transactId, accountId);
        }
        do {
          try {
            mLockedAccounts.wait();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          for (int idx = mPassedTransacts.size() - 1; idx >= 0; idx--) {
            if (mPassedTransacts.get(idx).longValue() == transactId) {
              isLocked = false;
              mPassedTransacts.remove(idx);
            }
          }
        } while (isLocked);
      }
      mLockedAccounts.add(accountId);
      synchronized (mWaitingAccounts) {
        mWaitingAccounts.remove(transactId);
      }
    }
  }
  
  public void unlockAccount(long accountId) {
    synchronized (mLockedAccounts) {
      synchronized (mWaitingAccounts) {
        
        for (long transactId : mWaitingAccounts.keySet()){
          Long waitingAccountId = mWaitingAccounts.get(transactId);
          if (waitingAccountId != null) {
            if (waitingAccountId.longValue() == accountId){
              mPassedTransacts.add(transactId);
              break;
            }
          }
        }
      }
      mLockedAccounts.remove(accountId);
      mLockedAccounts.notifyAll();
    }
    
  }
  
  public long getUniqueTransactId() {
    synchronized (this) {
      return mUniqueTransactId++;
    }
  }
  
  private long mUniqueTransactId = 1L;
  
  private HashSet<Long> mLockedAccounts = new HashSet<Long>();
  
  private HashMap<Long, Long> mWaitingAccounts = new HashMap<Long, Long>();
  
  private ArrayList<Long> mPassedTransacts = new ArrayList<Long>();
  
  private long mPassedTransactId = -1;

  public boolean isExists(JsqlTransact transact) {
    boolean isLoaded = false;
    if (transact.mId >= -1){
      PreparedStatement pstmt = null;
      String sqlString = "select " +
          JbTransact.Columns.ACCOUNT_ID_FROM + ", " +
          JbTransact.Columns.ACCOUNT_ID_TO + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_BEFORE + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_AFTER + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_TO_BEFORE + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_TO_AFTER + ", " +
          JbTransact.Columns.MONEY + ", " +
          JbTransact.Columns.STATE + " from " + JbTransact.TABLE_NAME +
          " where " + JbTransact.Columns.ID + " = ? ";
          
      try {
        pstmt  = conn.prepareStatement(sqlString);
        pstmt.setLong(1, transact.mId);
          
        ResultSet rs  = pstmt.executeQuery();
        while (rs.next()) {
          isLoaded = true;
        }
        rs.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        closePreparedStatement(pstmt);
        pstmt = null;
      }
    }
    return isLoaded;
    
  }
  
  public boolean load(JsqlTransact transact) {
    boolean isLoaded = false;
    if (transact.mId >= -1){
      PreparedStatement pstmt = null;
      String sqlString = "select " +
          JbTransact.Columns.ACCOUNT_ID_FROM + ", " +
          JbTransact.Columns.ACCOUNT_ID_TO + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_BEFORE + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_AFTER + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_TO_BEFORE + ", " +
          JbTransact.Columns.ACCOUNT_MONEY_TO_AFTER + ", " +
          JbTransact.Columns.MONEY + ", " +
          JbTransact.Columns.STATE + " from " + JbTransact.TABLE_NAME +
          " where " + JbTransact.Columns.ID + " = ? ";
          
      try {
        pstmt  = conn.prepareStatement(sqlString);
        pstmt.setLong(1, transact.mId);
          
        ResultSet rs  = pstmt.executeQuery();
        while (rs.next()) {
          transact.mAccIdfrom = rs.getLong(JbTransact.Columns.ACCOUNT_ID_FROM);
          transact.mAccIdTo = rs.getLong(JbTransact.Columns.ACCOUNT_ID_TO);
          transact.mMoneyFromBefore = rs.getLong(JbTransact.Columns.ACCOUNT_MONEY_FROM_BEFORE);
          transact.mMoneyFromAfter = rs.getLong(JbTransact.Columns.ACCOUNT_MONEY_FROM_AFTER);
          transact.mMoneyToBefore = rs.getLong(JbTransact.Columns.ACCOUNT_MONEY_TO_BEFORE);
          transact.mMoneyToAfter = rs.getLong(JbTransact.Columns.ACCOUNT_MONEY_TO_AFTER);
          transact.mMoney = rs.getLong(JbTransact.Columns.MONEY);
          transact.mState = rs.getString(JbTransact.Columns.STATE);
          isLoaded = true;
        }

      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        closePreparedStatement(pstmt);
        pstmt = null;
      }
    }
    return isLoaded;
  }
  
  public void save(JsqlTransact transact) {
    String sqlQuesry = null;
    if (isExists(transact)) {
      sqlQuesry = "update " + JbTransact.TABLE_NAME + " set " +
          JbTransact.Columns.ACCOUNT_ID_FROM + " = ? , " +
          JbTransact.Columns.ACCOUNT_ID_TO + " = ? , " +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_BEFORE + " = ? , " +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_AFTER + " = ? , " +
          JbTransact.Columns.ACCOUNT_MONEY_TO_BEFORE + " = ? , " +
          JbTransact.Columns.ACCOUNT_MONEY_TO_AFTER + " = ? , " +
          JbTransact.Columns.MONEY + " = ? , " +
          JbTransact.Columns.STATE + " = ? " +
           " where " + JbTransact.Columns.ID + " = ?";
    } else {
      sqlQuesry = "INSERT INTO " + JbTransact.TABLE_NAME + "(" +
          JbTransact.Columns.ACCOUNT_ID_FROM + "," +
          JbTransact.Columns.ACCOUNT_ID_TO + "," +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_BEFORE + "," +
          JbTransact.Columns.ACCOUNT_MONEY_FROM_AFTER + "," +
          JbTransact.Columns.ACCOUNT_MONEY_TO_BEFORE + "," +
          JbTransact.Columns.ACCOUNT_MONEY_TO_AFTER + "," +
          JbTransact.Columns.MONEY + "," +
          JbTransact.Columns.STATE + "," +
          JbTransact.Columns.ID + 
          ") VALUES(?,?,?,?,?,?,?,?,?)";
    }
      PreparedStatement pstmt = null;
      try {
        pstmt = conn.prepareStatement(sqlQuesry);
        pstmt.setLong(1, transact.mAccIdfrom);
        pstmt.setLong(2, transact.mAccIdTo);
        pstmt.setLong(3, transact.mMoneyFromBefore);
        pstmt.setLong(4, transact.mMoneyFromAfter);
        pstmt.setLong(5, transact.mMoneyToBefore);
        pstmt.setLong(6, transact.mMoneyToAfter);
        pstmt.setLong(7, transact.mMoney);
        pstmt.setString(8, transact.mState);
        pstmt.setLong(9, transact.mId);

        pstmt.executeUpdate();
        //conn.commit();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        closePreparedStatement(pstmt);
      }
  }
}
