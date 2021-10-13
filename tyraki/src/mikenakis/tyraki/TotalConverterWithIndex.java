package mikenakis.tyraki;

/**
 * A total converter. (The conversion is defined for all input values, so {@code null} will never be returned.)
 *
 * @author michael.gr
 */
public interface TotalConverterWithIndex<T, E>
{
	T invoke( int index, E e );
}
