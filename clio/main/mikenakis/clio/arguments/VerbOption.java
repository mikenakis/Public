package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Option} which consumes all of the remaining tokens in the command-line.
 *
 * It is optional by definition, and it is intended for using its own instance of {@link Clio} to further parse them.
 *
 * NOTE: this is not really working yet, it just exists as a reminder that it should be implemented at some point.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class VerbOption extends Option<Boolean>
{
	public List<String> parameters = null;
	private final String description;

	public VerbOption( Clio clio, String name, String description )
	{
		super( clio, name, true );
		this.description = description;
	}

	@Override public boolean tryParse( List<String> tokens )
	{
		if( tokens.get( 0 ).equals( name ) )
		{
			parameters = new ArrayList<>( tokens );
			tokens.clear();
			return true;
		}
		return false;
	}

	@Override public String getLongUsage()
	{
		return description;
	}

	@Override public Boolean get()
	{
		return parameters != null;
	}
}
