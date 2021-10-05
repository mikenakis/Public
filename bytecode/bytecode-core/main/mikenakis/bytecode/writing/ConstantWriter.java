package mikenakis.bytecode.writing;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.Kit;

public class ConstantWriter
{
	public final BufferWriter bufferWriter;
	final ConstantPool constantPool;
	final BootstrapPool bootstrapPool;

	ConstantWriter( BufferWriter bufferWriter, ConstantPool constantPool, BootstrapPool bootstrapPool )
	{
		this.bufferWriter = bufferWriter;
		this.constantPool = constantPool;
		this.bootstrapPool = bootstrapPool;
	}

	public void writeUnsignedByte( int value ) { bufferWriter.writeUnsignedByte( value ); }
	public void writeUnsignedShort( int value ) { bufferWriter.writeUnsignedShort( value ); }
	public void writeInt( int value ) { bufferWriter.writeInt( value ); }
	public void writeBuffer( Buffer value ) { bufferWriter.writeBuffer( value ); }
	public void writeBytes( byte[] value ) { bufferWriter.writeBytes( value ); }
	public void writeFloat( float value ) { bufferWriter.writeFloat( value ); }
	public void writeLong( long value ) { bufferWriter.writeLong( value ); }
	public void writeDouble( double value ) { bufferWriter.writeDouble( value ); }
	public int getConstantIndex( Constant constant ) { return constantPool.getIndex( constant ); }
	public int getBootstrapIndex( BootstrapMethod bootstrapMethod ) { return bootstrapPool.getIndex( bootstrapMethod ); }

	public CodeConstantWriter asCodeConstantWriter() { return Kit.fail(); }
}
