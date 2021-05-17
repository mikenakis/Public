package mikenakis.bytecode.attributes.stackmap.verification;

import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.LazyInitializer;
import mikenakis.bytecode.kit.Printable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Verification Type.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class VerificationType extends Printable
{
	public abstract static class Kind
	{
		public final int tag;
		public final String name;

		protected Kind( int tag, String name )
		{
			this.tag = tag;
			this.name = name;
		}

		public abstract VerificationType newVerificationType( CodeAttribute codeAttribute, BufferReader bufferReader );
	}

	private static final LazyInitializer<List<Kind>> KINDS = new LazyInitializer<>( () ->
	{
		List<Kind> result = List.of(
			SimpleVerificationType.TOP_KIND,
			SimpleVerificationType.INTEGER_KIND,
			SimpleVerificationType.FLOAT_KIND,
			SimpleVerificationType.DOUBLE_KIND,
			SimpleVerificationType.LONG_KIND,
			SimpleVerificationType.NULL_KIND,
			SimpleVerificationType.UNINITIALIZED_THIS_KIND,
			ObjectVerificationType.KIND,
			UninitializedVerificationType.KIND );
		assert IntStream.range( 0, result.size() ).filter( i -> result.get( i ).tag != i ).count() == 0;
		return result;
	} );

	public static VerificationType parse( CodeAttribute codeAttribute, BufferReader bufferReader )
	{
		int tag = bufferReader.readUnsignedByte();
		Kind kind = KINDS.get().get( tag );
		assert kind.tag == tag;
		return kind.newVerificationType( codeAttribute, bufferReader );
	}

	public final Kind kind;

	protected VerificationType( Kind kind )
	{
		this.kind = kind;
	}

	public abstract void intern( ConstantPool constantPool );

	@OverridingMethodsMustInvokeSuper
	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( kind.tag );
	}

	public void collectTargets( Consumer<Instruction> targetInstructionConsumer )
	{
		/* by default, nothing to do */
	}

	@Override @OverridingMethodsMustInvokeSuper
	public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "kind = " ).append( kind.name );
	}
}
