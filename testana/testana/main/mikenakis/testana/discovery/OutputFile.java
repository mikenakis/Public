package mikenakis.testana.discovery;

import mikenakis.kit.Kit;

import java.time.Instant;

public class OutputFile
{
	private static final String classExtension = ".class";

	public enum Type
	{
		Resource,
		Class
	}

	public final Type type;
	public final String relativePath;
	public final Instant lastModifiedTime;
	private String className;

	public OutputFile( Type type, String relativePath, Instant lastModifiedTime )
	{
		assert (type == Type.Class) == relativePath.endsWith( classExtension );
		this.type = type;
		this.relativePath = relativePath;
		this.lastModifiedTime = lastModifiedTime;
	}

	@Override public String toString()
	{
		return type.toString() + " " + relativePath + " last modified " + Kit.time.localTimeString( lastModifiedTime );
	}

	public String className()
	{
		assert type == Type.Class;
		if( className == null )
			className = relativePath.substring( 0, relativePath.length() - classExtension.length() ).replace( '/', '.' );
		return className;
	}
}
