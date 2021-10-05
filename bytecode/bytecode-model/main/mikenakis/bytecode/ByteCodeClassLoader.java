package mikenakis.bytecode;

import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.writing.ByteCodeWriter;
import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;

public class ByteCodeClassLoader extends ClassLoader
{
	private final Map<String,byte[]> classes = new HashMap<>();

	public ByteCodeClassLoader()
	{
		super( ByteCodeClassLoader.class.getClassLoader() );
	}

	public <T> Class<T> load( ByteCodeType byteCodeType )
	{
		String name = byteCodeType.typeDescriptor().typeName;
		byte[] bytes = ByteCodeWriter.write( byteCodeType );
		Kit.map.add( classes, name, bytes ); //if this fails, the bytecode has already been created.
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
