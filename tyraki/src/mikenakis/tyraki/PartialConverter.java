package mikenakis.tyraki;

import java.util.Optional;

/**
 * A partial converter. (The conversion may not be defined for some input values, in which case {@link Optional#empty} will be returned.)
 *
 * @author michael.gr
 */
public interface PartialConverter<T, E>
{
	Optional<T> convert( E e );
}
