package mikenakis.bytecode.attributes.stackmap;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * 'Same' {@link Frame}.
 *
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is zero.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameFrame extends Frame
{
	private static final int EXTENDED_FRAME_TYPE = 251;

	public static final Kind KIND = new Kind( "SameFrame" )
	{
		@Override public Frame newFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
		{
			return new SameFrame( codeAttribute, previousFrame, frameType, bufferReader );
		}
	};

	public SameFrame( Instruction targetInstruction )
	{
		super( KIND, targetInstruction );
	}

	SameFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
	{
		super( KIND, findTargetInstruction( codeAttribute, previousFrame, frameType == EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType ) );
		assert (frameType >= 0 && frameType <= 63) || frameType == EXTENDED_FRAME_TYPE;
	}

	@Override public void write( Optional<Frame> previousFrame, ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		if( offsetDelta >= 0 && offsetDelta <= 63 )
			bufferWriter.writeUnsignedByte( offsetDelta );
		else
		{
			bufferWriter.writeUnsignedByte( EXTENDED_FRAME_TYPE );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
	}

	@Override public String getName( Optional<Frame> previousFrame )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		if( offsetDelta >= 0 && offsetDelta <= 63 )
			return kind.name;
		return "SameExtendedFrame";
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
	}
}
