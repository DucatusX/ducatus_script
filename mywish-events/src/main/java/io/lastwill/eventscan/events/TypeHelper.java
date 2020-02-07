package io.lastwill.eventscan.events;

import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class TypeHelper {
    public BigInteger toBigInteger(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof BigInteger) {
            return (BigInteger) o;
        }
        if (o instanceof String) {
            return new BigInteger((String) o);
        }
        if (o instanceof Number) {
            return BigInteger.valueOf(((Number) o).longValue());
        }
        throw new UnsupportedOperationException("Unsupported type " + o.getClass());
    }
}
