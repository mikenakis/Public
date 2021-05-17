package mikenakis.bytecode.attributes;

import mikenakis.bytecode.Attribute;
import mikenakis.bytecode.Attributes;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.attributes.stackmap.AppendFrame;
import mikenakis.bytecode.attributes.stackmap.ChopFrame;
import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.attributes.stackmap.FullFrame;
import mikenakis.bytecode.attributes.stackmap.SameFrame;
import mikenakis.bytecode.attributes.stackmap.SameLocals1StackItemFrame;
import mikenakis.bytecode.attributes.stackmap.verification.VerificationType;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
 * @author Michael Belivanakis (michael.gr)
 */
public final class StackMapTableAttribute extends Attribute
{
	public static final String NAME = "StackMapTable";

	public static Optional<StackMapTableAttribute> tryFrom( Attributes attributes )
	{
		return Attribute.tryFrom( attributes, NAME ).map( attribute -> attribute.asStackMapTableAttribute() );
	}

	private final CodeAttribute codeAttribute;
	public final ArrayList<Frame> frames = new ArrayList<>();

	public StackMapTableAttribute( Runnable observer, CodeAttribute codeAttribute )
	{
		super( observer, NAME );
		this.codeAttribute = codeAttribute;
	}

	public StackMapTableAttribute( Runnable observer, CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		super( observer, NAME );
		this.codeAttribute = codeAttribute;
		int count = bufferReader.readUnsignedShort();
		assert count > 0;
		frames.clear();
		frames.ensureCapacity( count );
		Optional<Frame> previousFrame = Optional.empty();
		for( int i = 0; i < count; i++ )
		{
			Frame frame = Frame.parse( codeAttribute, previousFrame, bufferReader );
			frames.add( frame );
			previousFrame = Optional.of( frame );
		}
	}

	@Override public void intern( ConstantPool constantPool )
	{
		for( Frame frame : frames )
			frame.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedShort( frames.size() );
		Optional<Frame> previousFrame = Optional.empty();
		for( Frame frame : frames )
		{
			frame.write( previousFrame, constantPool, bufferWriter );
			previousFrame = Optional.of( frame );
		}
	}

	@Override public Optional<StackMapTableAttribute> tryAsStackMapTableAttribute()
	{
		return Optional.of( this );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( frames.size() ).append( " entries" );
	}

	private <T extends Frame> T addStackMapFrame( T stackMapFrame )
	{
		frames.add( stackMapFrame );
		return stackMapFrame;
	}

	public SameFrame addSameFrame( Instruction targetInstruction )
	{
		SameFrame frame = new SameFrame( targetInstruction );
		return addStackMapFrame( frame );
	}

	public SameLocals1StackItemFrame addSameLocals1StackItemFrame( Instruction targetInstruction, VerificationType stackVerificationType )
	{
		SameLocals1StackItemFrame frame = new SameLocals1StackItemFrame( targetInstruction, stackVerificationType );
		return addStackMapFrame( frame );
	}

	public ChopFrame addChopFrame( Instruction targetInstruction, int count )
	{
		ChopFrame frame = new ChopFrame( targetInstruction, count );
		return addStackMapFrame( frame );
	}

	public AppendFrame addAppendFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes )
	{
		AppendFrame frame = new AppendFrame( targetInstruction, localVerificationTypes );
		return addStackMapFrame( frame );
	}

	public FullFrame addFullFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		FullFrame frame = new FullFrame( targetInstruction, localVerificationTypes, stackVerificationTypes );
		return addStackMapFrame( frame );
	}

	@Override public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		for( Frame frame : frames )
			frame.collectTargets( targetInstructionConsumer );
	}
}
