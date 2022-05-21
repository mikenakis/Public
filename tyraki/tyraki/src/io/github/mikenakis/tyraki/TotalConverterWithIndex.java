package io.github.mikenakis.tyraki;

/**
 * A total converter. (The conversion is defined for all input values.)
 *
 * @author michael.gr
 */
public interface TotalConverterWithIndex<T, E>
{
	T invoke( int index, E e );
}
