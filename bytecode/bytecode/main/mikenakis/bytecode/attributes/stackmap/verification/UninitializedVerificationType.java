package mikenakis.bytecode.attributes.stackmap.verification;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.function.Consumer;

/**
 * 'Uninitialized' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class UninitializedVerificationType extends VerificationType
{
	public static final Kind KIND = new Kind( 8, "Uninitialized" )
	{
		@Override public VerificationType newVerificationType( CodeAttribute codeAttribute, BufferReader bufferReader )
		{
			return new UninitializedVerificationType( codeAttribute, bufferReader );
		}
	};

	public final AbsoluteInstructionReference instructionReference;

	public UninitializedVerificationType( CodeAttribute codeAttribute, Instruction targetInstruction )
	{
		super( KIND );
		assert codeAttribute.instructions.contains( targetInstruction );
		instructionReference = new AbsoluteInstructionReference( codeAttribute, targetInstruction );
	}

	public UninitializedVerificationType( CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		super( KIND );
		instructionReference = new AbsoluteInstructionReference( codeAttribute, false, bufferReader );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		/* nothing to do */
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		super.write( constantPool, bufferWriter );
		instructionReference.write( false, bufferWriter );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( instructionReference.getTargetInstruction() );
	}
}
