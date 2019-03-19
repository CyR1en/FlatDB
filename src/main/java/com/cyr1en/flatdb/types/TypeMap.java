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

import java.math.BigDecimal;
import java.util.Map;

public class TypeMap {

  private static final Map<Class<?>, SQLTypePair> JAVADT_TO_SQLDT = new ImmutableMap.Builder<Class<?>, SQLTypePair>()
          .put(Boolean.class, SQLTypePair.of("BIT", "0"))
          .put(String.class, SQLTypePair.of("VARCHAR", "null"))
          .put(BigDecimal.class, SQLTypePair.of("NUMERIC", "0"))
          .put(Integer.class, SQLTypePair.of("INTEGER", "0"))
          .put(Long.class, SQLTypePair.of("BIGINT", "0"))
          .put(Float.class, SQLTypePair.of("REAL", "0.0"))
          .put(Double.class, SQLTypePair.of("FLOAT", "0.0"))
          .build();

  public static SQLTypePair getSQLType(Class<?> jClass) {
    return jClass.isPrimitive() ? JAVADT_TO_SQLDT.get(Primitives.wrap(jClass)) : JAVADT_TO_SQLDT.get(jClass);
  }

}
