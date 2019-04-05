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

import com.cyr1en.flatdb.types.SQLTypePair;
import com.cyr1en.flatdb.types.TypeMap;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.sql.Types;
import java.util.Map;

public class TypeMapTest {

  /**
   * Test adding a custom type using {@link TypeMap#addCustomType(Class, SQLTypePair)}
   */
  @Test
  public void a() {
    Assertions.assertThat(TypeMap.getSQLType(TestClass.class)).isNull();
    TypeMap.addCustomType(TestClass.class, SQLTypePair.of(Types.VARCHAR, "Default_Value"));
    SQLTypePair typePair = TypeMap.getSQLType(TestClass.class);
    Assertions.assertThat(typePair).isNotNull();
    Assertions.assertThat(typePair.getType()).isEqualTo(Types.VARCHAR);
    Assertions.assertThat(typePair.getDefaultValue()).isEqualTo("Default_Value");
  }

  /**
   * Test adding a custom type with override {@link TypeMap#addCustomType(Class, SQLTypePair, boolean)}
   */
  @Test
  public void b() {
    TypeMap.addCustomType(TestClass.class, SQLTypePair.of(Types.INTEGER, "0"), true);
    SQLTypePair typePair = TypeMap.getSQLType(TestClass.class);
    Assertions.assertThat(typePair).isNotNull();
    Assertions.assertThat(typePair.getType()).isEqualTo(Types.INTEGER);
    Assertions.assertThat(typePair.getDefaultValue()).isEqualTo("0");
  }

  /**
   * Test adding custom types by passing a {@link Map} to {@link TypeMap#addCustomType(Map)}; without override.
   */
  @Test
  public void c() {
    Map<Class<?>, SQLTypePair> customMap = new ImmutableMap.Builder<Class<?>, SQLTypePair>()
            .put(TestClass.class, SQLTypePair.of(Types.BIT, "1"))
            .put(TestClass2.class, SQLTypePair.of(Types.VARCHAR, "null")).build();
    TypeMap.addCustomType(customMap);
    SQLTypePair typePair1 = TypeMap.getSQLType(TestClass.class);
    Assertions.assertThat(typePair1).isNotNull();
    Assertions.assertThat(typePair1.getType()).isEqualTo(Types.INTEGER);
    Assertions.assertThat(typePair1.getDefaultValue()).isEqualTo("0");

    SQLTypePair typePair2 = TypeMap.getSQLType(TestClass2.class);
    Assertions.assertThat(typePair2).isNotNull();
    Assertions.assertThat(typePair2.getType()).isEqualTo(Types.VARCHAR);
    Assertions.assertThat(typePair2.getDefaultValue()).isEqualTo("null");
  }

  /**
   * Test remove custom types by passing a {@link Map} to {@link TypeMap#removeCustomType(Map)}.
   */
  @Test
  public void d() {
    Map<Class<?>, SQLTypePair> customMap = new ImmutableMap.Builder<Class<?>, SQLTypePair>()
            .put(TestClass.class, SQLTypePair.of(Types.BIT, "1"))
            .put(TestClass2.class, SQLTypePair.of(Types.VARCHAR, "null")).build();
    TypeMap.removeCustomType(customMap);
    Assertions.assertThat(TypeMap.getSQLType(TestClass.class)).isNull();
    Assertions.assertThat(TypeMap.getSQLType(TestClass.class)).isNull();
  }

  private class TestClass {
  }

  private class TestClass2 {
  }
}
