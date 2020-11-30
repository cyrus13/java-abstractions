package net.anastasakis.pattern.chain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FirstMatchingHandlerRepositoryTest {

    @Test
    void usageExample(){
        final HandlerRepository<Integer,String > repository =
                FirstMatchingHandlerRepository.<Integer,String>builder()
                .handler(new EventNumberHandler())
                .handler(new OddNumberHanlder())
                .build();

        final Stream<Integer> numbersStream = Stream.of(3,5,2,10);

        final List<String> stringValues =  numbersStream.map( n-> repository.invokeAppropriateHandler(n))
            .map(Optional::get)
                .collect(Collectors.toList());
        assertEquals("Odd",stringValues.get(0));
        assertEquals("Odd",stringValues.get(1));
        assertEquals("Even",stringValues.get(2));
        assertEquals("Even",stringValues.get(3));
    }

    private static class EventNumberHandler implements Handler<Integer,String>{
        @Override
        public boolean canApply(Integer request) {
            return request % 2.0 == 0 ;
        }

        @Override
        public String doApply(Integer request) {
            return "Even";
        }
    }

    private static class OddNumberHanlder implements Handler<Integer,String>{
        @Override
        public boolean canApply(Integer request) {
            return request % 2.0 != 0 ;
        }

        @Override
        public String doApply(Integer request) {
            return "Odd";
        }
    }
}
