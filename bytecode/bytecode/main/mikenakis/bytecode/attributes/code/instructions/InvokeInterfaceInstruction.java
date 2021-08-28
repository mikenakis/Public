package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Collection;
import java.util.Optional;

public final class InvokeInterfaceInstruction extends Instruction
{
	public static final class Model extends InstructionModel
	{
		public Model()
		{
			super( OpCode.INVOKEINTERFACE );
		}

		public InvokeInterfaceInstruction newInstruction( Constant constant, int argumentCount )
		{
			return new InvokeInterfaceInstruction( this, constant, argumentCount );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			return new InvokeInterfaceInstruction( this, codeAttribute, pc, wide, bufferReader );
		}
	}

	public final Model model;
	public final Constant constant;
	public final int argumentCount;

	private InvokeInterfaceInstruction( Model model, Constant constant, int argumentCount )
	{
		super( Optional.empty() );
		this.model = model;
		this.constant = constant;
		this.argumentCount = argumentCount;
	}

	private InvokeInterfaceInstruction( Model model, CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		assert !wide;
		this.model = model;
		constant = codeAttribute.constantPool.readIndexAndGetConstant( bufferReader );
		argumentCount = bufferReader.readUnsignedByte();
		int extraByte = bufferReader.readUnsignedByte(); //one extra byte, unused.
		assert extraByte == 0;
	}

	@Override public Model getModel()
	{
		return model;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		constant.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( model.opCode );
		constant.writeIndex( constantPool, bufferWriter );
		bufferWriter.writeUnsignedByte( argumentCount );
		bufferWriter.writeUnsignedByte( 0 );
	}

	@Override public Optional<InvokeInterfaceInstruction> tryAsInvokeInterfaceInstruction()
	{
		return Optional.of( this );
	}
}
