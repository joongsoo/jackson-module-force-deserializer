package software.fitz.jackson.module.force;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * @author Joongsoo.Park (https://github.com/joongsoo)
 * @since 2019-12-19
 */
class ForceCreatorBeanDeserializerModifier extends BeanDeserializerModifier {

    private static final ForceCreatorBeanDeserializerModifier INSTANCE = new ForceCreatorBeanDeserializerModifier();

    public static ForceCreatorBeanDeserializerModifier getInstance() {
        return INSTANCE;
    }

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                 BeanDescription beanDesc,
                                                 BeanDeserializerBuilder builder) {
        ValueInstantiator valueInstantiator = builder.getValueInstantiator();
        Class<?> deserType = beanDesc.getBeanClass();

        boolean forceCreationTarget = !deserType.isPrimitive()
                && deserType != String.class
                && beanDesc.findDefaultConstructor() == null
                && isNotPossibleInstantiation(valueInstantiator);

        if (forceCreationTarget) {
            builder.setValueInstantiator(ForceValueInstantiator.getInstance(deserType));
        }

        return builder;
    }

    @Override
    public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config, MapType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return new ForceMapDeserializer((MapDeserializer) deserializer);
    }

    private static boolean isNotPossibleInstantiation(ValueInstantiator valueInstantiator) {
        return !(valueInstantiator.canCreateUsingDelegate()
                || valueInstantiator.canCreateUsingArrayDelegate()
                || valueInstantiator.canCreateFromObjectWith());
    }
}
