package mikenakis.bytecode.test.kit;

import mikenakis.kit.Kit;

import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Indispensable utility methods.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TestKit
{
	private TestKit()
	{
	}

	public static List<Path> collectResourcePaths( Path rootPath, boolean recursive, String suffix )
	{
		List<Path> result = new ArrayList<>();
		Kit.unchecked( () -> Files.walkFileTree( rootPath, new SimpleFileVisitor<>()
		{
			@Override public FileVisitResult visitFile( Path filePath, BasicFileAttributes attrs )
			{
				if( filePath.toString().endsWith( suffix ) )
					result.add( filePath );
				return FileVisitResult.CONTINUE;
			}

			@Override public FileVisitResult preVisitDirectory( Path directoryPath, BasicFileAttributes attrs )
			{
				return directoryPath.equals( rootPath ) || recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
			}
		} ) );
		return result;
	}

	private static Path getPathToResource( Class<?> javaClass, String resourceName )
	{
		URL url = javaClass.getResource( resourceName );
		assert url != null : resourceName;
		return Kit.classLoading.getPathFromUrl( url );
	}

	public static Path getPathToClassFile( Class<?> javaClass )
	{
		int packageNameLength = javaClass.getPackageName().length();
		String name = javaClass.getName().substring( packageNameLength + 1 ) + ".class";
		return getPathToResource( javaClass, name );
	}
}
