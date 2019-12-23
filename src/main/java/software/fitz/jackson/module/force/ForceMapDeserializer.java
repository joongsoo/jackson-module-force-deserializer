package software.fitz.jackson.module.force;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import software.fitz.jackson.module.force.ex.BeanCreationException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class ForceMapDeserializer extends MapDeserializer {

    public ForceMapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(mapType, valueInstantiator, keyDeser, valueDeser, valueTypeDeser);
    }

    public ForceMapDeserializer(MapDeserializer src) {
        super(src);
    }

    protected ForceMapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable) {
        super(src, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable);
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (!_valueInstantiator.canInstantiate()) {

            // Usually, readonly type is wrapped super class
            for(Constructor<?> constructor : this._valueClass.getDeclaredConstructors()) {

                if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == _valueClass.getSuperclass()) {
                    MapType superType = ctxt.getTypeFactory().constructMapType(
                            (Class<? extends Map>) _valueClass.getSuperclass(),
                            _containerType.getKeyType(),
                            _containerType.getContentType());

                    BeanDescription superTypeBeanDescription = ctxt.getConfig().getClassIntrospector()
                            .forDeserialization(
                                    ctxt.getConfig(),
                                    ctxt.getTypeFactory().constructType(_valueClass.getSuperclass()),
                                    ctxt.getConfig());

                    Map<Object, Object> superTypeDeser = new MapDeserializer(
                            superType,
                            ctxt.getFactory().findValueInstantiator(ctxt, superTypeBeanDescription),
                            StdKeyDeserializer.forType(ctxt.getTypeFactory().constructType(_valueClass.getSuperclass().getGenericInterfaces()[0]).getRawClass()),
                            ctxt.findNonContextualValueDeserializer(ctxt.getTypeFactory().constructType(_valueClass.getSuperclass().getGenericInterfaces()[1])),
                            ctxt.getFactory().findTypeDeserializer(ctxt.getConfig(), ctxt.getTypeFactory().constructType(_valueClass.getSuperclass().getGenericInterfaces()[1]))
                    ).deserialize(p, ctxt);

                    try {
                        boolean accessible = constructor.isAccessible();

                        if (!accessible) {
                            constructor.setAccessible(true);
                        }

                        try {
                            return (Map<Object, Object>) constructor.newInstance(superTypeDeser);
                        } finally {
                            if (!accessible) {
                                constructor.setAccessible(false);
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new BeanCreationException("Create instance \"" + this._valueClass.getName() + "\" failed", e);
                    }
                }
            }
        }

        return super.deserialize(p, ctxt);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        return new ForceMapDeserializer((MapDeserializer) super.createContextual(ctxt, property));
    }
}
