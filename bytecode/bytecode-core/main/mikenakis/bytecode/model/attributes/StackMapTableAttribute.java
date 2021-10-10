package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.AppendStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.ChopStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.FullStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.SameLocals1StackItemStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.SameStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.StackMapFrame;
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
 * Represents the "StackMapTable" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link CodeAttribute}
 * <p>
 * For a discussion of why this is lame, see:
 * <p>
 * http://chrononsystems.com/blog/java-7-design-flaw-leads-to-huge-backward-step-for-the-jvm
 * <p>
 * If it cannot be made to work, there is always the option of launching java with {@code -noverify}.
 *
 * @author michael.gr
 */
public final class StackMapTableAttribute extends KnownAttribute
{
	public static StackMapTableAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap )
	{
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		List<StackMapFrame> frames = new ArrayList<>( count );
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( int i = 0; i < count; i++ )
		{
			int frameType = bufferReader.readUnsignedByte();
			int stackMapFrameTag = StackMapFrame.getTagFromType( frameType );
			StackMapFrame frame = switch( stackMapFrameTag )
				{
					case StackMapFrame.tag_Same, StackMapFrame.tag_SameExtended -> SameStackMapFrame.read( bufferReader, locationMap, previousFrame, frameType );
					case StackMapFrame.tag_SameLocals1StackItem, StackMapFrame.tag_SameLocals1StackItemExtended -> SameLocals1StackItemStackMapFrame.read( bufferReader, constantPool, locationMap, previousFrame, frameType );
					case StackMapFrame.tag_Chop -> ChopStackMapFrame.read( bufferReader, locationMap, previousFrame, frameType );
					case StackMapFrame.tag_Append -> AppendStackMapFrame.read( bufferReader, constantPool, locationMap, previousFrame, frameType );
					case StackMapFrame.tag_Full -> FullStackMapFrame.read( bufferReader, constantPool, locationMap, previousFrame );
					default -> throw new AssertionError( frameType );
				};
			frames.add( frame );
			previousFrame = Optional.of( frame );
		}
		return of( frames );
	}

	public static StackMapTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static StackMapTableAttribute of( List<StackMapFrame> frames )
	{
		return new StackMapTableAttribute( frames );
	}

	private final List<StackMapFrame> frames;

	private StackMapTableAttribute( List<StackMapFrame> frames )
	{
		super( tag_StackMapTable );
		this.frames = frames;
	}

	public List<StackMapFrame> frames()
	{
		return Collections.unmodifiableList( frames );
	}

	private <T extends StackMapFrame> T addStackMapFrame( T stackMapFrame )
	{
		frames.add( stackMapFrame );
		return stackMapFrame;
	}

	public SameStackMapFrame addSameFrame( Instruction targetInstruction )
	{
		SameStackMapFrame frame = SameStackMapFrame.of( targetInstruction );
		return addStackMapFrame( frame );
	}

	public SameLocals1StackItemStackMapFrame addSameLocals1StackItemFrame( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		SameLocals1StackItemStackMapFrame frame = SameLocals1StackItemStackMapFrame.of( targetInstruction, stackVerificationType );
		return addStackMapFrame( frame );
	}

	public ChopStackMapFrame addChopFrame( Instruction targetInstruction, int count )
	{
		ChopStackMapFrame frame = ChopStackMapFrame.of( targetInstruction, count );
		return addStackMapFrame( frame );
	}

	public AppendStackMapFrame addAppendFrame( Instruction targetInstruction, VerificationType... localVerificationTypes )
	{
		return addAppendFrame( targetInstruction, List.of( localVerificationTypes ) );
	}

	public AppendStackMapFrame addAppendFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		AppendStackMapFrame frame = AppendStackMapFrame.of( targetInstruction, localVerificationTypes );
		return addStackMapFrame( frame );
	}

	public FullStackMapFrame addFullFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		FullStackMapFrame frame = FullStackMapFrame.of( targetInstruction, localVerificationTypes, stackVerificationTypes );
		return addStackMapFrame( frame );
	}

	@Deprecated @Override public StackMapTableAttribute asStackMapTableAttribute() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return frames.size() + " entries"; }

	@Override public void intern( Interner interner )
	{
		for( StackMapFrame frame : frames )
			frame.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( frames.size() );
		Optional<StackMapFrame> previousFrame = Optional.empty();
		for( StackMapFrame frame : frames )
		{
			frame.write( bufferWriter, constantPool, locationMap.orElseThrow(), previousFrame );
			previousFrame = Optional.of( frame );
		}
	}
}
