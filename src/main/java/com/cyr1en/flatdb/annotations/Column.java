package com.cyr1en.flatdb.annotations;

import com.cyr1en.flatdb.TableProcessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

  String nameOverride() default "";


  String defaultValue() default "";

  /**
   * Is this column the primary key of a {@link Table}?
   *
   * <p>You cannot have multiple primary keys. The {@link TableProcessor} will
   * try to give you a warning and prevent setting this as a primary key before sql execution.
   * However, the TableProcessor will not see if a column is a primary key if it already exists. So make sure
   * that your table doesn't have overlapping primary keys to prevent {@link java.sql.SQLException}.</p>
   *
   * @return if this is {@link Column} is a primary key.
   */
  boolean primaryKey() default false;

  /**
   * Will this column be an auto incrementing column?
   *
   * @return if this {@link Column} is an auto incrementing column.
   */
  boolean autoIncrement() default false;
}
