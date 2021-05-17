package mikenakis.bytecode.attributes.stackmap;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 'Same Locals, 1 Stack Item' {@link Frame}.
 *
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is 1.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameLocals1StackItemFrame extends Frame
{
	private static final int EXTENDED_FRAME_TYPE = 247;

	public static final Kind KIND = new Kind( "SameLocals1StackItemFrame" )
	{
		@Override public Frame newFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
		{
			assert (frameType >= 64 && frameType <= 127) || frameType == EXTENDED_FRAME_TYPE;
			return new SameLocals1StackItemFrame( frameType, codeAttribute, previousFrame, bufferReader );
		}
	};

	public final VerificationType stackVerificationType;

	public SameLocals1StackItemFrame( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		super( KIND, targetInstruction );
		this.stackVerificationType = stackVerificationType;
	}

	SameLocals1StackItemFrame( int frameType, CodeAttribute codeAttribute, Optional<Frame> previousFrame, BufferReader bufferReader )
	{
		super( KIND, findTargetInstruction( codeAttribute, previousFrame, frameType == EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType - 64 ) );
		assert (frameType >= 64 && frameType <= 127) || frameType == EXTENDED_FRAME_TYPE;
		stackVerificationType = VerificationType.parse( codeAttribute, bufferReader );
	}

	@Override public void intern( ConstantPool constantPool )
	{
		stackVerificationType.intern( constantPool );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		super.collectTargets( targetInstructionConsumer );
		stackVerificationType.collectTargets( targetInstructionConsumer );
	}

	@Override public void write( Optional<Frame> previousFrame, ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		if( offsetDelta <= 127 )
			bufferWriter.writeUnsignedByte( 64 + offsetDelta );
		else
		{
			bufferWriter.writeUnsignedByte( EXTENDED_FRAME_TYPE );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
		stackVerificationType.write( constantPool, bufferWriter );
	}

	@Override public String getName( Optional<Frame> previousFrame )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		if( offsetDelta <= 127 )
			return kind.name;
		return "SameLocals1StackItemExtendedFrame";
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "stackVerificationType:" );
		stackVerificationType.toStringBuilder( builder );
	}
}
