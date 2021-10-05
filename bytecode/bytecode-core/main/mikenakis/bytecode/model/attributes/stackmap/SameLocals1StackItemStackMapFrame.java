package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Same Locals, 1 Stack Item' {@link StackMapFrame}.
 * <p>
 * the frame has exactly the same locals as the previous stack map frame and the number of stack items is 1.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameLocals1StackItemStackMapFrame extends StackMapFrame
{
	public static SameLocals1StackItemStackMapFrame read( CodeAttributeReader codeAttributeReader, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert (frameType >= 64 && frameType <= 127) || frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE;
		int offsetDelta = frameType == SameLocals1StackItemStackMapFrame.EXTENDED_FRAME_TYPE ? codeAttributeReader.readUnsignedShort() : frameType - 64;
		var targetInstruction = findTargetInstruction( previousFrame, offsetDelta, codeAttributeReader.locationMap ).orElseThrow();
		VerificationType stackVerificationType = VerificationType.read( codeAttributeReader );
		return of( targetInstruction, stackVerificationType );
	}

	public static SameLocals1StackItemStackMapFrame of( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		return new SameLocals1StackItemStackMapFrame( targetInstruction, stackVerificationType );
	}

	public static final int EXTENDED_FRAME_TYPE = 247;
	public static final String typeName = "SameLocals1StackItemFrame";
	public static final String extendedTypeName = "SameLocals1StackItemExtendedFrame";

	public final VerificationType stackVerificationType;

	private SameLocals1StackItemStackMapFrame( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		super( tag_SameLocals1StackItem, targetInstruction );
		this.stackVerificationType = stackVerificationType;
	}

	@Deprecated @Override public SameLocals1StackItemStackMapFrame asSameLocals1StackItemStackMapFrame() { return this; }
	@Override public String getName( Optional<StackMapFrame> previousFrame )
	{
		return typeName;
//		int offsetDelta = getOffsetDelta( previousFrame );
//		if( offsetDelta <= 127 )
//			return typeName;
//		return extendedTypeName;
	}
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "stackVerificationType = " + stackVerificationType; }

	@Override public void intern( Interner interner )
	{
		stackVerificationType.intern( interner );
	}

	@Override public void write( CodeConstantWriter codeConstantWriter, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = codeConstantWriter.getOffsetDelta( this, previousFrame );
		if( offsetDelta <= 127 )
			codeConstantWriter.writeUnsignedByte( 64 + offsetDelta );
		else
		{
			codeConstantWriter.writeUnsignedByte( EXTENDED_FRAME_TYPE );
			codeConstantWriter.writeUnsignedShort( offsetDelta );
		}
		stackVerificationType.write( codeConstantWriter );
	}
}
