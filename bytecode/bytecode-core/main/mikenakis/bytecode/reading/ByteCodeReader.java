package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.ByteCodeVersion;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.Optional;

public class ByteCodeReader
{
	public static ByteCodeType read( Class<?> jvmClass )
	{
		String resourceName = jvmClass.getSimpleName() + ".class";
		byte[] bytes = Kit.uncheckedTryGetWithResources( //
			() -> jvmClass.getResourceAsStream( resourceName ), //
			i -> i.readAllBytes() );
		return read( bytes );
	}

	public static ByteCodeType read( byte[] bytes )
	{
		ByteCodeReader byteCodeReader = new ByteCodeReader( bytes );
		ByteCodeType byteCodeType = ByteCodeType.read( byteCodeReader );
		assert byteCodeReader.bufferReader.isAtEnd();
		return byteCodeType;
	}

	private final int magic;
	private final BufferReader bufferReader;
	private final ByteCodeVersion version;
	private final ConstantPool constantPool;

	private ByteCodeReader( byte[] bytes )
	{
		bufferReader = BufferReader.of( bytes, 0, bytes.length );
		magic = bufferReader.readInt();
		version = ByteCodeVersion.read( bufferReader );
		constantPool = ConstantPoolReader.read( bufferReader );
	}

	public int getMagic()
	{
		return magic;
	}

	public ByteCodeVersion getVersion()
	{
		return version;
	}

	public AttributeReader getAttributeReader()
	{
		return new AttributeReader( bufferReader, constantPool );
	}

	public int readInt() { return bufferReader.readInt(); }
	public int readUnsignedByte() { return bufferReader.readUnsignedByte(); }
	public int readUnsignedShort() { return bufferReader.readUnsignedShort(); }
	public double readDouble() { return bufferReader.readDouble(); }
	public Buffer readBuffer( int count ) { return bufferReader.readBuffer( count ); }

	public void applyFixUps( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		constantPool.runBootstrapFixUps( bootstrapMethodsAttribute );
	}

	public Collection<ClassConstant> getExtraClassReferences()
	{
		return constantPool.getExtraClassReferences();
	}

	public Optional<Constant> tryReadIndexAndGetConstant()
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return constantIndex == 0 ? Optional.empty() : Optional.of( constantPool.getConstant( constantIndex ) );
	}

	public Constant readIndexAndGetConstant()
	{
		int constantIndex = bufferReader.readUnsignedShort();
		return constantPool.getConstant( constantIndex );
	}
}
