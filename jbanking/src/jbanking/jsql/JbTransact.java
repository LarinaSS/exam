package jbanking.jsql;

import jbanking.jsql.account.Account;
import jbanking.jsql.transacts.JsqlTransact;

public class JbTransact {
  
  public static final String TABLE_NAME = "main.transacts";
  
  public static interface Columns {
    static final String ID = "id";
    static final String ACCOUNT_ID_FROM = "accIdFrom";
    static final String ACCOUNT_MONEY_FROM_BEFORE = "accMoneyFromBefore";
    static final String ACCOUNT_MONEY_FROM_AFTER = "accMoneyFromAfter";
    static final String ACCOUNT_ID_TO = "accIdTo";
    static final String ACCOUNT_MONEY_TO_BEFORE = "accMoneyToBefore";
    static final String ACCOUNT_MONEY_TO_AFTER = "accMoneyToAfter";
    static final String MONEY = "money";
    static final String STATE = "state";
  }
  
  public static interface States {
    static final String NONE = "NONE";
    static final String DONE = "DONE";
    static final String REJECTED = "REJECTED";
    
  }
  
  public static boolean moveMoney(
      JSQL jsql,
      long accountIdFrom,
      long accountIdTo,
      long money)
  {
    long transactId = jsql.getUniqueTransactId();
    JsqlTransact transact = new JsqlTransact();
    transact.mId = transactId;
    transact.mAccIdfrom = accountIdFrom;
    transact.mAccIdTo = accountIdTo;
    transact.mMoney = money;
    jsql.lockAccount(transactId, accountIdFrom);
    Account accountFrom = jsql.load(accountIdFrom);
    long available = accountFrom.getMoney();
    transact.mMoneyFromBefore = available;
    if (available < money) {
      jsql.unlockAccount(accountIdFrom);
      transact.mMoneyFromAfter = available;
      transact.mState = States.REJECTED;
      return false;
    }
    accountFrom.setMoney(available - money);
    transact.mMoneyFromAfter = available - money;
    transact.mState = States.DONE;
    jsql.save(accountFrom);
    jsql.unlockAccount(accountIdFrom);
    jsql.lockAccount(transactId, accountIdTo);
    Account accountTo = jsql.load(accountIdTo);
    transact.mMoneyToBefore = accountTo.getMoney();
    transact.mMoneyToAfter = transact.mMoneyToBefore + money;
    accountTo.setMoney(accountTo.getMoney() + money);
    jsql.save(accountTo);
    jsql.unlockAccount(accountIdTo);
    return true;
  }
  
  public static boolean handleTransact(JSQL jsql, JsqlTransact transact) {
    if (transact.mId < 0) {
      transact.mId = jsql.getUniqueTransactId();
    }
    jsql.lockAccount(transact.mId, transact.mAccIdfrom);
    Account accountFrom = jsql.load(transact.mAccIdfrom);
    long available = accountFrom.getMoney();
    transact.mMoneyFromBefore = available;
    if (available < transact.mMoney) {
      jsql.unlockAccount(transact.mAccIdfrom);
      transact.mMoneyFromAfter = available;
      transact.mState = States.REJECTED;
      return false;
    }
    transact.mMoneyFromAfter = available - transact.mMoney;
    accountFrom.setMoney(transact.mMoneyFromAfter);
    transact.mState = States.DONE;
    jsql.save(accountFrom);
    jsql.unlockAccount(transact.mAccIdfrom);
    jsql.lockAccount(transact.mId, transact.mAccIdTo);
    Account accountTo = jsql.load(transact.mAccIdTo);
    transact.mMoneyToBefore = accountTo.getMoney();
    transact.mMoneyToAfter = transact.mMoneyToBefore + transact.mMoney;
    accountTo.setMoney(transact.mMoneyToAfter);
    jsql.save(accountTo);
    jsql.unlockAccount(transact.mAccIdTo);
    return true;
    
  }
}
