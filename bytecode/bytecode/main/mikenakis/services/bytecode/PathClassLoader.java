package mikenakis.services.bytecode;

import mikenakis.kit.Kit;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

//TODO DELETE
public class PathClassLoader extends ClassLoader
{
	private final List<Path> paths;

	public PathClassLoader( Path[] urls, ClassLoader parent )
	{
		super( parent );
		paths = List.of( urls );
	}

	public PathClassLoader( Path[] urls )
	{
		paths = List.of( urls );
	}

	@Override protected Class<?> findClass( String name ) throws ClassNotFoundException
	{
		String relativePath = relativePathFromClassName( name );
		Path path = findResourcePathByRelativePath( relativePath );
		if( path == null )
			throw new ClassNotFoundException( name );
		byte[] bytes;
		try
		{
			bytes = Files.readAllBytes( path );
		}
		catch( IOException exception )
		{
			throw new ClassNotFoundException( name, exception );
		}
		return defineClass( name, bytes, 0, bytes.length );
	}

	@Override public URL findResource( String name )
	{
		Path path = findResourcePathByRelativePath( name );
		if( path == null )
			return null;
		return Kit.unchecked( () -> path.toUri().toURL() );
	}

	@Override public Enumeration<URL> findResources( String name ) throws IOException
	{
		assert false; //does this ever get invoked?
		List<URL> urls = new ArrayList<>();
		for( Path path : paths )
		{
			Path resolvedPath = path.resolve( name );
			if( resolvedPath.toFile().exists() )
				urls.add( resolvedPath.toUri().toURL() );
		}
		return new IteratorEnumeration<>( urls.iterator() );
	}

	private Path findResourcePathByRelativePath( String relativePath )
	{
		for( Path path : paths )
		{
			Path resolvedPath = path.resolve( relativePath );
			if( resolvedPath.toFile().exists() )
				return resolvedPath;
		}
		return null;
	}

	private static String relativePathFromClassName( String name )
	{
		return name.replace( '.', '/' ) + ".class";
	}
}
