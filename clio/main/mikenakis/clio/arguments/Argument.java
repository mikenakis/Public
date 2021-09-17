package mikenakis.clio.arguments;

import java.util.List;
import java.util.function.Supplier;

/**
 * A command line argument.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Argument<T> extends Supplier<T>
{
	String name();
	String description();
	boolean tryParse( List<String> tokens );
	String getShortUsage();
	String getLongUsage();
	boolean isPositional();
	boolean isOptional();
}
