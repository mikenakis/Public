package mikenakis.bytecode.attributes.stackmap;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 'Append' {@link Frame}.
 *
 * the operand stack is empty and the current locals are the same as the locals in the previous frame, except that k additional locals are defined.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class AppendFrame extends Frame
{
	public static final Kind KIND = new Kind( "AppendFrame" )
	{
		@Override public Frame newFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
		{
			return new AppendFrame( frameType, codeAttribute, previousFrame, bufferReader );
		}
	};

	public final List<VerificationType> localVerificationTypes;

	public AppendFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		super( KIND, targetInstruction );
		assert !localVerificationTypes.isEmpty();
		assert localVerificationTypes.size() <= 3;
		this.localVerificationTypes = localVerificationTypes;
	}

	AppendFrame( int frameType, CodeAttribute codeAttribute, Optional<Frame> previousFrame, BufferReader bufferReader )
	{
		super( KIND, findTargetInstruction( codeAttribute, previousFrame, bufferReader.readUnsignedShort() ) );
		assert frameType >= 252 && frameType <= 254;
		int localCount = frameType - 251;
		localVerificationTypes = new ArrayList<>( localCount );
		for( int i = 0; i < localCount; i++ )
			localVerificationTypes.add( VerificationType.parse( codeAttribute, bufferReader ) );
	}

	@Override public String getName( Optional<Frame> previousFrame )
	{
		return kind.name;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.intern( constantPool );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		super.collectTargets( targetInstructionConsumer );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.collectTargets( targetInstructionConsumer );
	}

	@Override public void write( Optional<Frame> previousFrame, ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		assert localVerificationTypes.size() <= 3;
		bufferWriter.writeUnsignedByte( 251 + localVerificationTypes.size() );
		bufferWriter.writeUnsignedShort( offsetDelta );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.write( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( localVerificationTypes.size() ).append( " localVerificationTypes" );
	}
}
