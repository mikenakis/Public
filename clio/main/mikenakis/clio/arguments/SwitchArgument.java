package mikenakis.clio.arguments;

import java.util.List;

/**
 * An {@link BaseArgument} which accepts no parameters. (Its significance lies in its presence or absence.) It is by definition optional.
 *
 * @author michael.gr
 */
public final class SwitchArgument extends BaseArgument<Boolean>
{
	private final List<String> switchNames;
	private boolean given = false;

	public SwitchArgument( List<String> switchNames, String description )
	{
		super( switchNames.get( 0 ), description );
		this.switchNames = switchNames;
	}

	@Override public boolean tryParse( List<String> tokens )
	{
		String token = tokens.get( 0 );
		if( !switchNames.contains( token ) )
			return false;
		tokens.remove( 0 );
		given = true;
		return true;
	}

	@Override public String getShortUsage()
	{
		return switchNames.get( 0 );
	}

	@Override public String getLongUsage()
	{
		return description();
	}

	@Override public boolean isPositional()
	{
		return false;
	}

	@Override public boolean isOptional()
	{
		return true;
	}

	@Override public Boolean get()
	{
		return given;
	}
}
