package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

/**
 * A positional {@link Option} which can be any string.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringPositionalOption extends PositionalOption<String>
{
	public StringPositionalOption( Clio clio, String name, String defaultValue, String description )
	{
		super( clio, name, defaultValue, description );
	}

	@Override public String get()
	{
		return stringValue == null ? defaultValue : stringValue;
	}
}
