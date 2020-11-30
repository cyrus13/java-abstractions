package net.anastasakis.pattern.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

/**
 * @param <R> The type of the request object
 * @param <T> The type of the response object. Can be {@link Void} if we don't want to return any value from this handler
 */
@RequiredArgsConstructor
@Builder
public class FirstMatchingHandlerRepository<R, T> implements HandlerRepository<R, T> {
    @Singular
    private final List<Handler<R, T>> handlers;

    public Optional<Handler<R, T>> getHandler(R request) {
        for (Handler<R, T> handler : handlers) {
            if (handler.canApply(request)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }
}
