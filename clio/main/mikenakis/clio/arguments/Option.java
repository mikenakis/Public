package mikenakis.clio.arguments;

import mikenakis.clio.Clio;
import mikenakis.clio.exceptions.ClioException;
import mikenakis.kit.Kit;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A command line argument.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Option<T> implements Supplier<T>
{
	final Clio clio;
	public final boolean optional;
	public final String name;

	protected Option( Clio clio, String name, boolean optional )
	{
		assert nameIsOkAssertion( name );
		this.clio = clio;
		this.optional = optional;
		this.name = name;
	}

	public abstract boolean tryParse( List<String> tokens ) throws ClioException;

	@Override public String toString()
	{
		var builder = new StringBuilder();
		builder.append( getClass().getSimpleName() ).append( ' ' ).append( name );
		builder.append( " (" ).append( optional ? "optional" : "required" ).append( ")" );
		return builder.toString();
	}

	public String getShortUsage()
	{
		return name;
	}

	public abstract String getLongUsage();

	private static boolean nameIsOkAssertion( String name )
	{
		List<String> parts = Kit.string.splitAtCharacter( name, '|' ).stream().map( s -> s.trim() ).collect( Collectors.toList() );
		for( String part : parts )
		{
			if( part.startsWith( "--" ) )
				assert matches( part, "--[a-z][a-zA-Z0-9_-]+" ) : part;
			else if( part.startsWith( "-" ) )
				assert matches( part, "-[a-z]+" ) : part;
			else
				assert matches( part, "[a-z][a-zA-Z0-9_-]+" ) : part;
		}
		return true;
	}

	private static boolean matches( String part, String pattern )
	{
		return part.matches( pattern );
	}
}
