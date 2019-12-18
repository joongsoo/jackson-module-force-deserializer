package software.fitz.jackson.module.creator;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Joongsoo.Park (https://github.com/joongsoo)
 * @since 2019-12-19
 */
class ForceValueInstantiator extends ValueInstantiator {

    private static final Objenesis OBJENESIS = new ObjenesisStd();
    private static final ConcurrentMap<Class<?>, ForceValueInstantiator> INSTANTIATOR_CACHE = new ConcurrentHashMap<>();

    private Class<?> type;

    private ForceValueInstantiator(Class<?> type) {
        this.type = type;
    }

    public static ForceValueInstantiator getInstance(Class<?> type) {
        ForceValueInstantiator instantiator = INSTANTIATOR_CACHE.putIfAbsent(type, new ForceValueInstantiator(type));

        if (instantiator == null) {
            return INSTANTIATOR_CACHE.get(type);
        }
        return instantiator;
    }

    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }

    @Override
    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        return OBJENESIS.newInstance(type);
    }
}
