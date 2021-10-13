package mikenakis.tyraki;

/**
 * A total converter. (The conversion is defined for all input values, so {@code null} will never be returned.)
 *
 * @author michael.gr
 */
public interface TotalConverter<T, E>
{
	T invoke( E e );
}
