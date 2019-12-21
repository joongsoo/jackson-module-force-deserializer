package software.fitz.jackson.module.force;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.Test;

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
