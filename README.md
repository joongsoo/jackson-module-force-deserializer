# jackson-module-force-deserializer
This module is born [jackson-databind#2570](https://github.com/FasterXML/jackson-databind/issues/2570)

This module is support deserialize all possible types by force create. it is possible to deserialize many types that could not be deserialized in existing modules.

`@JsonCreator` may be used, but this is too cumbersome. And it may be need to serialize class developed by other developers.

Alternatively, the [jackson-module-parameter-names](https://github.com/FasterXML/jackson-modules-java8) module can be choice. but it does not support versions under 8. and it has some limitations that cannot be deserialize.

For example. this class is immutable.

```java
class ImmutableClass {
    private String str;
    
    public ImmutableClass(String someStr) {
        this.str = someStr;
    }

    public String getStr() {
        return str;
    }
}

new ObjectMapper().writeAsString(new SomeClass("Hi"));
```

This code is serialized as `{ "str": "Hi" }`.

But jackson is not deserialize it. because jackson is using default constructor for create instance. (or `@JsonCreator`) and same is true for the parameter module. because not matched parameter name and field name.

If using this module, possible to serialize this class. because this module is created object using [objenesis](http://objenesis.org/). so create object by-passing Object initialization.

It is inject value through field, setter after force creation. so possible to support more type.


## How to use
### Add dependency
#### Maven
```xml
<dependency>
    <groupId>software.fitz</groupId>
    <artifactId>jackson-module-force-deserializer</artifactId>
    <version>0.1.0-RELEASE</version>
</dependency>
```

#### Gradle
```groovy
compile group: 'software.fitz', name: 'jackson-module-force-deserializer', version: '0.1.0-RELEASE'
```

#### Register module
It is very simple. register module to `ObjectMapper` class.

```java
ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new ForceDeserializerModule());
```