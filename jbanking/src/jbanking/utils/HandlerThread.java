package jbanking.utils;

public abstract class HandlerThread {

  protected abstract void handleMessage(Object item);
  
  protected Thread mThread;
  
  public void start()
  {
    if (mThread == null) {
      mThread = new Thread(mRunnable);
      mThread.start();
    }
  }

  public void join() {
    if (mThread != null) {
      try {
        mThread.join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  private Runnable mRunnable = new Runnable() {
    
    @Override
    public void run() {
      Object item = null;
      do {
        item = mMsgQueue.recv();
        handleMessage(item);
      } while (item != null);
      
    }
  };
  
  private MsgQueue mMsgQueue = new MsgQueue();
  
  public int send(Object item) {
    return mMsgQueue.send(item);
  }
  
  public void stop() {
    send(null);
    join();
    mThread = null;
  }
  
  public int size() {
    return mMsgQueue.size();
  }
}
