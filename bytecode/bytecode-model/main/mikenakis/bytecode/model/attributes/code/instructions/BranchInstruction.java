package mikenakis.bytecode.model.attributes.code.instructions;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class BranchInstruction extends Instruction
{
	public static BranchInstruction of( int opCode, Instruction targetInstruction )
	{
		BranchInstruction branchInstruction = of( opCode );
		branchInstruction.setTargetInstruction( targetInstruction );
		return branchInstruction;
	}

	public static BranchInstruction of( int opCode )
	{
		return of( opCode, Kind.fromOpCode( opCode ).getFlavorOfOpCode( opCode ) );
	}

	private static BranchInstruction of( int opCode, Flavor flavor )
	{
		return new BranchInstruction( opCode, flavor );
	}

	private enum Flavor
	{
		ANY, SHORT, LONG
	}

	private enum Kind
	{
		GOTO( OpCode.GOTO, OpCode.GOTO_W ), //
		JSR( OpCode.JSR, OpCode.JSR_W );

		public static Kind fromOpCode( int opCode )
		{
			for( Kind kind : values() )
				if( opCode == kind.shortOpCode || opCode == kind.longOpCode )
					return kind;
			throw new AssertionError( String.valueOf( opCode ) );
		}

		public final int shortOpCode;
		public final int longOpCode;

		Kind( int shortOpCode, int longOpCode )
		{
			this.shortOpCode = shortOpCode;
			this.longOpCode = longOpCode;
		}

		public Flavor getFlavorOfOpCode( int opCode )
		{
			if( opCode == shortOpCode )
				return Flavor.SHORT;
			if( opCode == longOpCode )
				return Flavor.LONG;
			throw new AssertionError( opCode );
		}

		public int getOpCode( Flavor flavor )
		{
			return switch( flavor )
				{
					case ANY, SHORT -> shortOpCode;
					case LONG -> longOpCode;
				};
		}
	}

	private final int opCode;
	private final Flavor flavor;
	private Instruction targetInstruction; //null means that it has not been set yet.

	private BranchInstruction( int opCode, Flavor flavor )
	{
		super( Group.Branch );
		this.opCode = opCode;
		this.flavor = flavor;
	}

	public Instruction getTargetInstruction()
	{
		assert targetInstruction != null;
		return targetInstruction;
	}

	public void setTargetInstruction( Instruction targetInstruction )
	{
		assert this.targetInstruction == null;
		assert targetInstruction != null;
		this.targetInstruction = targetInstruction;
	}

	public boolean isAny()
	{
		return flavor == Flavor.ANY;
	}

	public boolean isLong()
	{
		return flavor == Flavor.LONG;
	}

	public int getOpCode()
	{
		return opCode;
	}

// TODO:
//	void intern( ConstantPool constantPool )
//	{
//		flavor = instructionReference.isShort() ? Flavor.SHORT : Flavor.LONG;
//		opCode = Kind.fromOpCode( opCode ).getOpCode( flavor );
//	}
//
//	void write( ConstantPool constantPool, BufferWriter bufferWriter )
//	{
//		assert flavor != Flavor.ANY;
//		bufferWriter.writeUnsignedByte( opCode );
//		instructionReference.write( flavor == Flavor.LONG, bufferWriter );
//	}

	@Deprecated @Override public BranchInstruction asBranchInstruction()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return OpCode.getOpCodeName( opCode );
	}
}
