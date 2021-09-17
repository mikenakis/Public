package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;

import java.util.Optional;

/**
 * A Stack Map Frame.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class StackMapFrame
{
	public static String getTypeNameFromType( int frameType )
	{
		if( frameType <= 63 )
			return SameStackMapFrame.typeName;
		if( frameType <= 127 )
			return SameLocals1StackItemStackMapFrame.typeName;
		if( frameType <= 246 )
			return SameStackMapFrame.typeName; //Unknown
		if( frameType == 247 )
			return SameLocals1StackItemStackMapFrame.extendedTypeName;
		if( frameType <= 250 )
			return ChopStackMapFrame.typeName;
		if( frameType == 251 )
			return SameStackMapFrame.extendedTypeName;
		if( frameType <= 254 )
			return AppendStackMapFrame.typeName;
		assert frameType == 255;
		return FullStackMapFrame.typeName;
	}

	public final String typeName;
	private Instruction targetInstruction;

	protected StackMapFrame( String typeName, Instruction targetInstruction )
	{
		assert targetInstruction != null;
		this.typeName = typeName;
		this.targetInstruction = targetInstruction;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public Instruction getTargetInstruction()
	{
		assert targetInstruction != null;
		return targetInstruction;
	}

	public void setTargetInstruction( Instruction targetInstruction )
	{
		assert targetInstruction != null;
		this.targetInstruction = targetInstruction;
	}

	//@formatter:off
	public Optional<AppendStackMapFrame>               tryAsAppendStackMapFrame()               { return Optional.empty(); }
	public Optional<ChopStackMapFrame>                 tryAsChopStackMapFrame()                 { return Optional.empty(); }
	public Optional<FullStackMapFrame>                 tryAsFullStackMapFrame()                 { return Optional.empty(); }
	public Optional<SameStackMapFrame>                 tryAsSameStackMapFrame()                 { return Optional.empty(); }
	public Optional<SameLocals1StackItemStackMapFrame> tryAsSameLocals1StackItemStackMapFrame() { return Optional.empty(); }

	public final AppendStackMapFrame               asAppendStackMapFrame()               { return tryAsAppendStackMapFrame()              .orElseThrow(); }
	public final ChopStackMapFrame                 asChopStackMapFrame()                 { return tryAsChopStackMapFrame()                .orElseThrow(); }
	public final FullStackMapFrame                 asFullStackMapFrame()                 { return tryAsFullStackMapFrame()                .orElseThrow(); }
	public final SameStackMapFrame                 asSameStackMapFrame()                 { return tryAsSameStackMapFrame()                .orElseThrow(); }
	public final SameLocals1StackItemStackMapFrame asSameLocals1StackItemStackMapFrame() { return tryAsSameLocals1StackItemStackMapFrame().orElseThrow(); }

	public final boolean isAppendStackMapFrame()               { return tryAsAppendStackMapFrame()              .isPresent(); }
	public final boolean isChopStackMapFrame()                 { return tryAsChopStackMapFrame()                .isPresent(); }
	public final boolean isFullStackMapFrame()                 { return tryAsFullStackMapFrame()                .isPresent(); }
	public final boolean isSameStackMapFrame()                 { return tryAsSameStackMapFrame()                .isPresent(); }
	public final boolean isSameLocals1StackItemStackMapFrame() { return tryAsSameLocals1StackItemStackMapFrame().isPresent(); }
	//@formatter:on

	public abstract String getName( Optional<StackMapFrame> previousFrame );
}
