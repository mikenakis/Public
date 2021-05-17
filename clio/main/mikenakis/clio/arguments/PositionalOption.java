package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

import java.util.List;

/**
 * A positional {@link Option}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class PositionalOption<T> extends Option<T>
{
	protected final String defaultValue;
	private final String description;
	protected String stringValue = null;

	protected PositionalOption( Clio clio, String name, String defaultValue, String description )
	{
		super( clio, name, defaultValue != null );
		this.defaultValue = defaultValue;
		this.description = description;
	}

	@Override public boolean tryParse( List<String> tokens )
	{
		stringValue = tokens.remove( 0 );
		return true;
	}

	@Override public String getShortUsage()
	{
		return "<" + name + ">";
	}

	@Override public String getLongUsage()
	{
		var builder = new StringBuilder();
		builder.append( description );
		if( defaultValue != null )
			builder.append( " (default is " ).append( defaultValue ).append( ")" );
		return builder.toString();
	}
}
