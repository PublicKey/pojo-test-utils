# pojo-test-utils
Collection of unit test utility classes to improve code coverage for POJO (Plain Old Java Object) classes

Maven:
```xml
<dependency>
  <groupId>com.github.publickey</groupId>
  <artifactId>pojo-test-utils</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
```

Note: Library depends on:
  Java 1.8
  JUnit 4.12

## Components:

AbstractPojoTester - The main abstract base class meant to be extended by Unit Tests

## Usage:

Here's an example of simple Pojo under test
```java
import org.junit.Test;

import com.github.publickey.test.pojo.AbstractPojoTester;

public class MyPojoTest extends AbstractPojoTester {
    @Test
    public void testMyPojo() throws Exception {
        super.testPojoAllConstructors(MyPojo.class);
    }
}
```

Here's an example of simple Pojo under test
```java
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import com.github.publickey.test.pojo.AbstractPojoTester;

public class MyPojo2Test extends AbstractPojoTester {
    @Before
    public void setUp() throws Exception {
        // set custom value of type String to be provided (i.e. MyPojo2 performs 
        // validation and requires the following format)
        super.putTestValue(String.class, "special-string");
    }

    @Test
    public void testMyPojo2() throws Exception {
        // perform standard POJO test on MyPojo2.class 
        // but also invoke someMethod and make sure no exception is thrown
        super.testPojo(MyPojo2.class, "someMethod");
    }
    
    @Test
    public void testMyPojo2CustomTest() throws Exception {
        // custom custom 
        MyPojo2 myPojo2 = new MyPojo2("Custom value");
        assertEquals("Custom value", myPojo2.calculate());
    }
}
```

The Maven artifacts are deployed with the Maven Repository Switchboard at:
http://repo1.maven.org/maven2/com/github/publickey/pojo-test-utils/

Sample repository configuration
```xml
<repository>
	<id>central</id>
	<name>Maven Repository Switchboard</name>
	<layout>default</layout>
	<url>http://repo1.maven.org/maven2/</url>
	<snapshots>
		<enabled>false</enabled>
	</snapshots>
</repository>
```

The Maven artifacts are also avaialble through with Sonatype at:
https://oss.sonatype.org/service/local/repositories/releases/content/
