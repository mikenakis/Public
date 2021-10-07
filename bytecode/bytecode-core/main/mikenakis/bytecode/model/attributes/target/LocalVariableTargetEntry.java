package mikenakis.bytecode.model.attributes.target;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class LocalVariableTargetEntry
{
	public static LocalVariableTargetEntry read( BufferReader bufferReader )
	{
		int startPc = bufferReader.readUnsignedShort();
		int length = bufferReader.readUnsignedShort();
		int index = bufferReader.readUnsignedShort();
		return new LocalVariableTargetEntry( startPc, length, index );
	}

	public final int startPc; //TODO: this needs to be replaced with an instruction.
	public final int length;
	public final int index;

	private LocalVariableTargetEntry( int startPc, int length, int index )
	{
		this.startPc = startPc;
		this.length = length;
		this.index = index;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "startPc = " + startPc + ", length = " + length + ", index = " + index; }

	public void intern( Interner interner )
	{
		//TODO
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedShort( startPc ); //TODO
		constantWriter.writeUnsignedShort( length );
		constantWriter.writeUnsignedShort( index );
	}
}
