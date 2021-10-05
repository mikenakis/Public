package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.reading.CodeAttributeReader;
import mikenakis.bytecode.writing.CodeConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 'Full' {@link StackMapFrame}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FullStackMapFrame extends StackMapFrame
{
	public static FullStackMapFrame read( CodeAttributeReader codeAttributeReader, Optional<StackMapFrame> previousFrame )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, codeAttributeReader.readUnsignedShort(), codeAttributeReader.locationMap ).orElseThrow();
		List<VerificationType> localVerificationTypes = readVerificationTypes( codeAttributeReader );
		List<VerificationType> stackVerificationTypes = readVerificationTypes( codeAttributeReader );
		return of( targetInstruction, localVerificationTypes, stackVerificationTypes );
	}

	private static List<VerificationType> readVerificationTypes( CodeAttributeReader codeAttributeReader )
	{
		int count = codeAttributeReader.readUnsignedShort();
		List<VerificationType> verificationTypes = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
		{
			VerificationType verificationType = VerificationType.read( codeAttributeReader );
			verificationTypes.add( verificationType );
		}
		return verificationTypes;
	}

	public static FullStackMapFrame of( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		return new FullStackMapFrame( targetInstruction, localVerificationTypes, stackVerificationTypes );
	}

	public static final int type = 255;
	public static final String typeName = "FullFrame";

	public final List<VerificationType> localVerificationTypes;
	public final List<VerificationType> stackVerificationTypes;

	private FullStackMapFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		super( tag_Full, targetInstruction );
		this.localVerificationTypes = localVerificationTypes;
		this.stackVerificationTypes = stackVerificationTypes;
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame ) { return typeName; }
	@Deprecated @Override public FullStackMapFrame asFullStackMapFrame() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return localVerificationTypes.size() + " localVerificationTypes, " + stackVerificationTypes.size() + " stackVerificationTypes"; }

	@Override public void intern( Interner interner )
	{
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.intern( interner );
		for( VerificationType verificationType : stackVerificationTypes )
			verificationType.intern( interner );
	}

	@Override public void write( CodeConstantWriter codeConstantWriter, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = codeConstantWriter.getOffsetDelta( this, previousFrame );
		codeConstantWriter.writeUnsignedByte( type );
		codeConstantWriter.writeUnsignedShort( offsetDelta );
		codeConstantWriter.writeUnsignedShort( localVerificationTypes.size() );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.write( codeConstantWriter );
		codeConstantWriter.writeUnsignedShort( stackVerificationTypes.size() );
		for( VerificationType verificationType : stackVerificationTypes )
			verificationType.write( codeConstantWriter );
	}
}
