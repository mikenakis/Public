package mikenakis.bytecode.attributes.stackmap;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A Stack Map Frame.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Frame extends Printable
{
	public abstract static class Kind
	{
		public final String name;

		protected Kind( String name )
		{
			this.name = name;
		}

		public abstract Frame newFrame( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int frameType, BufferReader bufferReader );
	}

	public static Frame parse( CodeAttribute codeAttribute, Optional<Frame> previousFrame, BufferReader bufferReader )
	{
		int frameType = bufferReader.readUnsignedByte();
		Kind kind = getKindFromFrameType( frameType );
		return kind.newFrame( codeAttribute, previousFrame, frameType, bufferReader );
	}

	public static Kind getKindFromFrameType( int frameType )
	{
		if( frameType <= 63 )
			return SameFrame.KIND;
		if( frameType <= 127 )
			return SameLocals1StackItemFrame.KIND;
		if( frameType <= 246 )
			return SameFrame.KIND; //Unknown
		if( frameType == 247 )
			return SameLocals1StackItemFrame.KIND; //Extended
		if( frameType <= 250 )
			return ChopFrame.KIND;
		if( frameType == 251 )
			return SameFrame.KIND; //Extended
		if( frameType <= 254 )
			return AppendFrame.KIND;
		assert frameType == 255;
		return FullFrame.KIND;
	}

	protected static Instruction findTargetInstruction( CodeAttribute codeAttribute, Optional<Frame> previousFrame, int offsetDelta )
	{
		int pc = offsetDelta + (previousFrame.isEmpty() ? 0 : previousFrame.get().targetInstruction.getPc() + 1);
		return codeAttribute.getInstructionByPc( pc );
	}

	public final Kind kind;
	public final Instruction targetInstruction;

	protected Frame( Kind kind, Instruction targetInstruction )
	{
		assert kind != null;
		assert targetInstruction != null;
		this.kind = kind;
		this.targetInstruction = targetInstruction;
	}

	public void intern( ConstantPool constantPool )
	{
		/* by default, nothing to do. */
	}

	@OverridingMethodsMustInvokeSuper
	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		targetInstructionConsumer.accept( targetInstruction );
	}

	public abstract void write( Optional<Frame> previousFrame, ConstantPool constantPool, BufferWriter bufferWriter );

	public abstract String getName( Optional<Frame> previousFrame );

	public final int getOffsetDelta( Optional<Frame> previousFrame )
	{
		int offset = targetInstruction.getPc();
		if( previousFrame.isEmpty() )
			return offset;
		return offset - previousFrame.get().targetInstruction.getPc() - 1;
	}
}
