package net.anastasakis.pattern.chain;

import java.util.Optional;

/**
 * A Representation of the Repository that holds handlers.
 *
 * @param <R> The type of the request object.
 * @param <T>
 */
public interface HandlerRepository<R, T> {
    Optional<Handler<R, T>> getHandler(R request);

    default boolean hasHandler(R request){
        return getHandler(request).isPresent();
    }

    default Optional<T> invokeAppropriateHandler(R request) {
        final var handler = getHandler(request);
        return handler.map(h-> h.doApply(request));
    }
}
