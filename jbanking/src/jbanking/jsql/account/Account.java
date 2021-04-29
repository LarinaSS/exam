package jbanking.jsql.account;

import java.util.Map;

import jbanking.jsql.IDbObject;


public class Account implements IDbObject {

  public Account(IDbObject iDbObject) {
    mId = iDbObject.getId();
  }
  
  public Account(long id) {
    mId = id;
  }
  
  public Account(String name) {
    mName = name;
    mId = -1;
  }
  
  public void setName(final String name) {
    mName = name;
  }
  
  public void setMoney(final long money) {
    mMoney = money;
  }
  
  public String getName() {
    return mName;
  }
  
  public long getMoney() {
    return mMoney;
  }
  
  @Override
  public long getId() {
    // TODO Auto-generated method stub
    return mId;
  }

  public void setId(long argId) {
    mId = argId;
  }
  
  @Override
  public Map<String, Long> getI8Values() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, String> getStrValues() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Float> getR4Values() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }
  
  protected long mId = -1L;
  public static final String TABLE_NAME = "main.accounts";
  protected String mName = "";
  protected long mMoney = 0L;
  
  public static interface ColumnNames {
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String MONEY = "money";
  }
  
}
