package com.aol.cyclops.scala.collections;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PSet;

import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPSetX;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
import reactor.core.publisher.Flux;
import scala.collection.GenTraversableOnce;
import scala.collection.JavaConversions;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.HashSet$;
import scala.collection.mutable.Builder;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScalaHashPSet<T> extends AbstractSet<T>implements PSet<T>, HasScalaCollection<T> {

    /**
     * Create a LazyPSetX from a Stream
     * 
     * @param stream to construct a LazyQueueX from
     * @return LazyPSetX
     */
    public static <T> LazyPSetX<T> fromStream(Stream<T> stream) {
        return new LazyPSetX<T>(
                                  Flux.from(ReactiveSeq.fromStream(stream)), toPSet());
    }

    /**
     * Create a LazyPSetX that contains the Integers between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range SetX
     */
    public static LazyPSetX<Integer> range(int start, int end) {
        return fromStream(ReactiveSeq.range(start, end));
    }

    /**
     * Create a LazyPSetX that contains the Longs between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range SetX
     */
    public static LazyPSetX<Long> rangeLong(long start, long end) {
        return fromStream(ReactiveSeq.rangeLong(start, end));
    }

    /**
     * Unfold a function into a SetX
     * 
     * <pre>
     * {@code 
     *  LazyPSetX.unfold(1,i->i<=6 ? Optional.of(Tuple.tuple(i,i+1)) : Optional.empty());
     * 
     * //(1,2,3,4,5)
     * 
     * }</pre>
     * 
     * @param seed Initial value 
     * @param unfolder Iteratively applied function, terminated by an empty Optional
     * @return SetX generated by unfolder function
     */
    public static <U, T> LazyPSetX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return fromStream(ReactiveSeq.unfold(seed, unfolder));
    }

    /**
     * Generate a LazyPSetX from the provided Supplier up to the provided limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param s Supplier to generate SetX elements
     * @return SetX generated from the provided Supplier
     */
    public static <T> LazyPSetX<T> generate(long limit, Supplier<T> s) {

        return fromStream(ReactiveSeq.generate(s)
                                     .limit(limit));
    }

    /**
     * Create a LazyPSetX by iterative application of a function to an initial element up to the supplied limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param seed Initial element
     * @param f Iteratively applied to each element to generate the next element
     * @return SetX generated by iterative application
     */
    public static <T> LazyPSetX<T> iterate(long limit, final T seed, final UnaryOperator<T> f) {
        return fromStream(ReactiveSeq.iterate(seed, f)
                                     .limit(limit));
    }

    /**
     * <pre>
     * {@code 
     * PSet<Integer> q = JSPSet.<Integer>toPSet()
                                     .mapReduce(Stream.of(1,2,3,4));
     * 
     * }
     * </pre>
     * @return Reducer for PSet
     */
    public static <T> Reducer<PSet<T>> toPSet() {
        return Reducer.<PSet<T>> of(ScalaHashPSet.emptyPSet(), (final PSet<T> a) -> b -> a.plusAll(b),
                                      (final T x) -> ScalaHashPSet.singleton(x));
    }

    public static <T> ScalaHashPSet<T> fromSet(HashSet<T> set) {
        return new ScalaHashPSet<>(
                                 set);
    }

    public static <T> ScalaHashPSet<T> emptyPSet() {
  
        return new ScalaHashPSet<>(
                                 HashSet$.MODULE$.empty());
    }

    public static <T> LazyPSetX<T> empty() {
        
        HashSet<T> empty = HashSet$.MODULE$.empty();
        return LazyPSetX.fromPSet(new ScalaHashPSet<T>(
                                                        empty),toPSet());
    }

    public static <T> LazyPSetX<T> singleton(T t) {
        return of(t);
    }

    public static <T> LazyPSetX<T> of(T... t) {

         Builder<T, HashSet> lb = HashSet$.MODULE$.newBuilder();
        for (T next : t)
            lb.$plus$eq(next);
        HashSet<T> vec = lb.result();
        return LazyPSetX.fromPSet(new ScalaHashPSet<>(
                                                        vec),
                                      toPSet());
    }

    public static <T> LazyPSetX<T> PSet(HashSet<T> q) {
        return LazyPSetX.fromPSet(new ScalaHashPSet<T>(
                                                         q),
                                      toPSet());
    }

    @SafeVarargs
    public static <T> LazyPSetX<T> PSet(T... elements) {
        return LazyPSetX.fromPSet(of(elements), toPSet());
    }

    @Wither
    private final HashSet<T> set;

    @Override
    public ScalaHashPSet<T> plus(T e) {
       
        return withSet(set.$plus(e));
    }

    @Override
    public ScalaHashPSet<T> plusAll(Collection<? extends T> l) {
       
        HashSet<T> res = HasScalaCollection.<T,HashSet<T>>visit(HasScalaCollection.narrow(l), scala->
            (HashSet<T>) set.$plus$plus(scala.traversable(),  scala.canBuildFrom())
        , java->{
            HashSet<T> vec = set;
            for (T next : l) {
                vec = vec.$plus(next);
          }
            return vec;
        });

        return withSet(res);
       
    }

   

    
  

    @Override
    public PSet<T> minus(Object e) {
        return withSet(set.$minus((T)e));
        
    }

    @Override
    public PSet<T> minusAll(Collection<?> s) {
        GenTraversableOnce<T> col = HasScalaCollection.<T>traversable((Collection)s);
        return withSet((HashSet)set.$minus$minus(col));        
    }

  
   

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public Iterator<T> iterator() {
        return JavaConversions.asJavaIterator(set.iterator());
    }

    @Override
    public GenTraversableOnce<T> traversable() {
       return set;
    }

    @Override
    public CanBuildFrom canBuildFrom() {
       return HashSet.canBuildFrom();
      
    }

   

}
