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

import com.cyr1en.flatdb.util.FastStrings;
import com.cyr1en.flatdb.util.Pair;

public class SQLTypePair extends Pair<Integer, String> {

  public SQLTypePair(Integer o, String o2) {
    super(o, o2);
  }

  public Integer getType() {
    return getX();
  }

  public String getTypeName() {
    return TypeMap.getName(getType());
  }

  public String getDefaultValue() {
    return getY();
  }

  public static SQLTypePair of(int type, String defaultVal) {
    assertValidType(type);
    assertNotBlank(defaultVal);
    return new SQLTypePair(type, defaultVal);
  }

  private static void assertValidType(int i) {
    if(!TypeMap.TYPE_TO_NAME.containsKey(i))
      throw new IllegalArgumentException(i + " is not a valid SQL Type.");
  }

  private static void assertNotBlank(String s) {
    if(FastStrings.isBlank(s))
      throw new IllegalArgumentException("SQLType pair value cannot be an blank string!");
  }
}
