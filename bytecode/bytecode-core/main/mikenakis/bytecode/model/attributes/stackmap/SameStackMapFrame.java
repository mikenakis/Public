package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Same' {@link StackMapFrame}.
 * <p>
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is zero.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameStackMapFrame extends StackMapFrame
{
	public static SameStackMapFrame read( CodeAttributeReader codeAttributeReader, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert (frameType >= 0 && frameType <= 63) || frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE;
		int offsetDelta = frameType == SameStackMapFrame.EXTENDED_FRAME_TYPE ? codeAttributeReader.readUnsignedShort() : frameType;
		Instruction targetInstruction = findTargetInstruction( previousFrame, offsetDelta, codeAttributeReader.locationMap ).orElseThrow();
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

	@Override public void write( CodeConstantWriter codeConstantWriter, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = codeConstantWriter.getOffsetDelta( this, previousFrame );
		if( offsetDelta >= 0 && offsetDelta <= 63 )
			codeConstantWriter.writeUnsignedByte( offsetDelta );
		else
		{
			codeConstantWriter.writeUnsignedByte( EXTENDED_FRAME_TYPE );
			codeConstantWriter.writeUnsignedShort( offsetDelta );
		}
	}
}
