package mikenakis.testana.test.rigs.rig3;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import org.junit.Test;

import java.nio.file.Path;

/**
 * A class which performs a test.  This is meant to be discovered and run by the testana test.
 *
 * @author michael.gr
 */
public class T03_Test
{
	public static String testMethodName = T03_Test.class.getSimpleName() + ".test()";

	@Test public void test()
	{
		Path path = Kit.path.getWorkingDirectory();
		String sourceFile = "main/" + T03_Test.class.getName().replace( ".", "/" ) + ".java";
		assert path.resolve( sourceFile ).toFile().exists(); //Current directory is not ${project.basedir}
	}
}
