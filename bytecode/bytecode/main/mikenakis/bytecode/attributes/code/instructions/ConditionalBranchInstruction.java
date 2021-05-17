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

public final class ConditionalBranchInstruction extends Instruction
{
	@SuppressWarnings( { "SpellCheckingInspection" } )
	public enum Kind
	{
		//@formatter:off
		IFEQ      ( OpCode.IFEQ      , OpCode.IFNE ),
		IFNE      ( OpCode.IFNE      , OpCode.IFEQ ),
		IFLT      ( OpCode.IFLT      , OpCode.IFGE ),
		IFGE      ( OpCode.IFGE      , OpCode.IFLT ),
		IFGT      ( OpCode.IFGT      , OpCode.IFLE ),
		IFLE      ( OpCode.IFLE      , OpCode.IFGT ),
		IF_ICMPEQ ( OpCode.IF_ICMPEQ , OpCode.IF_ICMPNE ),
		IF_ICMPNE ( OpCode.IF_ICMPNE , OpCode.IF_ICMPEQ ),
		IF_ICMPLT ( OpCode.IF_ICMPLT , OpCode.IF_ICMPGE ),
		IF_ICMPGE ( OpCode.IF_ICMPGE , OpCode.IF_ICMPLT ),
		IF_ICMPGT ( OpCode.IF_ICMPGT , OpCode.IF_ICMPLE ),
		IF_ICMPLE ( OpCode.IF_ICMPLE , OpCode.IF_ICMPGT ),
		IF_ACMPEQ ( OpCode.IF_ACMPEQ , OpCode.IF_ACMPNE ),
		IF_ACMPNE ( OpCode.IF_ACMPNE , OpCode.IF_ACMPEQ ),
		IFNULL    ( OpCode.IFNULL    , OpCode.IFNONNULL ),
		IFNONNULL ( OpCode.IFNONNULL , OpCode.IFNULL    );
		//@formatter:on

		public static Kind fromOpCode( int opCode )
		{
			for( Kind kind : values() )
				if( opCode == kind.opCode )
					return kind;
			throw new AssertionError( String.valueOf( opCode ) );
		}

		public final int opCode;
		private final int reverseOpCode;

		Kind( int opCode, int reverseOpCode )
		{
			this.opCode = opCode;
			this.reverseOpCode = reverseOpCode;
		}
	}

	public static final class Model extends InstructionModel
	{
		public final Kind kind;

		public Model( Kind kind )
		{
			super( kind.opCode );
			this.kind = kind;
		}

		public Model( int opCode )
		{
			super( opCode );
			kind = Kind.fromOpCode( opCode );
		}

		public ConditionalBranchInstruction newInstruction( Instruction targetInstruction )
		{
			return new ConditionalBranchInstruction( this, targetInstruction );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new ConditionalBranchInstruction( this, codeAttribute, pc, bufferReader, fixUps );
		}
	}

	public final Model model;
	public final RelativeInstructionReference instructionReference;

	private ConditionalBranchInstruction( Model model, Instruction targetInstruction )
	{
		super( Optional.empty() );
		this.model = model;
		instructionReference = new RelativeInstructionReference( this, targetInstruction );
	}

	private ConditionalBranchInstruction( Model model, CodeAttribute codeAttribute, int pc, BufferReader bufferReader, Collection<Runnable> fixUps )
	{
		super( Optional.of( pc ) );
		this.model = model;
		instructionReference = new RelativeInstructionReference( codeAttribute, this, false, bufferReader, fixUps );
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		if( instructionReference.isShort() )
		{
			bufferWriter.writeUnsignedByte( model.kind.opCode );
			instructionReference.write( false, bufferWriter );
			return;
		}
		bufferWriter.writeUnsignedByte( model.kind.reverseOpCode );
		bufferWriter.writeSignedByte( 1 + 1 + 1 + instructionReference.getLength( true ) ); //length of this instruction plus length of GOTO_W instruction that follows
		bufferWriter.writeUnsignedByte( OpCode.GOTO_W );
		instructionReference.write( true, bufferWriter );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( instructionReference.getTargetInstruction() );
	}

	@Override public Optional<ConditionalBranchInstruction> tryAsConditionalBranchInstruction()
	{
		return Optional.of( this );
	}
}
