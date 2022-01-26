package mikenakis.tyraki;

/**
 * A total converter. (The conversion must be defined for all input values)
 *
 * @author michael.gr
 */
public interface TotalConverter<T, E>
{
	T invoke( E e );
}
