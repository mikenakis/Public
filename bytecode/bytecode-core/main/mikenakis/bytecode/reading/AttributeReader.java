package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.Constant;
import mikenakis.kit.Kit;

import java.util.Optional;

public class AttributeReader
{
	public final BufferReader bufferReader;
	public final ConstantResolver constantResolver;

	public AttributeReader( BufferReader bufferReader, ConstantResolver constantResolver )
	{
		this.bufferReader = bufferReader;
		this.constantResolver = constantResolver;
	}

	public int readInt() { return bufferReader.readInt(); }
	public int readSignedByte() { return bufferReader.readSignedByte(); }
	public int readSignedShort() { return bufferReader.readSignedShort(); }
	public int readUnsignedByte() { return bufferReader.readUnsignedByte(); }
	public int readUnsignedShort() { return bufferReader.readUnsignedShort(); }
	public double readDouble() { return bufferReader.readDouble(); }
	public Buffer readBuffer( int count ) { return bufferReader.readBuffer( count ); }

	public Constant readIndexAndGetConstant()
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return constantResolver.getConstant( constantIndex );
	}

	public Optional<Constant> tryReadIndexAndGetConstant()
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return constantIndex == 0 ? Optional.empty() : Optional.of( constantResolver.getConstant( constantIndex ) );
	}

	public CodeAttributeReader asCodeAttributeReader() { return Kit.fail(); }
}
