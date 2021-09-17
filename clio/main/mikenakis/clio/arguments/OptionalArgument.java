package mikenakis.clio.arguments;

import java.util.List;
import java.util.Optional;

/**
 * An optional {@link Argument}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class OptionalArgument<T> implements Argument<Optional<T>>
{
	private final Argument<T> delegee;
	private boolean parsed;

	public OptionalArgument( Argument<T> delegee )
	{
		assert !delegee.isOptional();
		this.delegee = delegee;
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
		return delegee.getLongUsage() + " (optional)";
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
		return "Optional(" + delegee.toString() + ")";
	}

	@Override public Optional<T> get()
	{
		if( !parsed)
			return Optional.empty();
		return Optional.of( delegee.get() );
	}
}
