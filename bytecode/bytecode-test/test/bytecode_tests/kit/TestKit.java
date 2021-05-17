package bytecode_tests.kit;

import mikenakis.kit.Kit;

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

	public static List<Path> collectResourcePaths( Path rootPath, String suffix )
	{
		List<Path> result = new ArrayList<>();
		Kit.unchecked( () -> Files.walkFileTree( rootPath, new SimpleFileVisitor<>()
		{
			@Override public FileVisitResult visitFile( Path path1, BasicFileAttributes attrs )
			{
				add( path1 );
				return FileVisitResult.CONTINUE;
			}

			private void add( Path path1 )
			{
				if( !path1.toString().endsWith( suffix ) )
					return;
				result.add( path1 );
			}
		} ) );
		return result;
	}
}
