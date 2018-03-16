package eu.hlavki.identity.services.rest.util;

import static java.util.Collections.singletonList;
import javax.validation.ValidationProviderResolver;
import javax.validation.spi.ValidationProvider;

import java.util.List;

/**
 * OSGi-friendly implementation of {@code javax.validation.SingleValidationProviderResolver} returning
 * {@code org.hibernate.validator.HibernateValidator} instance.
 *
 */
public class SingleValidationProviderResolver implements ValidationProviderResolver {

    private final ValidationProvider<?> provider;


    public SingleValidationProviderResolver(ValidationProvider<?> provider) {
        this.provider = provider;
    }


    @Override
    public List<ValidationProvider<?>> getValidationProviders() {
        return singletonList(provider);
    }
}
