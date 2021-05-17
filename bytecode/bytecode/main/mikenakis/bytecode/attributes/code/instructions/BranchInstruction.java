package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.attributes.code.RelativeInstructionReference;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public final class BranchInstruction extends Instruction
{
	public enum Flavor
	{
		ANY,
		SHORT,
		LONG
	}

	public enum Kind
	{
		GOTO( OpCode.GOTO, OpCode.GOTO_W ),
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
			throw new AssertionError( String.valueOf( opCode ) );
		}

		public int getOpCode( Flavor flavor )
		{
			switch( flavor )
			{
				case ANY:
				case SHORT:
					return shortOpCode;
				case LONG:
					return longOpCode;
				default:
					throw new AssertionError( flavor.name() );
			}
		}
	}

	public static final class Model extends InstructionModel
	{
		public final Kind kind;
		public final Flavor flavor;

		public Model( Kind kind )
		{
			super( kind.shortOpCode );
			this.kind = kind;
			flavor = Flavor.ANY;
		}

		public Model( int opCode )
		{
			super( opCode );
			kind = Kind.fromOpCode( opCode );
			flavor = kind.getFlavorOfOpCode( opCode );
		}

		public BranchInstruction newInstruction( Instruction targetInstruction )
		{
			return new BranchInstruction( this, targetInstruction );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new BranchInstruction( this, codeAttribute, pc, bufferReader, fixUps );
		}
	}

	public final Model model;
	private int opCode;
	private Flavor flavor;
	public final RelativeInstructionReference instructionReference;

	private BranchInstruction( Model model, Instruction targetInstruction )
	{
		super( Optional.empty() );
		this.model = model;
		opCode = model.opCode;
		flavor = Flavor.ANY;
		instructionReference = new RelativeInstructionReference( this, targetInstruction );
	}

	private BranchInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader, Collection<Runnable> fixUps )
	{
		super( Optional.of( pc ) );
		assert model.flavor != Flavor.ANY;
		this.model = model;
		opCode = model.opCode;
		flavor = model.flavor;
		instructionReference = new RelativeInstructionReference( codeAttribute, this, flavor == Flavor.LONG, bufferReader, fixUps );
	}

	public int getOpCode()
	{
		return opCode;
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		flavor = instructionReference.isShort() ? Flavor.SHORT : Flavor.LONG;
		opCode = model.kind.getOpCode( flavor );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		assert flavor != Flavor.ANY;
		bufferWriter.writeUnsignedByte( opCode );
		instructionReference.write( flavor == Flavor.LONG, bufferWriter );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( instructionReference.getTargetInstruction() );
	}

	@Override public Optional<BranchInstruction> tryAsBranchInstruction()
	{
		return Optional.of( this );
	}
}
