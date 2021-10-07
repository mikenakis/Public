package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;

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

	@SuppressWarnings( "MethodMayBeStatic" ) public void intern( Interner interner )
	{
		Kit.get( interner ); /* nothing to do */
	}

	public void write( BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( pathKind );
		bufferWriter.writeUnsignedByte( argumentIndex );
	}
}
