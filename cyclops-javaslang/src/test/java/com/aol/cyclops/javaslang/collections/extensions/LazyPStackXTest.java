package com.aol.cyclops.javaslang.collections.extensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.data.collections.extensions.persistent.PBagX;
import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.javaslang.collections.JavaSlangPStack;
import com.aol.cyclops.reactor.collections.extensions.AbstractOrderDependentCollectionXTest;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPStackX;

import reactor.core.publisher.Flux;

public class LazyPStackXTest extends AbstractOrderDependentCollectionXTest  {

    @Override
    public <T> FluentCollectionX<T> of(T... values) {
        LazyPStackX<T> list = JavaSlangPStack.empty();
        for (T next : values) {
            list = list.plus(list.size(), next);
        }
        System.out.println("List " + list);
        return list.efficientOpsOff();

    }

    @Test
    public void onEmptySwitch() {
        assertThat(JavaSlangPStack.empty()
                          .onEmptySwitch(() -> PStackX.of(1, 2, 3)),
                   equalTo(PStackX.of(1, 2, 3)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.functions.collections.extensions.AbstractCollectionXTest#
     * empty()
     */
    @Override
    public <T> FluentCollectionX<T> empty() {
        return JavaSlangPStack.empty();
    }

    

    @Test
    public void remove() {

        JavaSlangPStack.of(1, 2, 3)
               .minusAll(PBagX.of(2, 3))
               .flatMapPublisher(i -> Flux.just(10 + i, 20 + i, 30 + i));

    }

    @Override
    public FluentCollectionX<Integer> range(int start, int end) {
        return JavaSlangPStack.range(start, end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start, long end) {
        return JavaSlangPStack.rangeLong(start, end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times, T seed, UnaryOperator<T> fn) {
        return JavaSlangPStack.iterate(times, seed, fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times, Supplier<T> fn) {
        return JavaSlangPStack.generate(times, fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return JavaSlangPStack.unfold(seed, unfolder);
    }
}
