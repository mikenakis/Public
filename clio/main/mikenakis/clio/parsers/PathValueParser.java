package mikenakis.clio.parsers;

import mikenakis.kit.Kit;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathValueParser extends ValueParser<Path>
{
	public static ValueParser<Path> instance = new PathValueParser();

	private PathValueParser()
	{
	}

	@Override public boolean isValid( String s )
	{
		try
		{
			Kit.get( Paths.get( s ).toAbsolutePath().normalize() );
			return true;
		}
		catch( Exception ignore )
		{
			return false;
		}
	}

	@Override public Path valueFromString( String s )
	{
		return Paths.get( s ).toAbsolutePath().normalize();
	}

	@Override public String stringFromValue( Path value )
	{
		return value.toString();
	}
}
