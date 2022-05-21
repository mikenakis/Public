package io.github.mikenakis.kit.functional;

public interface BooleanFunction2<T1,T2>
{
	boolean invoke( T1 argument1, T2 argument2 );
}
