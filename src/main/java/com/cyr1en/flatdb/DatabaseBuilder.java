/*
 * MIT License
 *
 * Copyright (c) 2019 Ethan Bacurio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cyr1en.flatdb;

import com.cyr1en.flatdb.annotations.Table;
import com.cyr1en.flatdb.types.SQLTypePair;
import com.cyr1en.flatdb.types.TypeMap;
import com.cyr1en.flatdb.util.Strings;
import lombok.Getter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseBuilder {

  @Getter private String path;
  @Getter private String driverName;
  @Getter private String connectionURL;
  @Getter private String databasePrefix;
  @Getter private List<Class> tables;

  public DatabaseBuilder() {
    path = "";
    driverName = "";
    connectionURL = "jdbc:%s:%s";
    databasePrefix = "flatdb_";
    tables = new ArrayList<>();
    tryDefaultDrivers();
  }

  private void tryDefaultDrivers() {
    if(Strings.isBlank(driverName)) tryDriverName("org.h2.Driver");
    if(Strings.isBlank(driverName)) tryDriverName("org.sqlite.JDBC");
  }

  public DatabaseBuilder appendTable(Class... schemas) {
    Stream<Class> toStream = Arrays.stream(schemas);
    this.tables.addAll(toStream.filter(sc ->
            sc.isAnnotationPresent(Table.class)).collect(Collectors.toList()));
    return this;
  }

  public DatabaseBuilder setDatabasePrefix(String prefix) {
    this.databasePrefix = prefix.endsWith("_") ? prefix : prefix + "_";
    return this;
  }

  public DatabaseBuilder setPath(String path) {
    this.path = path;
    return this;
  }

  public DatabaseBuilder addCustomTypes(Map<Class<?>, SQLTypePair> customTypes) {
    TypeMap.addCustomType(customTypes);
    return this;
  }

  public DatabaseBuilder addCustomType(Class<?> javaClass, SQLTypePair sqlTypePair) {
    TypeMap.addCustomType(javaClass, sqlTypePair);
    return this;
  }

  private void tryDriverName(String driverName) {
    try {
      Class.forName(driverName).newInstance();
      this.driverName = driverName.split("\\.")[1];
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  public Database build() throws SQLException {
    if(Strings.isBlank(driverName)) throw new SQLException("The driver name was left empty!");
    connectionURL = String.format(connectionURL, driverName, path);
    return new FlatDatabase(this);
  }
}
