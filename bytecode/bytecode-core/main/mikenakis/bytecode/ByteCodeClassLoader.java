package mikenakis.bytecode;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.Kit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class ByteCodeClassLoader extends ClassLoader
{
	private static final Map<ClassLoader,ByteCodeClassLoader> byteCodeClassLoadersByParent = new IdentityHashMap<>();

	public static <T> Class<T> load( ClassLoader parentClassLoader, ByteCodeType byteCodeType )
	{
		ByteCodeClassLoader byteCodeClassLoader;
		synchronized( byteCodeClassLoadersByParent )
		{
			byteCodeClassLoader = byteCodeClassLoadersByParent.computeIfAbsent( parentClassLoader, c -> new ByteCodeClassLoader( c ) );
		}
		return byteCodeClassLoader.load0( byteCodeType );
	}

	private final Map<String,byte[]> byteArraysByTypeName = new HashMap<>();

	private ByteCodeClassLoader( ClassLoader parentClassLoader )
	{
		super( parentClassLoader );
		//Kit.classLoading.troubleshoot( parentClassLoader ).forEach( s -> Log.debug( s.toString() ) );
	}

	private <T> Class<T> load0( ByteCodeType byteCodeType )
	{
		String name = byteCodeType.typeDescriptor().typeName;
		byte[] bytes = Kit.map.tryGet( byteArraysByTypeName, name );
		assert bytes == null || Arrays.equals( bytes, byteCodeType.write() );
		if( bytes == null )
			Kit.map.add( byteArraysByTypeName, name, byteCodeType.write() ); //TODO: try exercising replacing, see if defineClass() can redefine a class
		Class<?> javaClass = Kit.unchecked( () -> loadClass( name ) );
		@SuppressWarnings( "unchecked" ) Class<T> result = (Class<T>)javaClass;
		return result;
	}

	@Override protected Class<?> findClass( String name ) throws ClassNotFoundException
	{
		byte[] code = byteArraysByTypeName.get( name );
		if( code != null )
			return defineClass( name, code, 0, code.length );
		return super.findClass( name ); // will throw ClassNotFoundException
	}
}
