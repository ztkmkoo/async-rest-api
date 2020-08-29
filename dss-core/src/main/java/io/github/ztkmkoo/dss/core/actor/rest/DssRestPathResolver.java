package io.github.ztkmkoo.dss.core.actor.rest;

import akka.actor.typed.ActorRef;
import io.github.ztkmkoo.dss.core.exception.DssRestServiceMappingException;
import io.github.ztkmkoo.dss.core.message.rest.DssRestServiceActorCommand;
import io.github.ztkmkoo.dss.core.network.rest.enumeration.DssRestMethodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Project: dss
 * Created by: @ztkmkoo(ztkmkoo@gmail.com)
 * Date: 20. 3. 14. 오후 8:44
 */
public class DssRestPathResolver {

    private static final Logger logger = LoggerFactory.getLogger(DssRestPathResolver.class);

    public static Builder builder() {
        return new Builder();
    }

    private final Map<DssRestMethodType, Map<String, ActorRef<DssRestServiceActorCommand>>> staticServiceActorMap;

    private DssRestPathResolver(Builder builder) {
        this.staticServiceActorMap = Collections.unmodifiableMap(builder.staticServiceActorMap);
    }

    public Optional<ActorRef<DssRestServiceActorCommand>> getStaticServiceActor(DssRestMethodType methodType, String path) {
        return Optional
                .ofNullable(
                        staticServiceActorMap
                                .getOrDefault(methodType, Collections.emptyMap())
                                .get(path)
                );
    }

    public static class Builder {

        private Map<DssRestMethodType, Map<String, ActorRef<DssRestServiceActorCommand>>> staticServiceActorMap = new EnumMap<>(DssRestMethodType.class);

        public Builder addServiceActor(DssRestMethodType methodType, String path, ActorRef<DssRestServiceActorCommand> serviceActor) {
            Objects.requireNonNull(methodType);
            Objects.requireNonNull(path);
            Objects.requireNonNull(serviceActor);

            if (!staticServiceActorMap.containsKey(methodType)) {
                staticServiceActorMap.put(methodType, new HashMap<>());
            }

            if (staticServiceActorMap.get(methodType).containsKey(path)) {
                throw new DssRestServiceMappingException("Cannot map same path: " + path);
            }
            staticServiceActorMap.get(methodType).put(path, serviceActor);
            return this;
        }

        public DssRestPathResolver build() {
            if (!staticServiceActorMap.isEmpty()) {
                staticServiceActorMap.forEach((methodType, map) -> map.forEach((path, actorRef) ->
                        logger.info("Add mapping {} {} to {}",
                                methodType.name(), path, actorRef.path().name())
                ));
            }

            return new DssRestPathResolver(this);
        }
    }
}
