package mikenakis.bytecode.test;

import mikenakis.kit.Kit;

import java.net.URL;
import java.nio.file.Path;

/**
 * Testing helpers.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class Helpers
{
	private Helpers()
	{
	}

	static Path getOutputPath( Class<?> javaClass )
	{
		return getPathToResource( javaClass, javaClass.getSimpleName() + ".class" ).getParent();
	}

	static Path getPathToResource( Class<?> javaClass, String resourceName )
	{
		URL url = javaClass.getResource( resourceName );
		assert url != null : resourceName;
		return Kit.classLoading.getPathFromUrl( url );
	}

	static Path getPathToClassFile( Class<?> javaClass )
	{
		int packageNameLength = javaClass.getPackageName().length();
		String name = javaClass.getName().substring( packageNameLength + 1 ) + ".class";
		return getPathToResource( javaClass, name );
	}
}
