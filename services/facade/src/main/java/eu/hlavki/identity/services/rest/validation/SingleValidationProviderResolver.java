package eu.hlavki.identity.services.rest.validation;

import static java.util.Collections.singletonList;
import java.util.List;
import javax.validation.ValidationProviderResolver;
import javax.validation.spi.ValidationProvider;

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
