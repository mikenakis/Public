package mikenakis.bytecode;

import mikenakis.bytecode.kit.Buffer;

class ConstantEntry
{
	private final ConstantPool constantPool;
	private final ConstantKind constantKind;
	private Buffer buffer;
	private Constant constant;

	ConstantEntry( ConstantPool constantPool, ConstantKind constantKind, Buffer buffer )
	{
		this.constantPool = constantPool;
		this.constantKind = constantKind;
		this.buffer = buffer;
	}

	ConstantEntry( ConstantPool constantPool, Constant constant )
	{
		this.constantPool = constantPool;
		constantKind = constant.kind;
		this.constant = constant;
	}

	Constant getConstant()
	{
		if( constant != null )
			return constant;
		constant = constantKind.parse( constantPool, buffer );
		return constant;
	}

	@Override public String toString()
	{
		return buffer.getLength() + " bytes";
	}
}
