package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.AppendStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.ChopStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.FullStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.SameLocals1StackItemStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.SameStackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.StackMapFrame;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public static StackMapTableAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static StackMapTableAttribute of( List<StackMapFrame> frames )
	{
		return new StackMapTableAttribute( frames );
	}

	public static final String name = "StackMapTable";
	public static final Kind kind = new Kind( name );

	private final List<StackMapFrame> frames;

	private StackMapTableAttribute( List<StackMapFrame> frames )
	{
		super( kind );
		this.frames = frames;
	}

	public List<StackMapFrame> frames()
	{
		return Collections.unmodifiableList( frames );
	}

	@Deprecated @Override public StackMapTableAttribute asStackMapTableAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return frames.size() + " entries";
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
}
