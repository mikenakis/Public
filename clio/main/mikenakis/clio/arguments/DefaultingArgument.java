package mikenakis.clio.arguments;

import java.util.List;
import java.util.Optional;

/**
 * An {@link Argument} which adds a default to an {@link OptionalArgument}.
 *
 * @author michael.gr
 */
public final class DefaultingArgument<T> implements Argument<T>
{
	private final Argument<Optional<T>> delegee;
	private boolean parsed;
	private final T defaultValue;

	public DefaultingArgument( Argument<Optional<T>> delegee, T defaultValue )
	{
		assert delegee.isOptional();
		this.delegee = delegee;
		this.defaultValue = defaultValue;
	}

	@Override public String name()
	{
		return delegee.name();
	}

	@Override public String description()
	{
		return delegee.description();
	}

	@Override public boolean tryParse( List<String> tokens )
	{
		assert !parsed;
		parsed = delegee.tryParse( tokens );
		return parsed;
	}

	@Override public String getShortUsage()
	{
		return "[" + delegee.getShortUsage() + "]";
	}

	@Override public String getLongUsage()
	{
		return delegee.getLongUsage() + " (optional; default = " + defaultValue + ")";
	}

	@Override public boolean isPositional()
	{
		return delegee.isPositional();
	}

	@Override public boolean isOptional()
	{
		return true;
	}

	@Override public String toString()
	{
		return "OptionalWithDefault(" + delegee.toString() + ")";
	}

	@Override public T get()
	{
		return delegee.get().orElse( defaultValue );
	}
}
