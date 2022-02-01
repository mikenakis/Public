package mikenakis.clio.arguments;

import mikenakis.kit.Try;

import java.util.List;
import java.util.Optional;

/**
 * An {@link Argument} which adds a default to an {@link OptionalArgument}.
 *
 * @author michael.gr
 */
public final class DefaultingArgument<T> extends Argument<T>
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

	@Override public Try<Boolean> tryParse( List<String> tokens )
	{
		assert !parsed;
		Try<Boolean> result = delegee.tryParse( tokens );
		if( result.isSuccess() && result.get() )
			parsed = true;
		return result;
	}

	@Override public String getShortUsage() { return "[" + delegee.getShortUsage() + "]"; }
	@Override public String getLongUsage() { return delegee.getLongUsage() + " (optional; default = " + defaultValue + ")"; }
	@Override public boolean isPositional() { return delegee.isPositional(); }
	@Override public boolean isOptional() { return true; }
	@Override public String debugString() { return getClass().getSimpleName() + "(" + delegee.debugString() + ")"; }
	@Override public T get() { return delegee.get().orElse( defaultValue ); }
}
