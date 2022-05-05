package mikenakis.testana.test.rigs.classes_under_test;

import mikenakis.kit.Kit;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Represents a dependency of a class which is to be tested.
 *
 * @author michael.gr
 */
public class Alice
{
	private final String suffix;

	public Alice( String suffix )
	{
		this.suffix = suffix;
	}

	public String doYourThing( String s )
	{
		return s + suffix;
	}

	static
	{
		production_resource_file_is_accessible();
		production_resource_directory_is_not_mixed_with_testana_resource_directory();
	}


	private static void production_resource_file_is_accessible()
	{
		URL url = Alice.class.getResource( "/sample_resource_file.txt" );
		assert url != null;
		Path path = Kit.classLoading.getPathFromUrl( url );
		List<String> lines = Kit.unchecked( () -> Files.readAllLines(path) );
		assert lines.size() == 1;
		assert lines.get( 0 ).equals( "sample resource content" );
	}

	private static void production_resource_directory_is_not_mixed_with_testana_resource_directory()
	{
		Class<?> myClass = Alice.class;
		URL url = myClass.getClassLoader().getResource( "" );
		assert url != null;
		assert url.equals( myClass.getResource( "/" ) );
		assert url.equals( myClass.getResource( "/." ) );
		Path path = Kit.classLoading.getPathFromUrl( url );
		assert path.toString().contains( "testana-console" ); //FIXME XXX TODO currently contains 'testana-console'; it should not.
	}
}
