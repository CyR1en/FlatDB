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

import com.cyr1en.flatdb.annotations.Column;
import com.cyr1en.flatdb.annotations.Table;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class DatabaseBuildTest {

  @Test
  public void test() {
    DatabaseBuilder builder = new DatabaseBuilder()
            .setPath(System.getProperty("user.dir") + "/testDb/flatDB")
            .setDatabasePrefix("test_")
            .appendTable(TestTable.class);

    AtomicReference<Database> db = new AtomicReference<>();
    Assertions.assertThatCode(() -> db.set(builder.build())).doesNotThrowAnyException();
    Assertions.assertThat(db.get().tableExists("test_table1")).isEqualTo(true);
  }

  @Table(nameOverride = "table1")
  private class TestTable {
    @Column(primaryKey = true, autoIncrement = true) private int id;
    @Column private String name;
    @Column private String lastName;
  }
}
