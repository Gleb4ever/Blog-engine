package main.formatter;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

public class SimpleFormatter {

  private static final Formatter HIBERNATE_SQL_FORMATTER = new BasicFormatterImpl();

  public String formatMessage(int connectionId, String now, long elapsed, String category,
      String prepared, String sql) {
    if (sql.isEmpty()) {
      return "";
    }
    String batch = "batch".equals(category) ? " add to batch " : "";
    return String
        .format("Hibernate: %s %s {elapsed: %dms}", batch, HIBERNATE_SQL_FORMATTER.format(sql),
            elapsed);
  }
}