package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;

public final class TypePathEntry
{
	public static TypePathEntry read( BufferReader bufferReader )
	{
		int pathKind = bufferReader.readUnsignedByte();
		int argumentIndex = bufferReader.readUnsignedByte();
		return new TypePathEntry( pathKind, argumentIndex );
	}

	public final int pathKind;
	public final int argumentIndex;

	private TypePathEntry( int pathKind, int argumentIndex )
	{
		this.pathKind = pathKind;
		this.argumentIndex = argumentIndex;
	}

	@Override public String toString() { return "pathKind = " + pathKind + ", argumentIndex = " + argumentIndex; }

	public void intern( Interner interner )
	{
		/* nothing to do */
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( pathKind );
		constantWriter.writeUnsignedByte( argumentIndex );
	}
}
