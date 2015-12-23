package com.aol.cyclops.comprehensions.comprehenders;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.aol.cyclops.lambda.api.Comprehender;

public class StreamComprehender implements Comprehender<Stream> {
	public Class getTargetClass(){
		return Stream.class;
	}
	@Override
	public Object filter(Stream t, Predicate p) {
		return t.filter(p);
	}

	@Override
	public Object map(Stream t, Function fn) {
		return t.map(fn);
	}
	public Stream executeflatMap(Stream t, Function fn){
		return flatMap(t,input -> unwrapOtherMonadTypes(this,fn.apply(input)));
	}
	@Override
	public Stream flatMap(Stream t, Function fn) {
		return t.flatMap(fn);
	}

	@Override
	public boolean instanceOfT(Object apply) {
		return apply instanceof Stream;
	}
	@Override
	public Stream empty() {
		return Stream.of();
	}
	@Override
	public Stream of(Object o) {
		return Stream.of(o);
	}
	static <T> T unwrapOtherMonadTypes(Comprehender<T> comp,Object apply){
		
		
		
		if(apply instanceof Collection){
			return (T)((Collection)apply).stream();
		}
		if(apply instanceof Iterable){
			 return (T)StreamSupport.stream(((Iterable)apply).spliterator(),
						false);
		}
		
		return Comprehender.unwrapOtherMonadTypes(comp,apply);
		
	}
	

	

}