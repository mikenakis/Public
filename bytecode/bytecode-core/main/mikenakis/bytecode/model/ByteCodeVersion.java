package mikenakis.bytecode.model;

import mikenakis.bytecode.kit.BufferReader;

public record ByteCodeVersion( int major, int minor )
{
	public static ByteCodeVersion read( BufferReader bufferReader )
	{
		int minor = bufferReader.readUnsignedShort();
		int major = bufferReader.readUnsignedShort();
		return new ByteCodeVersion( major, minor );
	}
}
