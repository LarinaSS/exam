package jbanking.jsql;

import java.util.Map;

public interface IDbObject {
  long getId();
  Map<String, Long> getI8Values();
  Map<String, String> getStrValues();
  Map<String, Float> getR4Values();
  String getTableName();
}
