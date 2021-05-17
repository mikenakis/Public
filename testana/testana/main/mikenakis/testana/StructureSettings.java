package mikenakis.testana;

import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class StructureSettings
{
	private static class ExcludedDirectoryEntry
	{
		final String originalString;
		boolean used = false;

		ExcludedDirectoryEntry( String originalString )
		{
			this.originalString = originalString;
		}
	}

	private final Map<Path,ExcludedDirectoryEntry> excludedDirectoryEntryFromPathMap = new HashMap<>();

	public StructureSettings()
	{
	}

	public void load( Path projectSourceRootDirectory, Path configurationPathName )
	{
		assert Kit.path.isAbsoluteNormalizedDirectory( projectSourceRootDirectory );
		assert Kit.path.isAbsoluteNormalized( configurationPathName );
		if( configurationPathName.toFile().exists() )
		{
			Kit.uncheckedTryWithResources( () -> Files.newBufferedReader( configurationPathName ), ( BufferedReader reader ) -> //
			{
				for( ; ; )
				{
					String line = reader.readLine();
					if( line == null )
						break;
					int i = line.indexOf( '#' );
					if( i != -1 )
						line = line.substring( 0, i );
					line = line.trim();
					if( line.isEmpty() )
						continue;
					int w = line.indexOf( ' ' );
					if( w == 0 )
						w = line.length();
					String command = line.substring( 0, w );
					String parameters = line.substring( w ).trim();
					switch( command )
					{
						case "exclude-directory":
							if( parameters.charAt( 0 ) == '\"' )
								parameters = Kit.string.unescapeForJava( parameters );
							addExcludedDirectory( projectSourceRootDirectory, parameters );
							break;
						default:
							Log.error( "Unknown command: \"" + command + "\"" );
							break;
					}
				}
			} );
		}
		for( Map.Entry<Path,ExcludedDirectoryEntry> entry : excludedDirectoryEntryFromPathMap.entrySet() )
		{
			Path excludedDirectory = entry.getKey();
			ExcludedDirectoryEntry excludedDirectoryEntry = entry.getValue();
			if( !excludedDirectory.toFile().exists() )
			{
				Log.warning( "Excluded directory does not exist: " + toString( excludedDirectoryEntry.originalString, excludedDirectory ) );
				excludedDirectoryEntry.used = true; //mark it as 'used' so as not to generate 'unused' warning in the end.
			}
		}
	}

	private void addExcludedDirectory( Path projectSourceRootDirectory, String originalString )
	{
		Path absoluteExcludedDirectory = projectSourceRootDirectory.resolve( Paths.get( originalString ) ).toAbsolutePath().normalize();
		if( excludedDirectoryEntryFromPathMap.containsKey( absoluteExcludedDirectory ) )
		{
			Log.error( "Duplicate excluded directory: " + toString( originalString, absoluteExcludedDirectory ) );
			return;
		}
		ExcludedDirectoryEntry excludedDirectoryEntry = new ExcludedDirectoryEntry( originalString );
		Kit.map.add( excludedDirectoryEntryFromPathMap, absoluteExcludedDirectory, excludedDirectoryEntry );
	}

	public boolean shouldExcludeDirectory( Path directory )
	{
		assert Kit.path.isAbsoluteNormalizedDirectory( directory );
		Optional<ExcludedDirectoryEntry> excludedDirectoryEntry = Optional.ofNullable( Kit.map.tryGet( excludedDirectoryEntryFromPathMap, directory ) );
		excludedDirectoryEntry.ifPresent( d -> d.used = true );
		return excludedDirectoryEntry.isPresent();
	}

	public void reportWarnings()
	{
		for( Map.Entry<Path,ExcludedDirectoryEntry> entry : excludedDirectoryEntryFromPathMap.entrySet() )
		{
			Path excludedDirectory = entry.getKey();
			ExcludedDirectoryEntry excludedDirectoryEntry = entry.getValue();
			if( !excludedDirectoryEntry.used )
				Log.warning( "Excluded directory is not reachable: " + toString( excludedDirectoryEntry.originalString, excludedDirectory ) );
		}
	}

	private static String toString( String originalString, Path path )
	{
		return "\"" + originalString + "\" ( -> " + path + ")";
	}
}
