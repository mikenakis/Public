package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class SupertypeTarget extends Target // "supertype_target" in jvms-4.7.20.1
{
	public static SupertypeTarget read( BufferReader bufferReader, int targetTag )
	{
		int supertypeIndex = bufferReader.readUnsignedShort();
		//TODO: use the supertypeIndex to look up the implemented interface (or supertype) and realize that type instead of the index.
		return new SupertypeTarget( targetTag, supertypeIndex );
	}

	public final int supertypeIndex;

	private SupertypeTarget( int tag, int supertypeIndex )
	{
		super( tag );
		assert tag == tag_Supertype;
		this.supertypeIndex = supertypeIndex;
	}

	@Deprecated @Override public SupertypeTarget asSupertypeTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "superTypeIndex = " + supertypeIndex; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( supertypeIndex );
	}
}
