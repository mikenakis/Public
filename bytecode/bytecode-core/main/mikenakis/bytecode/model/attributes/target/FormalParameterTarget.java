package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class FormalParameterTarget extends Target // "formal_parameter_target" in jvms-4.7.20.1
{
	public static FormalParameterTarget read( BufferReader bufferReader, int targetTag )
	{
		int formalParameterIndex = bufferReader.readUnsignedByte();
		//TO-maybe-DO: lookup the formal parameter and realize a reference to it instead of realizing this index.
		return new FormalParameterTarget( targetTag, formalParameterIndex );
	}

	public final int formalParameterIndex;

	private FormalParameterTarget( int tag, int formalParameterIndex )
	{
		super( tag );
		assert tag == tag_FormalParameter;
		this.formalParameterIndex = formalParameterIndex;
	}

	@Deprecated @Override public FormalParameterTarget asFormalParameterTarget() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "formalParameterIndex = " + formalParameterIndex; }

	@Override public void intern( Interner interner )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( formalParameterIndex );
	}
}