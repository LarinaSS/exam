package jbanking.utils;

public abstract class HandlerThreadPool {

  protected int mThreads = 4;
  
  protected HandlerThread[] mHTs;
  
  protected abstract void handleMessage(Object item);
  
  public HandlerThreadPool(int nThreads) {
    if (nThreads < 1) {
      mThreads = 1;
    } else {
      mThreads = nThreads;
    }
    mHTs = new HandlerThread[mThreads];
    
    for (int idx = 0; idx < mThreads; idx++) {
      mHTs[idx] = new HandlerThread() {
        
        @Override
        protected void handleMessage(Object item) {
          HandlerThreadPool.this.handleMessage(item);
        }
      };
    }
  }
  
  public void start() {
    for (int idx = 0; idx < mThreads; idx++) {
      mHTs[idx].start();
    }
  }
  
  public int send(Object item) {
    int minIdx = 0;
    int minSz = mHTs[minIdx].size();
    int sumSize = minSz;
    for (int idx = 1; idx < mThreads; idx++){
      int itemHTSize = mHTs[idx].size();
      if (itemHTSize < minIdx) {
        minIdx = itemHTSize;
        minIdx = idx;
      }
      sumSize += itemHTSize;
    }
    mHTs[minIdx].send(item);
    sumSize ++;
    return sumSize;
  }
  
  public void join() {
    for (HandlerThread ht : mHTs){
      ht.join();
    }
  }
  
  public void stop() {
    for (HandlerThread ht : mHTs){
      ht.stop();
    }
  }
}
