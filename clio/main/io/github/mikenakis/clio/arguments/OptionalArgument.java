package io.github.mikenakis.clio.arguments;

import io.github.mikenakis.kit.Try;

import java.util.List;
import java.util.Optional;

/**
 * An optional {@link Argument}.
 *
 * @author michael.gr
 */
public final class OptionalArgument<T> extends Argument<Optional<T>>
{
	private final Argument<T> delegee;
	private boolean parsed;

	public OptionalArgument( Argument<T> delegee )
	{
		assert !delegee.isOptional();
		this.delegee = delegee;
	}

	@Override public String name() { return delegee.name(); }
	@Override public String description() { return delegee.description(); }
	@Override public String getShortUsage() { return "[" + delegee.getShortUsage() + "]"; }
	@Override public String getLongUsage() { return delegee.getLongUsage() + " (optional)"; }
	@Override public boolean isPositional() { return delegee.isPositional(); }
	@Override public boolean isOptional() { return true; }
	@Override public String debugString() { return getClass().getSimpleName() + "(" + delegee.debugString() + ")"; }

	@Override public Try<Boolean> tryParse( List<String> tokens )
	{
		assert !parsed;
		Try<Boolean> result = delegee.tryParse( tokens );
		if( result.isSuccess() && result.get() )
			parsed = true;
		return result;
	}

	@Override public Optional<T> get()
	{
		if( !parsed )
			return Optional.empty();
		return Optional.of( delegee.get() );
	}
}
