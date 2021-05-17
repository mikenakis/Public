package mikenakis.bytecode;

import mikenakis.bytecode.annotationvalues.AnnotationAnnotationValue;
import mikenakis.bytecode.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.annotationvalues.ClassAnnotationValue;
import mikenakis.bytecode.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.annotationvalues.EnumAnnotationValue;
import mikenakis.bytecode.exceptions.UnknownValueException;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * Represents a value of an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class AnnotationValue extends Printable
{
	public interface Factory
	{
		AnnotationValue create( Runnable observer );
	}

	public static AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader )
	{
		int tag = bufferReader.readUnsignedByte();
		Kind kind = fromTag( tag );
		return kind.parse( observer, constantPool, bufferReader );
	}

	public abstract static class Kind
	{
		public final int tag;
		public final String name;

		protected Kind( int tag, String name )
		{
			this.tag = tag;
			this.name = name;
		}

		public abstract AnnotationValue parse( Runnable observer, ConstantPool constantPool, BufferReader bufferReader );
	}

	public static Kind fromTag( int tag )
	{
		switch( tag )
		{
			//@formatter:off
			case 'B': return ConstAnnotationValue.BYTE_KIND;
			case 'C': return ConstAnnotationValue.CHARACTER_KIND;
			case 'D': return ConstAnnotationValue.DOUBLE_KIND;
			case 'F': return ConstAnnotationValue.FLOAT_KIND;
			case 'I': return ConstAnnotationValue.INT_KIND;
			case 'J': return ConstAnnotationValue.LONG_KIND;
			case 'S': return ConstAnnotationValue.SHORT_KIND;
			case 'Z': return ConstAnnotationValue.BOOLEAN_KIND;
			case 's': return ConstAnnotationValue.STRING_KIND;
			case 'e': return EnumAnnotationValue.KIND;
			case 'c': return ClassAnnotationValue.KIND;
			case '@': return AnnotationAnnotationValue.KIND;
			case '[': return ArrayAnnotationValue.KIND;
			//@formatter:on
			default:
				throw new UnknownValueException( tag );
		}
	}

	private final Runnable observer;
	public final Kind kind;

	protected AnnotationValue( Runnable observer, Kind kind )
	{
		this.observer = observer;
		this.kind = kind;
	}

	public abstract void intern( ConstantPool constantPool );

	@OverridingMethodsMustInvokeSuper
	public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( kind.tag );
	}

	//@formatter:off
	public final ConstAnnotationValue      asConstAnnotationValue()       { return tryAsConstAnnotationValue()     .orElseThrow(); }
	public final EnumAnnotationValue       asEnumAnnotationValue()        { return tryAsEnumAnnotationValue()      .orElseThrow(); }
	public final ClassAnnotationValue      asClassAnnotationValue()       { return tryAsClassAnnotationValue()     .orElseThrow(); }
	public final AnnotationAnnotationValue asAnnotationAnnotationValue()  { return tryAsAnnotationAnnotationValue().orElseThrow(); }
	public final ArrayAnnotationValue      asArrayAnnotationValue()       { return tryAsArrayAnnotationValue()     .orElseThrow(); }
	//@formatter:on

	//@formatter:off
	public Optional<ConstAnnotationValue>      tryAsConstAnnotationValue()       { return Optional.empty(); }
	public Optional<EnumAnnotationValue>       tryAsEnumAnnotationValue()        { return Optional.empty(); }
	public Optional<ClassAnnotationValue>      tryAsClassAnnotationValue()       { return Optional.empty(); }
	public Optional<AnnotationAnnotationValue> tryAsAnnotationAnnotationValue()  { return Optional.empty(); }
	public Optional<ArrayAnnotationValue>      tryAsArrayAnnotationValue()       { return Optional.empty(); }
	//@formatter:on

	//@formatter:off
	public boolean isConstAnnotationValue()       { return tryAsConstAnnotationValue()     .isPresent(); }
	public boolean isEnumAnnotationValue()        { return tryAsEnumAnnotationValue()      .isPresent(); }
	public boolean isClassAnnotationValue()       { return tryAsClassAnnotationValue()     .isPresent(); }
	public boolean isAnnotationAnnotationValue()  { return tryAsAnnotationAnnotationValue().isPresent(); }
	public boolean isArrayAnnotationValue()       { return tryAsArrayAnnotationValue()     .isPresent(); }
	//@formatter:on

	protected void markAsDirty()
	{
		observer.run();
	}
}
