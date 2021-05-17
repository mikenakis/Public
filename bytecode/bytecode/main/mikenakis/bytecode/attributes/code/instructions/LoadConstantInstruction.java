package mikenakis.bytecode.attributes.code.instructions;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.code.InstructionModel;

import java.util.Optional;

public abstract class LoadConstantInstruction extends Instruction
{
	public static LoadConstantInstruction create( Constant constant )
	{
		Model model = Model.forConstant( constant );
		return model.newInstruction( constant );
	}

	public abstract static class Model extends InstructionModel
	{
		public static Model forConstant( Constant constant )
		{
			assert false; //test the following code to make sure it is equivalent to the commented-out code below
			return ConstantPool.tryGetOperandlessLoadConstantInstructionModelFromConstant( constant ) //
				.or( () -> ImmediateLoadConstantInstruction.tryGetModelFromConstant( constant ) ) //
				.orElseGet( () -> IndirectLoadConstantInstruction.getModelFromConstant( constant ) );
//			Optional<Model> model = ConstantPool.tryGetOperandlessLoadConstantInstructionModelFromConstant( constant );
//			if( model.isPresent() )
//				return model.get();
//			model = ImmediateLoadConstantInstruction.tryGetModelFromConstant( constant );
//			if( model.isPresent() )
//				return model.get();
//			return IndirectLoadConstantInstruction.getModelFromConstant( constant );
		}

		protected Model( int opCode )
		{
			super( opCode );
		}

		public abstract LoadConstantInstruction newInstruction( Constant constant );
	}

	protected LoadConstantInstruction( Optional<Integer> pc )
	{
		super( pc );
	}
}
