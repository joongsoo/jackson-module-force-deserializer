package software.fitz.jackson.module.force;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.Test;
import org.springframework.http.RequestEntity;

import java.io.IOException;

/**
 * Test for {@link ForceDeserializerModule}
 *
 * @author Joongsoo.Park (https://github.com/joongsoo)
 * @since 2019-12-19
 */
public class ForceDeserializerModuleTest
{
    @Test(expected = MismatchedInputException.class)
    public void shouldFailDeserialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String ser = objectMapper.writeValueAsString(new ImmutableClass("Hi")); // => { "str": "Hi" }
        objectMapper.readValue(ser, ImmutableClass.class);
    }

    @Test
    public void shouldSuccessDeserialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ForceDeserializerModule());
        String ser = objectMapper.writeValueAsString(new ImmutableClass("Hi")); // => { "str": "Hi" }
        ImmutableClass deser = objectMapper.readValue(ser, ImmutableClass.class);

        assertEquals(deser.getStr(), "Hi");
    }

    @Test
    public void shouldSuccessDeserializeCustomImmutableMap() throws IOException {
        // RequestEntity class have ReadOnlyHttpHeaders extending HttpHeaders implementing Map.
        String json = "{\"@class\":\"org.springframework.http.RequestEntity\",\"headers\":{\"@class\":\"org.springframework.http.ReadOnlyHttpHeaders\",\"host\":[\"java.util.LinkedList\",[\"localhost:8080\"]],\"user-agent\":[\"java.util.LinkedList\",[\"insomnia/7.0.1\"]],\"cookie\":[\"java.util.LinkedList\",[\"SESSIONID=abcdefg\"]],\"accept\":[\"java.util.LinkedList\",[\"*/*\"]],\"content-length\":[\"java.util.LinkedList\",[\"100\"]],\"Content-Type\":[\"java.util.LinkedList\",[\"application/json;charset=UTF-8\"]]},\"body\":{\"@class\":\"java.util.LinkedHashMap\",\"params\":[\"java.util.ArrayList\",[{\"@class\":\"java.util.LinkedHashMap\",\"email\":\"wndtn853@gmail.com\"}]]},\"method\":\"POST\",\"url\":\"http://localhost:8080/\",\"type\":null}";

        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@class")
                .registerModule(new ForceDeserializerModule());

        RequestEntity deser = objectMapper.readValue(json, RequestEntity.class);

        assertEquals(objectMapper.writeValueAsString(deser), json);
    }

    private static class ImmutableClass {
        private String str;

        public ImmutableClass(String someStr) {
            this.str = someStr;
        }

        public String getStr() {
            return str;
        }
    }
}
