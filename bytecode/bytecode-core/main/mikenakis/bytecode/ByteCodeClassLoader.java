package mikenakis.bytecode;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;

public class ByteCodeClassLoader extends ClassLoader
{
	private final Map<String,byte[]> classes = new HashMap<>();

	public ByteCodeClassLoader( ClassLoader parentClassLoader )
	{
		super( parentClassLoader );
	}

	public <T> Class<T> load( ByteCodeType byteCodeType )
	{
		String name = byteCodeType.typeDescriptor().typeName;
		byte[] bytes = byteCodeType.write();
		Kit.map.addOrReplace( classes, name, bytes ); //TODO: try exercising replacing, see if defineClass() can redefine a class
		Class<?> javaClass = Kit.unchecked( () -> loadClass( name ) );
		@SuppressWarnings( "unchecked" ) Class<T> result = (Class<T>)javaClass;
		return result;
	}

	@Override protected Class<?> findClass( String name ) throws ClassNotFoundException
	{
		byte[] code = classes.get( name );
		if( code != null )
			return defineClass( name, code, 0, code.length );
		return super.findClass( name ); // will throw ClassNotFoundException
	}
}
