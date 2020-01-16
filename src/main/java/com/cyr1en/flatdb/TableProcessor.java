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

import com.cyr1en.flatdb.Database;
import com.cyr1en.flatdb.FlatTable;
import com.cyr1en.flatdb.annotations.Column;
import com.cyr1en.flatdb.annotations.Table;
import com.cyr1en.flatdb.exceptions.JavaTypeConversionException;
import com.cyr1en.flatdb.exceptions.MissingAnnotationException;
import com.cyr1en.flatdb.types.SQLTypePair;
import com.cyr1en.flatdb.types.TypeMap;
import com.cyr1en.flatdb.util.FastStrings;
import com.google.common.collect.ImmutableList;
import lombok.extern.java.Log;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class TableProcessor {

  private Database database;
  private String db_prefix;

  public TableProcessor(Database database) {
    this.database = database;
    this.db_prefix = database.getDb_prefix();
  }

  public FlatTable process(Class classToProcess) {
    assertAnnotated(classToProcess);
    ImmutableList<Field> annotatedFields = checkAnnotatedFields(classToProcess);

    String tableName = getTableName(classToProcess);
    System.out.println(tableName);
    processTable(tableName);
    processColumns(tableName, annotatedFields);

    return new FlatTable(tableName, database);
  }

  private String getTableName(Class classToProcess) {
    Table table = (Table) classToProcess.getAnnotation(Table.class);
    String tableName = FastStrings.isBlank(table.nameOverride()) ?
            classToProcess.getSimpleName().toLowerCase() : table.nameOverride();
    return db_prefix + tableName;
  }

  private String getColName(Field field) {
    Column columnMeta = field.getAnnotation(Column.class);
    return FastStrings.isBlank(columnMeta.nameOverride()) ?
            field.getName().toLowerCase() : columnMeta.nameOverride();
  }

  private void processTable(String tableName) {
    if (database.tableExists(tableName)) return;
    database.executeUpdate("CREATE TABLE %s", tableName);
  }

  private void processColumns(String tableName, ImmutableList<Field> annotatedFields) {
    //Use this to check if there are more than one primary keys.
    AtomicReference<Boolean> initializedPrimary = new AtomicReference<>(false);
    Optional<ResultSet> oRS = database.executeQuery("SELECT * FROM %s", tableName);

    for (Field f : annotatedFields) {
      Column columnMeta = f.getAnnotation(Column.class);
      oRS.ifPresent(rs -> {
        if (columnMeta.primaryKey()) {
          if (!initializedPrimary.get()) {
            processColumn(rs, tableName, f, initializedPrimary.get());
            initializedPrimary.set(true);
          }
        } else {
          processColumn(rs, tableName, f, initializedPrimary.get());
        }
      });
    }
  }

  private void processColumn(ResultSet rs, String tableName, Field field, boolean isSecondaryKey) {
    Column columnMeta = field.getAnnotation(Column.class);
    String colName = getColName(field);

    if (colExists(rs, colName)) return;

    if (columnMeta.primaryKey() && isSecondaryKey) {
      log.warning("Cannot make the column " + field.getName() + " as primary key. Cannot define a secondary primary key!");
    }

    SQLTypePair sqlTypePair = TypeMap.getSQLType(field.getType());
    if (sqlTypePair == null)
      throw new JavaTypeConversionException(field.getType());

    String defaultValue = FastStrings.isBlank(columnMeta.defaultValue()) ?
            sqlTypePair.getDefaultValue() : columnMeta.defaultValue();
    defaultValue = encloseIfNeeded(defaultValue);

    StringBuilder sb = new StringBuilder("ALTER TABLE %s ADD %s %s NOT NULL "); //Initial = ALTER TABLE table_name ADD colName DATA_TYPE
    if (columnMeta.autoIncrement()) {
      sb.append("AUTO_INCREMENT ");
      String filteredPK = columnMeta.primaryKey() ? isSecondaryKey ? "" : "PRIMARY KEY" : "";
      sb.append(filteredPK);
      database.executeUpdate(sb.toString(), tableName, colName, sqlTypePair.getTypeName());
    } else {
      sb.append("DEFAULT %s");
      database.executeUpdate(sb.toString(), tableName, colName, sqlTypePair.getTypeName(), defaultValue);
    }
  }

  private boolean colExists(ResultSet resultSet, String columnName) {
    try {
      ResultSetMetaData meta = resultSet.getMetaData();
      int colCount = meta.getColumnCount();
      for (int i = 1; i <= colCount; i++)
        if (meta.getColumnName(i).equalsIgnoreCase(columnName))
          return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private void assertAnnotated(Class from) {
    if (!from.isAnnotationPresent(Table.class))
      throw new MissingAnnotationException(Table.class, from);
  }

  private ImmutableList<Field> checkAnnotatedFields(Class from) {
    Stream<Field> fieldStream = Arrays.stream(from.getDeclaredFields());
    List<Field> fields = filterAnnotatedFields(fieldStream);
    if (fields.isEmpty())
      log.warning("No columns were found in " + from.getSimpleName() + ". The table will still be created as an" +
              "empty table. Execute alter to append columns in the future.");
    return ImmutableList.copyOf(fields);
  }

  private List<Field> filterAnnotatedFields(Stream<Field> fieldStream) {
    return fieldStream.filter(field ->
            field.isAnnotationPresent(Column.class)).collect(Collectors.toList());
  }

  private String encloseIfNeeded(String s) {
    if(!FastStrings.isNumeric(s))
      return "'" + s + "'";
    return s;
  }
}
