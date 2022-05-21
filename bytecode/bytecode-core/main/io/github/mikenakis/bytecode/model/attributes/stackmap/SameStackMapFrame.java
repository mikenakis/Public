package io.github.mikenakis.bytecode.model.attributes.stackmap;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Same' {@link StackMapFrame}.
 * <p>
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is zero.
 *
 * @author michael.gr
 */
public final class SameStackMapFrame extends StackMapFrame
{
	public static SameStackMapFrame read( BufferReader bufferReader, ReadingLocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert (frameType >= 0 && frameType <= 63) || frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE;
		int offsetDelta = frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE ? bufferReader.readUnsignedShort() : frameType;
		Instruction targetInstruction = findTargetInstruction( previousFrame, offsetDelta, locationMap ).orElseThrow();
		return of( targetInstruction );
	}

	public static SameStackMapFrame of( Instruction targetInstruction )
	{
		return new SameStackMapFrame( targetInstruction );
	}

	public static final int EXTENDED_FRAME_TYPE = 251;
	public static final String typeName = "SameFrame";
	public static final String extendedTypeName = "SameExtendedFrame";

	private SameStackMapFrame( Instruction targetInstruction )
	{
		super( tag_Same, targetInstruction );
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame )
	{
		return typeName;
//		int offsetDelta = getOffsetDelta( previousFrame );
//		if( offsetDelta >= 0 && offsetDelta <= 63 )
//			return typeName;
//		return extendedTypeName;
	}

	@Deprecated @Override public SameStackMapFrame asSameStackMapFrame() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "targetInstruction = " + getTargetInstruction(); }

	@Override public void intern( Interner interner )
	{
		// nothing to do.
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = locationMap.getOffsetDelta( this, previousFrame );
		if( offsetDelta >= 0 && offsetDelta <= 63 )
			bufferWriter.writeUnsignedByte( offsetDelta );
		else
		{
			bufferWriter.writeUnsignedByte( EXTENDED_FRAME_TYPE );
			bufferWriter.writeUnsignedShort( offsetDelta );
		}
	}
}
