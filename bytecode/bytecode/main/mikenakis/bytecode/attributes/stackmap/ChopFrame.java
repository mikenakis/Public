package mikenakis.bytecode.attributes.stackmap;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.Optional;

/**
 * 'Chop' {@link Frame}.
 *
 * the operand stack is empty and the current locals are the same as the locals in the previous frame, except that the k last locals are absent.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ChopFrame extends Frame
{
	public static final Kind KIND = new Kind( "ChopFrame" )
	{
		@Override public Frame newFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
		{
			return new ChopFrame( codeAttribute, previousFrame, frameType, bufferReader );
		}
	};

	public final int count;

	public ChopFrame( Instruction targetInstruction, int count )
	{
		super( KIND, targetInstruction );
		assert count >= 0 && count <= 3;
		this.count = count;
	}

	ChopFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader )
	{
		super( KIND, findTargetInstruction( codeAttribute, previousFrame, bufferReader.readUnsignedShort() ) );
		assert frameType >= 248 && frameType < 251;
		count = 251 - frameType;
	}

	@Override public String getName( Optional<Frame> previousFrame )
	{
		return kind.name;
	}

	@Override public void write( Optional<Frame> previousFrame, ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int offsetDelta = getOffsetDelta( previousFrame );
		bufferWriter.writeUnsignedByte( 251 - count );
		bufferWriter.writeUnsignedShort( offsetDelta );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "count = " ).append( count );
	}
}
