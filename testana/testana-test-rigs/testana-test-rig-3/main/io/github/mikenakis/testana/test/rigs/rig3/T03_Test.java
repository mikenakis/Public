package io.github.mikenakis.testana.test.rigs.rig3;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T03_Test
{
	public static String deltaShortName = "delta()";
	public static String deltaName = T03_Test.class.getSimpleName() + "." + deltaShortName;
	@Test public void delta()
	{
		Path path = getWorkingDirectory();
		String sourceFile = "main/" + T03_Test.class.getName().replace( ".", "/" ) + ".java";
		assert path.resolve( sourceFile ).toFile().exists(); //Current directory is not ${project.basedir}
	}

	private static Path getWorkingDirectory()
	{
		return Paths.get( System.getProperty( "user.dir" ) ).toAbsolutePath().normalize();
	}
}
