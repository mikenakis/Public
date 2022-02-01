package mikenakis.clio.arguments;

/**
 * A command line argument.
 *
 * @author michael.gr
 */
public abstract class BaseArgument<T> extends Argument<T>
{
	private final String name; //the argument-name, for displaying usage information. For example, "help-switch" or "output-option".
	private final String description;

	protected BaseArgument( String name, String description )
	{
		this.name = name;
		this.description = description;
	}

	@Override public final String name() { return name; }
	@Override public final String description() { return description; }
	@Override public final String debugString() { return getClass().getSimpleName() + " \"" + name + "\""; }
}
