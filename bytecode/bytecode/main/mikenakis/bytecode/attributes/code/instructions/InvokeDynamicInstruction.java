package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.constants.InvokeDynamicConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;

public final class InvokeDynamicInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.INVOKEDYNAMIC );
		}

		public InvokeDynamicInstruction newInstruction( InvokeDynamicConstant invokeDynamicConstant )
		{
			return new InvokeDynamicInstruction( this, invokeDynamicConstant );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			return new InvokeDynamicInstruction( this, codeAttribute, pc, wide, bufferReader );
		}
	}

	public final Model model;
	public final InvokeDynamicConstant invokeDynamicConstant;

	private InvokeDynamicInstruction( Model model, InvokeDynamicConstant invokeDynamicConstant )
	{
		super( Optional.empty() );
		this.model = model;
		this.invokeDynamicConstant = invokeDynamicConstant;
	}

	private InvokeDynamicInstruction( Model model, CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		assert !wide;
		this.model = model;
		invokeDynamicConstant = codeAttribute.constantPool.readIndexAndGetConstant( bufferReader ).asInvokeDynamicConstant();
		int operand2 = bufferReader.readUnsignedByte();
		assert operand2 == 0;
		int operand3 = bufferReader.readUnsignedByte();
		assert operand3 == 0;
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		invokeDynamicConstant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( model.opCode );
		invokeDynamicConstant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedByte( 0 );
		bufferWriter.writeUnsignedByte( 0 );
	}

	@Override public Optional<InvokeDynamicInstruction> tryAsInvokeDynamicInstruction()
	{
		return Optional.of( this );
	}
}
