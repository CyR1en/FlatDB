package com.cyr1en.flatdb;

import com.cyr1en.flatdb.util.FastStrings;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseOptionsTest {

  private DatabaseBuilder dbBuilder;

  @Before
  public void before() {
    dbBuilder = new DatabaseBuilder();
  }

  @Test
  public void setPathTest() {
    String path = "C:/testPath";
    dbBuilder.setPath(path);
    Assertions.assertThat(dbBuilder.getPath()).isEqualTo(path);

    dbBuilder.setPath("");
    Assertions.assertThat(dbBuilder.getPath()).isEqualTo("");
  }

  @Test
  public void tryDriverNameTest() {
    try {
      Method method = DatabaseBuilder.class.getDeclaredMethod("tryDriverName", String.class);
      method.setAccessible(true);

      AtomicReference<String> dName = new AtomicReference<>();
      Assertions.assertThatCode(() -> {
        method.invoke(dbBuilder, "com.test.Missing");
        dName.set(dbBuilder.getDriverName());
      }).hasStackTraceContaining("RuntimeException");
      Assertions.assertThat(dName.get()).isEqualTo(null);

      Assertions.assertThatCode(() -> {
        method.invoke(dbBuilder, "org.h2.Driver");
        dName.set(dbBuilder.getDriverName());
      }).doesNotThrowAnyException();
      Assertions.assertThat(dName.get()).isEqualTo("h2");

      Assertions.assertThatCode(() -> {
        method.invoke(dbBuilder, "com.test.Driver");
        dName.set(dbBuilder.getDriverName());
      }).doesNotThrowAnyException();
      Assertions.assertThat(dName.get()).isEqualTo("test");
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void isBlankTest() {
    Assertions.assertThat(FastStrings.isBlank("test")).isEqualTo(false);
    Assertions.assertThat(FastStrings.isBlank("  _")).isEqualTo(false);

    Assertions.assertThat(FastStrings.isBlank("")).isEqualTo(true);
    Assertions.assertThat(FastStrings.isBlank("     ")).isEqualTo(true);

  }

  @Test
  public void buildTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String path;
    String OS = (System.getProperty("os.name")).toUpperCase();
    if (OS.contains("WIN")) {
      path = System.getenv("AppData");
      path += "/Local/Temp/FlatDBTest/testDB";
    } else {
      path = System.getProperty("user.home");
      path += "/Library/Caches/TemporaryItems/FlatDBTest/testDB";
    }
    dbBuilder.setPath(path);

    Method method = DatabaseBuilder.class.getDeclaredMethod("tryDriverName", String.class);
    method.setAccessible(true);
    method.invoke(dbBuilder, "org.h2.Driver");
    Assertions.assertThat(dbBuilder.getDriverName()).isEqualTo("h2");
    final String finalPath = path;
    Assertions.assertThatCode(() -> {
      Database db = dbBuilder.build();
      Assertions.assertThat(dbBuilder.getConnectionURL()).isEqualTo("jdbc:h2:" + finalPath);
      Assertions.assertThat(db).isNotEqualTo(null);
    }).doesNotThrowAnyException();
    File file = new File(path);
    if(file.exists())
      file.delete();
  }
}
