package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
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
 * @author Michael Belivanakis (michael.gr)
 */
public final class AppendStackMapFrame extends StackMapFrame
{
	public static AppendStackMapFrame read( CodeAttributeReader codeAttributeReader, Optional<StackMapFrame> previousFrame, int frameType )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, codeAttributeReader.readUnsignedShort(), codeAttributeReader.locationMap ).orElseThrow();
		assert frameType >= 252 && frameType <= 254;
		int localCount = frameType - 251;
		List<VerificationType> localVerificationTypes = new ArrayList<>( localCount );
		for( int j = 0; j < localCount; j++ )
		{
			VerificationType verificationType = VerificationType.read( codeAttributeReader );
			localVerificationTypes.add( verificationType );
		}
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

	@Override public void write( CodeConstantWriter codeConstantWriter, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = codeConstantWriter.getOffsetDelta( this, previousFrame );
		assert localVerificationTypes.size() <= 3;
		codeConstantWriter.writeUnsignedByte( 251 + localVerificationTypes.size() );
		codeConstantWriter.writeUnsignedShort( offsetDelta );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.write( codeConstantWriter );
	}
}
