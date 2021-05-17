package mikenakis.clio.arguments;

import mikenakis.clio.Clio;
import mikenakis.kit.Kit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link Option} which accepts no parameters. (Its significance lies in its presence or absence.) It is by definition optional.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SwitchOption extends Option<Boolean>
{
	private boolean given = false;
	private final String description;

	public SwitchOption( Clio clio, String name, String description )
	{
		super( clio, name, true );
		this.description = description;
	}

	@Override public boolean tryParse( List<String> tokens )
	{
		String token = tokens.get( 0 );
		if( !Kit.string.splitAtCharacter( name, '|' ).stream().map( p -> p.trim() ).collect( Collectors.toList() ).contains( token ) )
			return false;
		tokens.remove( 0 );
		given = true;
		return true;
	}

	@Override public String getLongUsage()
	{
		return description;
	}

	@Override public Boolean get()
	{
		return given;
	}
}
