package jbanking.jsql.transacts;

import jbanking.jsql.JSQL;

public class JsqlTransact {
  
  public JsqlTransact() {};
  public JsqlTransact(JSQL jsql, long accIdFrom, long accIfTo, long money)
  {
    mId = jsql.getUniqueTransactId();
    mAccIdfrom = accIdFrom;
    mAccIdTo = accIfTo;
    mMoney = money;
  }
  
  public long mId = -1;
  public long mAccIdfrom = -1;
  public long mMoneyFromBefore = 0;
  public long mMoneyFromAfter = 0;
  public long mAccIdTo = -1;
  public long mMoneyToBefore = 0;
  public long mMoneyToAfter = 0;
  public long mMoney = 0;
  public String mState = "NONE";

}
