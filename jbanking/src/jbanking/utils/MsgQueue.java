package jbanking.utils;

import java.util.ArrayList;

public class MsgQueue {

  synchronized public int send(Object item) {
    mTransacts.add(item);
    if (mIsWaiting) {
      this.notify();
    }
    return mTransacts.size();
  }
  
  private boolean mIsWaiting = false;
  
  synchronized public Object recv() {
    if (mTransacts.size() == 0) {
      try {
        mIsWaiting = true;
        this.wait();
        mIsWaiting = false;
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (mTransacts.size() > 0) {
      Object item = mTransacts.get(0);
      mTransacts.remove(0);
      return item;
    }
    return null;
  }
  
  protected ArrayList<Object> mTransacts = new ArrayList<Object>();
  
  synchronized public int size() {
    return mTransacts.size();
  }
}
