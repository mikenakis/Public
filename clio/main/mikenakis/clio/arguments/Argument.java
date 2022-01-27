package mikenakis.clio.arguments;

import mikenakis.kit.Try;

import java.util.List;
import java.util.function.Supplier;

/**
 * A command line argument.
 *
 * @author michael.gr
 */
public interface Argument<T> extends Supplier<T>
{
	String name();
	String description();
	Try<Boolean> tryParse( List<String> tokens );
	String getShortUsage();
	String getLongUsage();
	boolean isPositional();
	boolean isOptional();
}
