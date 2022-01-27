package mikenakis.clio.arguments;

import mikenakis.clio.Clio;
import mikenakis.kit.Try;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link BaseArgument} which consumes all of the remaining tokens in the command-line.
 *
 * TODO: this is yet to be implemented. The plan is to make it contain a nested instance of Clio.
 *
 * It is optional by definition, and it is intended for using its own instance of {@link Clio} to further parse them.
 *
 * NOTE: this is not really working yet, it just exists as a reminder that it should be implemented at some point.
 *
 * @author michael.gr
 */
public final class VerbArgument extends BaseArgument<Boolean>
{
	private List<String> parameters = null;

	public VerbArgument( String name, String description )
	{
		super( name, description );
	}

	@Override public Try<Boolean> tryParse( List<String> tokens )
	{
		if( tokens.get( 0 ).equals( name() ) )
		{
			parameters = new ArrayList<>( tokens );
			tokens.clear();
			return Try.success( true );
		}
		return Try.success( false );
	}

	@Override public String getShortUsage()
	{
		return name();
	}

	@Override public String getLongUsage()
	{
		return description();
	}

	@Override public boolean isOptional()
	{
		return false;
	}

	@Override public boolean isPositional()
	{
		return true;
	}

	@Override public Boolean get()
	{
		return parameters != null;
	}
}
