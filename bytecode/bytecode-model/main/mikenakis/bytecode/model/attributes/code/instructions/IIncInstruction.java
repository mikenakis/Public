package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;

public final class IIncInstruction extends Instruction
{
	public static IIncInstruction of( int index, int delta )
	{
		return of( false, index, delta );
	}

	public static IIncInstruction of( boolean wide, int index, int delta )
	{
		return new IIncInstruction( wide, index, delta );
	}

	private boolean wide; //TODO
	public final int index;
	public final int delta;

	private IIncInstruction( boolean wide, int index, int delta )
	{
		super( Group.IInc );
		this.wide = wide;
		this.index = index;
		this.delta = delta;
	}

	public boolean isWide()
	{
		return wide;
	}

	@Override public int getOpCode()
	{
		return OpCode.IINC;
	}

	// TODO:
	//	void intern( ConstantPool constantPool )
	//	{
	//		wide = !(Helpers.isUnsignedByte( index ) && Helpers.isSignedByte( delta ));
	//	}

	@Deprecated @Override public IIncInstruction asIIncInstruction()
	{
		return this;
	}
}
