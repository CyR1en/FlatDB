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

package com.cyr1en.flatdb.util;

import java.util.StringJoiner;

/**
 * Utility class for Strings
 *
 * <p>This is my own implementation of methods that could be found in
 * Apache Commons Lang's StringUtils</p>
 */
public class Strings {

  /**
   * Check if a {@link CharSequence} is blank.
   *
   * <p>This methods checks if the length of the {@link CharSequence}
   * is 0, if true, that means the sequence is indeed blank. This method
   * also checks if all code points in the sequence is equal to a
   * SPACE/0x00000020</p>
   *
   * @param charSequence {@link CharSequence} that you want to check.
   * @return if the {@link CharSequence} is blank.
   */
  public static boolean isBlank(CharSequence charSequence) {
    if (isEmpty(charSequence)) return true;
    for(int i = 0; i < charSequence.length(); i++) {
      if((int) charSequence.charAt(i) != 0x00000020)
        return false;
    }
    return true;
  }

  public static String join(Object[] array, CharSequence delimiter) {
    StringJoiner joiner = new StringJoiner(delimiter);
    for (Object obj : array)
      joiner.add(String.valueOf(obj));
    return joiner.toString();
  }

  /**
   * Count the number of occurrences of a {@link CharSequence} in a different
   * {@link CharSequence}.
   *
   * <p>This does not support non-planar characters (characters with code points > 0xFFFF).
   * If the argument is a non-planar character, the method will just return 0</p>
   *
   * @param original The original {@link CharSequence} where this method will be counting
   *                 occurrences from.
   * @param argument The {@link CharSequence} that we will count the number of occurrences
   *                 from the parameter "original".
   * @return How many times the argument occurred in the parameter original.
   */
  public static int countOccurrences(CharSequence original, char argument) {
    if (isBlank(original))
      return 0;
    int count = 0;
    for (int i = 0; i < original.length(); ++i)
      if (argument == original.charAt(i))
        count++;
    return count;
  }

  public static boolean isEmpty(CharSequence charSequence) {
    return charSequence == null || charSequence.length() == 0;
  }

}
