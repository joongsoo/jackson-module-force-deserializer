# jackson-module-force-creator
## Summary
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
