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
import java.util.List;
import java.util.Optional;

/**
 * 'Full' {@link StackMapFrame}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FullStackMapFrame extends StackMapFrame
{
	public static FullStackMapFrame read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap, Optional<StackMapFrame> previousFrame )
	{
		Instruction targetInstruction = findTargetInstruction( previousFrame, bufferReader.readUnsignedShort(), locationMap ).orElseThrow();
		List<VerificationType> localVerificationTypes = readVerificationTypes( bufferReader, constantPool, locationMap );
		List<VerificationType> stackVerificationTypes = readVerificationTypes( bufferReader, constantPool, locationMap );
		return of( targetInstruction, localVerificationTypes, stackVerificationTypes );
	}

	private static List<VerificationType> readVerificationTypes( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		List<VerificationType> verificationTypes = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			verificationTypes.add( VerificationType.read( bufferReader, constantPool, locationMap ) );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap, Optional<StackMapFrame> previousFrame )
	{
		int offsetDelta = locationMap.getOffsetDelta( this, previousFrame );
		bufferWriter.writeUnsignedByte( type );
		bufferWriter.writeUnsignedShort( offsetDelta );
		bufferWriter.writeUnsignedShort( localVerificationTypes.size() );
		for( VerificationType verificationType : localVerificationTypes )
			verificationType.write( bufferWriter, constantPool, locationMap );
		bufferWriter.writeUnsignedShort( stackVerificationTypes.size() );
		for( VerificationType verificationType : stackVerificationTypes )
			verificationType.write( bufferWriter, constantPool, locationMap );
	}
}
