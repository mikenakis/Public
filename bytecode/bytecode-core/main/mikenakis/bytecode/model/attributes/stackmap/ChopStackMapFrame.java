package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * 'Chop' {@link StackMapFrame}.
 * <p>
 * the operand stack is empty and the current locals are the same as the locals in the previous frame, except that the k last locals are absent.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ChopStackMapFrame extends StackMapFrame
{
	public static ChopStackMapFrame read( CodeAttributeReader codeAttributeReader, Optional<StackMapFrame> previousFrame, int frameType )
	{
		assert frameType >= 248 && frameType < 251;
		Instruction targetInstruction = findTargetInstruction( previousFrame, codeAttributeReader.readUnsignedShort(), codeAttributeReader.locationMap ).orElseThrow();
		return of( targetInstruction, 251 - frameType );
	}

	public static ChopStackMapFrame of( Instruction targetInstruction, int count )
	{
		return new ChopStackMapFrame( targetInstruction, count );
	}

	public static final String typeName = "ChopFrame";

	private final int count;

	private ChopStackMapFrame( Instruction targetInstruction, int count )
	{
		super( tag_Chop, targetInstruction );
		assert count >= 0 && count <= 3;
		this.count = count;
	}

	public int count()
	{
		return count;
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame ) { return typeName; }
	@Deprecated @Override public ChopStackMapFrame asChopStackMapFrame() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "count = " + count; }

	@Override public void intern( Interner interner )
	{
		//nothing to do.
	}

	@Override public void write( CodeConstantWriter codeConstantWriter, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = codeConstantWriter.getOffsetDelta( this, previousFrame );
		codeConstantWriter.writeUnsignedByte( 251 - count );
		codeConstantWriter.writeUnsignedShort( offsetDelta );
	}
}
