package mikenakis.services.bytecode;

import mikenakis.bytecode.ByteCodeType;
import mikenakis.kit.Kit;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ByteCode Service.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodeService
{
	public static final ByteCodeService instance = new ByteCodeService();

	private static final class Entry
	{
		final ByteCodeType byteCodeType;
		final Optional<ClassLoader> classLoader;

		Entry( ByteCodeType byteCodeType, Optional<ClassLoader> classLoader )
		{
			this.byteCodeType = byteCodeType;
			this.classLoader = classLoader;
		}
	}

	private final Map<String,Entry> entriesByTypeName = new HashMap<>();
	private int hits;
	private int misses;

	private ByteCodeService()
	{
	}

	public String getStatsString()
	{
		return "Hits:" + hits + " Misses:" + misses;
	}

	public Optional<ByteCodeType> tryGetByteCodeTypeByName( String typeName, ClassLoader classLoader )
	{
		return tryGetOrCreateByteCodeType( Optional.of( classLoader ), typeName, Optional.empty() );
	}

	private Optional<ClassLoader> getLoadedClassLoader( String typeName )
	{
		Optional<Entry> entry = Optional.ofNullable( Kit.map.tryGet( entriesByTypeName, typeName ) );
		if( entry.isEmpty() )
		{
			misses++;
			return Optional.empty();
		}
		hits++;
		return entry.get().classLoader;
	}

	public Optional<ByteCodeType> tryGetOrCreateByteCodeType( Optional<ClassLoader> classLoader, String typeName, Optional<byte[]> bytes )
	{
		return Optional.ofNullable( Kit.map.tryGet( entriesByTypeName, typeName ) ).map( e -> e.byteCodeType ).or( () ->
		{
			Optional<Path> path = classLoader.flatMap( c -> tryGetPathFromClassLoaderAndTypeName( c, typeName ) );
			return tryCreateByteCodeType( bytes, path ).map( byteCodeType ->
			{
				assert byteCodeType.getName().equals( typeName );
				Kit.map.add( entriesByTypeName, typeName, new Entry( byteCodeType, classLoader ) );
				return byteCodeType;
			} );
		} );
	}

	private static Optional<ByteCodeType> tryCreateByteCodeType( Optional<byte[]> bytes, Optional<Path> path )
	{
		if( bytes.isEmpty() )
		{
			if( path.isEmpty() )
				return Optional.empty();
			return Optional.of( ByteCodeType.create( path.get() ) );
		}
		return Optional.of( ByteCodeType.create( bytes.get(), path ) );
	}

	private static Optional<Path> tryGetPathFromClassLoaderAndTypeName( ClassLoader classLoader, String typeName )
	{
		URL url = classLoader.getResource( typeName.replace( '.', '/' ) + ".class" );
		if( url == null )
			return Optional.empty();
		return Optional.of( getPathFromUrl( url ) );
	}

	public static Path getPathFromUrl( URL url )
	{
		URI uri = Kit.unchecked( url::toURI );
		String scheme = uri.getScheme();
		if( scheme.equals( "jar" ) )
		{
			FileSystem fileSystem = getOrCreateFileSystem( uri );
			String jarPath = uri.getSchemeSpecificPart();
			int i = jarPath.indexOf( '!' );
			assert i >= 0;
			String subPath = jarPath.substring( i + 1 );
			return fileSystem.getPath( subPath );
		}
		if( scheme.equals( "jrt" ) )
		{
			FileSystem fileSystem = FileSystems.getFileSystem( URI.create( "jrt:/" ) );
			return fileSystem.getPath( "modules", uri.getPath() );
		}
		return Paths.get( uri );
	}

	private static FileSystem getOrCreateFileSystem( URI uri )
	{
		Optional<FileSystem> fileSystem = tryGetFileSystem( uri );
		return fileSystem.orElseGet( () -> newFileSystem( uri ) );
	}

	private static FileSystem newFileSystem( URI uri )
	{
		return Kit.unchecked( () -> FileSystems.newFileSystem( uri, Collections.emptyMap() ) );
	}

	private static Optional<FileSystem> tryGetFileSystem( URI uri )
	{
		try
		{
			/**
			 * NOTE: if you see this failing with a {@link FileSystemNotFoundException}
			 * it is probably because you have an exception breakpoint;
			 * just let it run, it will probably recover.
			 */
			FileSystem fileSystem = FileSystems.getFileSystem( uri );
			return Optional.of( fileSystem );
		}
		catch( FileSystemNotFoundException ignore )
		{
			return Optional.empty();
		}
	}

	public ClassLoader newInterceptingByteCodeClassLoader( Collection<Path> paths )
	{
		ClassLoader urlClassLoader = createUrlClassLoader( getClass().getClassLoader(), paths );
		//noinspection ClassLoaderInstantiation
		return new ByteCodeClassLoader( urlClassLoader );
	}

	private static ClassLoader createUrlClassLoader( ClassLoader parentClassLoader, Collection<Path> paths )
	{
		URL[] urls = paths.stream().map( ByteCodeService::urlFromPath ).toArray( URL[]::new );
		//noinspection ClassLoaderInstantiation
		return new URLClassLoader( urls, parentClassLoader );
	}

	private static URL urlFromPath( Path path )
	{
		return Kit.unchecked( () -> path.toUri().toURL() );
	}

	@SuppressWarnings( "CustomClassloader" )
	private class ByteCodeClassLoader extends ClassLoader
	{
		ByteCodeClassLoader( ClassLoader parent )
		{
			super( parent );
		}

		@Override public Class<?> loadClass( String typeName ) throws ClassNotFoundException
		{
			Optional<ClassLoader> otherClassLoader = getLoadedClassLoader( typeName );
			if( otherClassLoader.isPresent() && otherClassLoader.get() != this )
			{
				try
				{
					return otherClassLoader.get().loadClass( typeName );
				}
				catch( ClassNotFoundException e )
				{
					assert false;
				}
			}

			/* class is not already loaded and we don't have the bytes, so delegate to super. */
			return super.loadClass( typeName );
		}
	}
}
