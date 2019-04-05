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

package com.cyr1en.flatdb.types;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TypeMap {

  private static final Map<Class<?>, SQLTypePair> TYPE_MAP;
  private static final Map<Class<?>, SQLTypePair> CUSTOM_TYPE_MAP;
  public static final Map<Integer, String> TYPE_TO_NAME;

  static {
    TYPE_TO_NAME = extractTypeNames();
    TYPE_MAP = new ImmutableMap.Builder<Class<?>, SQLTypePair>()
            .put(Boolean.class, SQLTypePair.of(Types.BIT, "0"))
            .put(String.class, SQLTypePair.of(Types.VARCHAR, "null"))
            .put(BigDecimal.class, SQLTypePair.of(Types.NUMERIC, "0"))
            .put(Integer.class, SQLTypePair.of(Types.INTEGER, "0"))
            .put(Long.class, SQLTypePair.of(Types.BIGINT, "0"))
            .put(Float.class, SQLTypePair.of(Types.REAL, "0.0"))
            .put(Double.class, SQLTypePair.of(Types.FLOAT, "0.0"))
            .build();
    CUSTOM_TYPE_MAP = new HashMap<>();
  }

  public static SQLTypePair getSQLType(Class<?> jClass) {
    Class<?> checkedClass = jClass.isPrimitive() ? Primitives.wrap(jClass) : jClass;
    if(CUSTOM_TYPE_MAP.containsKey(checkedClass))
      return CUSTOM_TYPE_MAP.get(checkedClass);
    return TYPE_MAP.get(checkedClass);
  }

  public static void addCustomType(Map<Class<?>, SQLTypePair> customTypes, boolean override) {
    for(Map.Entry<Class<?>, SQLTypePair> entry : customTypes.entrySet())
      addCustomType(entry.getKey(), entry.getValue(), override);
  }

  public static void addCustomType(Class<?> javaClass, SQLTypePair sqlTypePair, boolean override) {
    boolean exists = CUSTOM_TYPE_MAP.containsKey(javaClass);
    if(!exists)
      CUSTOM_TYPE_MAP.put(javaClass, sqlTypePair);
    else if(override) {
      CUSTOM_TYPE_MAP.replace(javaClass, sqlTypePair);
    }
  }

  public static void addCustomType(Map<Class<?>, SQLTypePair> customTypes) {
    addCustomType(customTypes, false);
  }

  public static void addCustomType(Class<?> javaClass, SQLTypePair sqlTypePair) {
    addCustomType(javaClass, sqlTypePair, false);
  }

  public static void removeCustomType(Map<Class<?>, SQLTypePair> typeMap) {
    for(Map.Entry<Class<?>, SQLTypePair> entry : typeMap.entrySet())
      CUSTOM_TYPE_MAP.remove(entry.getKey());
  }

  public static void removeCustomType(Class<?> javaClass) {
    CUSTOM_TYPE_MAP.remove(javaClass);
  }

  public static String getName(int type) {
    return TYPE_TO_NAME.get(type);
  }

  @SuppressWarnings("unchecked")
  private static Map<Integer, String> extractTypeNames() {
    ImmutableMap.Builder builder = new ImmutableMap.Builder<Integer, String>();
    Field[] fields = Types.class.getDeclaredFields();
    Arrays.stream(fields).forEach(field -> {
      try {
        builder.put(field.get(null), field.getName());
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    });
    return builder.build();
  }
}
