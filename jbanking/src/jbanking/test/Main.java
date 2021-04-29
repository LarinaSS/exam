package jbanking.test;

import jbanking.jsql.JSQL;
import jbanking.jsql.JbHtPool;
import jbanking.jsql.JbTransact;
import jbanking.jsql.Utils;
import jbanking.jsql.account.Account;
import jbanking.jsql.transacts.JsqlTransact;

public class Main {

  
  static class TestRunnable implements Runnable {

    public Account[] mAccounts;
    
    public int mOwnerAccountIndex = -1;
    
    public int mIttrs = 32;
    
    public TestRunnable(int ownerAccIdx, Account[] accounts, JbHtPool jsql) {
      mAccounts = accounts;
      mOwnerAccountIndex = ownerAccIdx;
      mJbHtPool = jsql;
    }
    
    JbHtPool mJbHtPool;
    
    @Override
    public void run() {
      do {
      int otherIdx = Utils.randInt(0, mAccounts.length - 2);
      if (otherIdx >= mOwnerAccountIndex) {
        otherIdx ++;
      }
      long money = Utils.randInt(0, 1000);
      System.out.format("from:%2d to:%2d money:%4d\n", mOwnerAccountIndex, otherIdx, money);
      
      JsqlTransact jsqlTransact = new JsqlTransact(
          mJbHtPool.mJsql,
          mAccounts[mOwnerAccountIndex].getId(),
          mAccounts[otherIdx].getId(), money);
      mJbHtPool.send(jsqlTransact);

      } while (mIttrs-- > 0);
    }
    
  }
  
  public static void main(String[] args) {
    System.out.print("Hello world\n");
    
    //JSQL jsql = new JSQL();
    JbHtPool jbHtPool = new JbHtPool(4);
    jbHtPool.open("/home/alex/logs/test.db");
    //jbHtPool.open(":memory:");
    
    jbHtPool.mJsql.prepareTables();
    
    jbHtPool.start();
    
    int numOfAccounts = 4;
    
    Account[] accounts = new Account[numOfAccounts];
    
    for (int idx = 0; idx < numOfAccounts; idx ++) {
      accounts[idx] = new Account(String.format("name_%05d", idx));
    }
    for (Account account : accounts) {
      account.setMoney(3000);
      if(jbHtPool.mJsql.loadByName(account) == false) {
        account.setMoney(3000);
        jbHtPool.mJsql.save(account);
        jbHtPool.mJsql.loadByName(account);
      }
    }
    
    //JbTransact.moveMoney(jsql, accounts[0].getId(), accounts[1].getId(), 1000);
    
    Thread[] threads = new Thread[numOfAccounts];
    
    for (int idx = 0; idx < numOfAccounts; idx ++) {
      threads[idx] = new Thread(new TestRunnable(idx, accounts, jbHtPool));
      threads[idx].start();
    }
    
    for (int idx = 0; idx < numOfAccounts; idx ++) {
      try {
        threads[idx].join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
        
    jbHtPool.stop();
    
    long sum = 0;

    for (int idx = 0; idx < numOfAccounts; idx++) {
      accounts[idx] = jbHtPool.mJsql.load(accounts[idx].getId());
    }
    
    for (Account acc : accounts) {
      sum += acc.getMoney();
      System.out.format("money:%d\n", acc.getMoney());
    }
    
    System.out.format("SUM:%d\n", sum);
    
    jbHtPool.close();
  }
}
