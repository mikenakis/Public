package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModels;
import mikenakis.bytecode.attributes.code.OpCode;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Helpers;

import java.util.Collection;
import java.util.Optional;

public final class ImmediateLoadConstantInstruction extends LoadConstantInstruction
{
	static Optional<LoadConstantInstruction.Model> tryGetModelFromConstant( Constant constant )
	{
		if( constant.kind == IntegerConstant.KIND )
		{
			if( Helpers.isSignedByte( constant.asIntegerConstant().value ) ) //FIXME why SignedByte here, but UnsignedShort below?
				return Optional.of( InstructionModels.BIPUSH_MODEL );
			if( Helpers.isUnsignedShort( constant.asIntegerConstant().value ) )
				return Optional.of( InstructionModels.SIPUSH_MODEL );
		}
		return Optional.empty();
	}

	public static final class Model extends LoadConstantInstruction.Model
	{
		public Model( int opCode )
		{
			super( opCode );
		}

		@Override public LoadConstantInstruction newInstruction( Constant constant )
		{
			assert constant.kind == IntegerConstant.KIND;
			IntegerConstant integerConstant = constant.asIntegerConstant();
			int immediateValue = integerConstant.value;
			assert (Helpers.isSignedByte( immediateValue ) && this == InstructionModels.BIPUSH_MODEL) ||
			       (Helpers.isUnsignedShort( immediateValue ) && this == InstructionModels.SIPUSH_MODEL);
			return new ImmediateLoadConstantInstruction( this, immediateValue );
		}

		@Override public Instruction parseInstruction( CodeAttribute codeAttribute, int pc, boolean wide, BufferReader bufferReader, Collection<Runnable> fixUps )
		{
			assert !wide;
			return new ImmediateLoadConstantInstruction( this, pc, bufferReader );
		}
	}

	public final Model model;
	public final int immediateValue;

	private ImmediateLoadConstantInstruction( Model model, int immediateValue )
	{
		super( Optional.empty() );
		this.model = model;
		this.immediateValue = immediateValue;
	}

	private ImmediateLoadConstantInstruction( Model model, int pc, BufferReader bufferReader )
	{
		super( Optional.of( pc ) );
		this.model = model;
		switch( model.opCode )
		{
			case OpCode.BIPUSH:
			{
				immediateValue = bufferReader.readUnsignedByte();
				break;
			}
			case OpCode.SIPUSH:
			{
				immediateValue = bufferReader.readUnsignedShort();
				break;
			}
			default:
				assert false;
				immediateValue = 0;
		}
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
		bufferWriter.writeUnsignedByte( model.opCode );
		switch( model.opCode )
		{
			case OpCode.BIPUSH:
				bufferWriter.writeUnsignedByte( immediateValue );
				break;
			case OpCode.SIPUSH:
				bufferWriter.writeUnsignedShort( immediateValue );
				break;
			default:
				assert false;
				break;
		}
	}

	@Override public Optional<ImmediateLoadConstantInstruction> tryAsImmediateLoadConstantInstruction()
	{
		return Optional.of( this );
	}
}
