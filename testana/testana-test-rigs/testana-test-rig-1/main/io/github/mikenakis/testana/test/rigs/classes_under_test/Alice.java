package io.github.mikenakis.testana.test.rigs.classes_under_test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		try
		{
			production_resource_file_is_accessible();
			production_resource_directory_is_not_mixed_with_testana_resource_directory();
		}
		catch( Exception e )
		{
			throw new RuntimeException( e );
		}
	}
	private static void production_resource_file_is_accessible() throws Exception
	{
		URL url = Alice.class.getResource( "/sample_resource_file.txt" );
		assert url != null;
		Path path = getPathFromUrl( url );
		List<String> lines = Files.readAllLines( path );
		assert lines.size() == 1;
		assert lines.get( 0 ).equals( "sample resource content" );
	}

	private static void production_resource_directory_is_not_mixed_with_testana_resource_directory() throws Exception
	{
		Class<?> myClass = Alice.class;
		URL url = myClass.getClassLoader().getResource( "" );
		assert url != null;
		assert url.equals( myClass.getResource( "/" ) );
		assert url.equals( myClass.getResource( "/." ) );
		Path path = getPathFromUrl( url );
		assert !path.toString().contains( "testana-console" ); //Note: this used to fail, then it was fixed, but we keep it as a regression test.
	}

	private static Path getPathFromUrl( URL url ) throws Exception
	{
		return Paths.get( url.toURI() );
	}
}
