package org.moodminds.spring;

import org.moodminds.lang.Emittable;
import org.moodminds.lang.Publishable;
import org.moodminds.reactor.adapter.PublishableAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;

import static org.moodminds.lang.Emittable.emittable;
import static org.moodminds.lang.Publishable.publishable;
import static org.moodminds.reactor.adapter.PublishableAdapter.fromReactive;
import static org.moodminds.reactor.adapter.ReactorPublisherAdapter.toReactor;
import static org.springframework.core.ReactiveTypeDescriptor.multiValue;

/**
 * The {@link Emittable} Reactive Adapter Registration configuration bean.
 */
@Configuration
public class ReactiveAdapterRegistration implements InitializingBean {

    /**
     * The reactive adapter registry bean holder field.
     */
    private final ReactiveAdapterRegistry reactiveAdapterRegistry;

    /**
     * Construct the configuration bean object with the specified {@link ReactiveAdapterRegistry}.
     *
     * @param reactiveAdapterRegistry the specified {@link ReactiveAdapterRegistry}
     */
    public ReactiveAdapterRegistration(@Autowired(required = false) ReactiveAdapterRegistry reactiveAdapterRegistry) {
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
    }

    /**
     * Register the {@link Emittable} in the available {@link ReactiveAdapterRegistry}
     * instances at the end of the bean initialization.
     */
    @Override
    public void afterPropertiesSet() {
        registerAdapter(ReactiveAdapterRegistry.getSharedInstance());
        if (reactiveAdapterRegistry != null) registerAdapter(reactiveAdapterRegistry);
    }

    /**
     * Register the {@link Emittable} in the specified {@link ReactiveAdapterRegistry}.
     */
    private void registerAdapter(ReactiveAdapterRegistry reactiveAdapterRegistry) {
        reactiveAdapterRegistry.registerReactiveType(
            multiValue(Emittable.class, () -> emittable(publishable())),
            publishable -> toReactor(((Emittable<?, ?>) publishable)::subscribe),
            publisher -> emittable(fromReactive(publisher))
        );

        reactiveAdapterRegistry.registerReactiveType(
            multiValue(Publishable.class, Publishable::publishable),
            publishable -> toReactor(((Publishable<?, ?>) publishable)::subscribe),
            PublishableAdapter::fromReactive
        );
    }
}
