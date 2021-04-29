package jbanking.jsql;

import jbanking.jsql.transacts.JsqlTransact;
import jbanking.utils.HandlerThreadPool;

public class JbHtPool extends HandlerThreadPool {

  public JbHtPool(int nThreads) {
    super(nThreads);
    // TODO Auto-generated constructor stub
  }

  public JSQL mJsql = new JSQL();
  
  public void open(String dbPath) {
    mJsql.open(dbPath);
  }

  public void close() {
    mJsql.close();
  }
  
  @Override
  protected void handleMessage(Object item) {
    if (item != null && item instanceof JsqlTransact) {
      JsqlTransact transact = (JsqlTransact)item;
      JbTransact.handleTransact(mJsql, transact);
      mJsql.save(transact);
    }

  }

}
