package camp.xit.identity.services.util;

import static java.util.Collections.singletonList;
import javax.validation.ValidationProviderResolver;
import java.util.List;

/**
 * OSGi-friendly implementation of {@code javax.validation.SingleValidationProviderResolver} returning
 * {@code org.hibernate.validator.HibernateValidator} instance.
 *
 */
public class SingleValidationProviderResolver implements ValidationProviderResolver {

    private final Object provider;


    public SingleValidationProviderResolver(Object provider) {
        this.provider = provider;
    }


    @Override
    public List getValidationProviders() {
        return singletonList(provider);
    }
}
