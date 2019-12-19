# jackson-module-force-creator
This module is born [jackson-databind#2570](https://github.com/FasterXML/jackson-databind/issues/2570)

If implements serialize/deserialize using jackson, developers should be careful about the data consistency of serialization and deserialization.

For example. developer will implements to caching for this class.

```java
class SomeClass {
    private String str;
    
    public SomeClass(String someStr) {
        this.str = someStr;
    }

    public String getStr() {
        return str;
    }
}

new ObjectMapper().writeAsString(new SomeClass("Hi"));
```

This code is serialized as this string.

```
{
    "str": "Hi"
}
```

But jackson is not deserialize. because jackson is using default constructor for create instance. (or `@JsonCreator`)

If using this module, possible to serialize this class. because this module is created object using objenesis. so create object by-passing Object initialization.


## How to use
### Add dependency
#### Maven
```xml
<dependency>
    <groupId>software.fitz</groupId>
    <artifactId>jackson-module-force-creator</artifactId>
    <version>0.1.0-RELEASE</version>
</dependency>
```

#### Gradle
```groovy
compile group: 'software.fitz', name: 'jackson-module-force-creator', version: '0.1.0-RELEASE'
```

#### Apply module
It is very simple. register module to `ObjectMapper` class.

```java
ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new ForceCreatorModule());
```