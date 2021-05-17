package mikenakis.clio.arguments;

import mikenakis.clio.Clio;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A positional {@link Option} which is a {@link Path}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class PathPositionalOption extends PositionalOption<Path>
{
	public PathPositionalOption( Clio clio, String name, String defaultValue, String description )
	{
		super( clio, name, defaultValue, description );
	}

	@Override public Path get()
	{
		String value = stringValue == null ? defaultValue : stringValue;
		return Paths.get( value ).toAbsolutePath().normalize();
	}
}
