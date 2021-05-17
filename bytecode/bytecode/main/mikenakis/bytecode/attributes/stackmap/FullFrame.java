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
 * 'Full' {@link Frame}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FullFrame extends Frame
{
	private static final int FRAME_TYPE = 255;

	public static final Kind KIND = new Kind( "FullFrame" )
	{
		@Override public Frame newFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
		{
			assert frameType == FRAME_TYPE;
			return new FullFrame( codeAttribute, previousFrame, bufferReader );
		}
	};

	public final List<VerificationType> localVerificationTypes;
	public final List<VerificationType> stackVerificationTypes;

	public FullFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		super( KIND, targetInstruction );
		this.localVerificationTypes = localVerificationTypes;
		this.stackVerificationTypes = stackVerificationTypes;
	}

	FullFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, BufferReader bufferReader )
	{
		super( KIND, findTargetInstruction( codeAttribute, previousFrame, bufferReader.readUnsignedShort() ) );
		int localCount = bufferReader.readUnsignedShort();
		localVerificationTypes = new ArrayList<>( localCount );
		for( int i = 0; i < localCount; i++ )
			localVerificationTypes.add( VerificationType.parse( codeAttribute, bufferReader ) );
		int stackCount = bufferReader.readUnsignedShort();
		stackVerificationTypes = new ArrayList<>( stackCount );
		for( int i = 0; i < stackCount; i++ )
			stackVerificationTypes.add( VerificationType.parse( codeAttribute, bufferReader ) );
	}

	@Override public String getName( Optional<Frame> previousFrame )
	{
		return kind.name;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.intern( constantPool );
		for( VerificationType verificationType : stackVerificationTypes )
			verificationType.intern( constantPool );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		super.collectTargets( targetInstructionConsumer );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.collectTargets( targetInstructionConsumer );
		for( VerificationType verificationType : stackVerificationTypes )
			verificationType.collectTargets( targetInstructionConsumer );
	}

	@Override public void write( Optional<Frame> previousFrame, ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		bufferWriter.writeUnsignedByte( FRAME_TYPE );
		bufferWriter.writeUnsignedShort( offsetDelta );
		bufferWriter.writeUnsignedShort( localVerificationTypes.size() );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.write( constantPool, bufferWriter );
		bufferWriter.writeUnsignedShort( stackVerificationTypes.size() );
		for( VerificationType verificationType : stackVerificationTypes )
			verificationType.write( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( localVerificationTypes.size() ).append( " localVerificationTypes, " );
		builder.append( stackVerificationTypes.size() ).append( " stackVerificationTypes" );
	}
}
