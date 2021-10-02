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
	}

	public Class<?> load( ByteCodeType byteCodeType )
	{
		String name = byteCodeType.typeDescriptor().typeName();
		byte[] bytes = ByteCodeWriter.write( byteCodeType );
		Kit.map.add( classes, name, bytes );
		return Kit.unchecked( () -> loadClass( name ) );
	}

	@Override protected Class<?> findClass( String name ) throws ClassNotFoundException
	{
		byte[] code = classes.get( name );
		if( code != null )
			return defineClass( name, code, 0, code.length );
		return super.findClass( name ); // will throw ClassNotFoundException
	}
}
