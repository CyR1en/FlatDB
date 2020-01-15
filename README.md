# FlatDB [![Action Status](https://github.com/CyR1en/FlatDB/workflows/Java%20CI/badge.svg)](https://github.com/CyR1en/FlatDB/actions) [![](https://jitpack.io/v/cyr1en/flatdb.svg)](https://jitpack.io/#cyr1en/flatdb)
An easy to use library for flat-file databases.

### Features
- Create tables using Java classes.
- Auto-update tables when new fields are added in the Java class.
- Easily connect to your database file.

### Requirements
Java 8+

### Dependencies
- [Google Guava](https://github.com/google/guava)
- [IntelliJ IDEA Annotations](https://mvnrepository.com/artifact/com.intellij/annotations)

FlatDB comes with [H2 Driver](https://mvnrepository.com/artifact/com.h2database/h2).
You can exclude it in your build tool if you wish to use a different driver.

---
### Getting Started
Add FlatDB as dependency.
#### Gradle
```groovy
repositories {
  maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
  compile 'com.github.cyr1en:flatdb:LATEST_VERSION'
}
```
#### Maven
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
```xml
<dependency>
  <groupId>com.github.cyr1en</groupId>
  <artifactId>flatdb</artifactId>
  <version>LATEST_VERSION</version>
</dependency>
```
---
### Using FlatDB

#### Creating a Table
Let's first create a table that we will pass down to the DatabaseBuilder.
```java
@Table(nameOverride = "table")
public class TestTable {
  @Column(autoIncrement = true) int id;
  @Column(primaryKey = true) UUID uuid;
  @Column String name;
  @Column String lastName;
}
```
The class above will get translated to:

| id | uuid | name | lastname |
|----|------|------|----------|
|    |      |      |          |

#### Connecting to Database
Now let's configure our connection to the database using the DatabaseBuilder class.
Here, we're setting the path in which the database is located. If the database file doesn't exist, it will be generated.
We're also setting the prefix of the tables to be "test_". Lastly, we now add the table that we created above.
```java 
DatabaseBuilder builder = new DatabaseBuilder()
        .setPath(System.getProperty("user.dir") + "/testDb/flatDB")
        .setDatabasePrefix("test_")
        .appendTable(TestTable.class);
try {
  Database db = builder.build();
} catch (SQLException e) {
  e.printStackTrace();
}
```

After that, it's pretty much a generic JDBC experience. Except the Database#executeQuery() function returns an Optional<ResultSet>.
#### To do a query
```java
Optional<ResutlSet> result = db.executeQuery("SELECT * FROM 'test_table' WHERE name = 'someName'");
result.ifPresent(rs -> System.out.println("Hey I'm present"));
```
---
### Additional Configurations

#### Defining Custom Type Maps.
FlatDB automatically converts the data types of a field into an SQL type equivalent. 
However, by default, FlatDB doesn't have all the data types mapped.
To solve this, we can define our own type conversions in the TypeMap class where we need to provide a java class,
and an SQLTypePair(Types.SOME_TYPE, "default value").

###### Using DatabaseBuilder
```java
DatabaseBuilder builder = new DatabaseBuilder();
builder.addCustomType(UUID.class, SQLTypePair.of(Types.VARCHAR, "null"));
```
###### Using TypeMap
```java
//The last parameter indicates if we should override the definition for UUID.class
TypeMap.addCustomType(UUID.class, SQLTypePair.of(Types.VARCHAR, "null"), true);
```
To see more examples: [Click here](https://github.com/CyR1en/FlatDB/blob/master/src/test/java/com/cyr1en/flatdb/TypeMapTest.java)
#### Process Tables in Runtime
In cases where new classes are loaded in runtime, and additional tables need to be processed. The TableProcessor class allows us to do so.
```java
Database database; //assuming database is already initialized.
TableProcessor tableProcessor = new TableProcessor(database);
tableProcessor.process(SomeClass.class);
```
---
### Disclaimer
[DBTablePrinter](https://github.com/htorun/dbtableprinter) by [hturon](https://github.com/htorun) is 
embedded in FlatDB to allow users to easily print their ResultSets. 
See [DBTablePrinter](https://github.com/CyR1en/FlatDB/blob/master/src/main/java/com/cyr1en/flatdb/util/DBTablePrinter.java) in this project.
