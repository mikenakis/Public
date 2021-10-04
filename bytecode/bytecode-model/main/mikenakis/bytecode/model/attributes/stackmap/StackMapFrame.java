package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.kit.Kit;

import java.util.Optional;

/**
 * A Stack Map Frame.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class StackMapFrame
{
	public static final int tag_Same                         /**/ = 1;
	public static final int tag_SameExtended                 /**/ = 2;
	public static final int tag_SameLocals1StackItem         /**/ = 3;
	public static final int tag_SameLocals1StackItemExtended /**/ = 4;
	public static final int tag_Chop                         /**/ = 5;
	public static final int tag_Append                       /**/ = 6;
	public static final int tag_Full                         /**/ = 7;

	public static int getTagFromType( int frameType )
	{
		if( frameType <= 63 )
			return tag_Same;
		if( frameType <= 127 )
			return tag_SameLocals1StackItem;
		if( frameType <= 246 )
			return tag_Same; //Unknown
		if( frameType == 247 )
			return tag_SameLocals1StackItemExtended;
		if( frameType <= 250 )
			return tag_Chop;
		if( frameType == 251 )
			return tag_SameExtended;
		if( frameType <= 254 )
			return tag_Append;
		assert frameType == 255;
		return tag_Full;
	}

	public final int tag;
	private Instruction targetInstruction;

	protected StackMapFrame( int tag, Instruction targetInstruction )
	{
		assert targetInstruction != null;
		this.tag = tag;
		this.targetInstruction = targetInstruction;
	}

	public String getTypeName()
	{
		return getTypeNameFromTag( tag );
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

	public AppendStackMapFrame               /**/ asAppendStackMapFrame()               /**/ { return Kit.fail(); }
	public ChopStackMapFrame                 /**/ asChopStackMapFrame()                 /**/ { return Kit.fail(); }
	public FullStackMapFrame                 /**/ asFullStackMapFrame()                 /**/ { return Kit.fail(); }
	public SameStackMapFrame                 /**/ asSameStackMapFrame()                 /**/ { return Kit.fail(); }
	public SameLocals1StackItemStackMapFrame /**/ asSameLocals1StackItemStackMapFrame() /**/ { return Kit.fail(); }

	public abstract String getName( Optional<StackMapFrame> previousFrame );

	private static String getTypeNameFromTag( int tag )
	{
		return switch( tag )
			{
				case tag_Same -> SameStackMapFrame.typeName;
				case tag_SameExtended -> SameStackMapFrame.extendedTypeName;
				case tag_SameLocals1StackItem -> SameLocals1StackItemStackMapFrame.typeName;
				case tag_SameLocals1StackItemExtended -> SameLocals1StackItemStackMapFrame.extendedTypeName;
				case tag_Chop -> ChopStackMapFrame.typeName;
				case tag_Append -> AppendStackMapFrame.typeName;
				case tag_Full -> FullStackMapFrame.typeName;
				default -> throw new AssertionError( tag );
			};
	}
}
