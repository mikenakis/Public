package io.github.mikenakis.clio.arguments;

import io.github.mikenakis.kit.Try;

import java.util.List;
import java.util.function.Supplier;

/**
 * A command line argument.
 *
 * @author michael.gr
 */
public abstract class Argument<T> implements Supplier<T>
{
	public abstract String name();
	public abstract String description();
	public abstract Try<Boolean> tryParse( List<String> tokens );
	public abstract String getShortUsage();
	public abstract String getLongUsage();
	public abstract boolean isPositional();
	public abstract boolean isOptional();
	public abstract String debugString();

	@Override public final String toString()
	{
		return debugString() + " = " + get();
	}
}
