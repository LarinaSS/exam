package jbanking.jsql;

import java.util.Map;
import java.util.Random;

public class Utils {

  static boolean isI8Value(IDbObject iDbObject, String columnName) {
    Map<String, Long> values = iDbObject.getI8Values();
    if (values != null) {
      return values.containsKey(columnName);
    }
    return false;
  }

  static long getI8Value(IDbObject iDbObject, String columnName) {
    Map<String, Long> values = iDbObject.getI8Values();
    if (values != null) {
      Long valueObj = values.get(columnName);
      if (valueObj != null) {
        return valueObj.longValue();
      }
    }
    return -1L;
  }
  
  static boolean isR4Value(IDbObject iDbObject, String columnName) {
    Map<String, Float> values = iDbObject.getR4Values();
    if (values != null) {
      return values.containsKey(columnName);
    }
    return false;
  }
  
  static float getR4Value(IDbObject iDbObject, String columnName) {
    Map<String, Float> values = iDbObject.getR4Values();
    if (values != null) {
      Float valueObj = values.get(columnName);
      if (valueObj != null) {
        return valueObj.floatValue();
      }
    }
    return 0.f;
  }
  
  static boolean textIsEmpty(String text) {
    if (text == null) {
      return true;
    }
    if (text.length() == 0) {
      return true;
    }
    return false;
  }
  
  private static Random rand = new Random();
  
  public static int randInt(int min, int max) {

    // NOTE: This will (intentionally) not run as written so that folks
    // copy-pasting have to think about how to initialize their
    // Random instance.  Initialization of the Random instance is outside
    // the main scope of the question, but some decent options are to have
    // a field that is initialized once and then re-used as needed or to
    // use ThreadLocalRandom (if using at least Java 1.7).
    // 
    // In particular, do NOT do 'Random rand = new Random()' here or you
    // will get not very good / not very random results.

    int randomNum = 0;
    synchronized (rand) {
      randomNum = rand.nextInt((max - min) + 1) + min;
    }
    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive

    return randomNum;
}
}
