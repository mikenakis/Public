package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 'Append' {@link StackMapFrame}.
 * <p>
 * the operand stack is empty and the current locals are the same as the locals in the previous frame, except that k additional locals are defined.
 *
 * @author michael.gr
 */
public final class AppendStackMapFrame extends StackMapFrame
{
	public static AppendStackMapFrame read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap, Optional<StackMapFrame> previousFrame, int frameType )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		assert frameType >= 252 && frameType <= 254;
		int localCount = frameType - 251;
		List<VerificationType> localVerificationTypes = new ArrayList<>( localCount );
		for( int j = 0; j < localCount; j++ )
			localVerificationTypes.add( VerificationType.read( bufferReader, constantPool, locationMap ) );
		return of( targetInstruction, localVerificationTypes );
	}

	public static AppendStackMapFrame of( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		return new AppendStackMapFrame( targetInstruction, localVerificationTypes );
	}

	public static final String typeName = "AppendFrame";

	private final List<VerificationType> localVerificationTypes;

	private AppendStackMapFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		super( tag_Append, targetInstruction );
		assert !localVerificationTypes.isEmpty();
		assert localVerificationTypes.size() <= 3;
		this.localVerificationTypes = localVerificationTypes;
	}

	public List<VerificationType> localVerificationTypes() { return Collections.unmodifiableList( localVerificationTypes ); }

	@Override public String getName( Optional<StackMapFrame> previousFrame ) { return typeName; }
	@Deprecated @Override public AppendStackMapFrame asAppendStackMapFrame() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return localVerificationTypes.size() + " localVerificationTypes"; }

	@Override public void intern( Interner interner )
	{
		for( VerificationType verificationType : localVerificationTypes() )
			verificationType.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = locationMap.getOffsetDelta( this, previousFrame );
		assert localVerificationTypes.size() <= 3;
		bufferWriter.writeUnsignedByte( 251 + localVerificationTypes.size() );
		bufferWriter.writeUnsignedShort( offsetDelta );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.write( bufferWriter, constantPool, locationMap );
	}
}
