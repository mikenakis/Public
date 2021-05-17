package mikenakis.bytecode;

import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.DoubleConstant;
import mikenakis.bytecode.constants.FieldReferenceConstant;
import mikenakis.bytecode.constants.FloatConstant;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.constants.InvokeDynamicConstant;
import mikenakis.bytecode.constants.LongConstant;
import mikenakis.bytecode.constants.MethodHandleConstant;
import mikenakis.bytecode.constants.MethodReferenceConstant;
import mikenakis.bytecode.constants.MethodTypeConstant;
import mikenakis.bytecode.constants.NameAndTypeConstant;
import mikenakis.bytecode.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.constants.ReferenceConstant;
import mikenakis.bytecode.constants.StringConstant;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.bytecode.constants.ValueConstant;
import mikenakis.bytecode.exceptions.UnknownConstantException;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.kit.Printable;
import mikenakis.kit.Kit;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Represents a java class file constant.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Constant extends Printable
{
	static ConstantKind getKindByTag( int tag )
	{
		switch( tag )
		{
			//@formatter:off
			case Utf8Constant.TAG:   				     return Utf8Constant.KIND;
			case IntegerConstant.TAG:				     return IntegerConstant.KIND;
			case FloatConstant.TAG: 				     return FloatConstant.KIND;
			case LongConstant.TAG:  				     return LongConstant.KIND;
			case DoubleConstant.TAG:				     return DoubleConstant.KIND;
			case ClassConstant.TAG: 				     return ClassConstant.KIND;
			case StringConstant.TAG:				     return StringConstant.KIND;
			case FieldReferenceConstant.TAG:		     return FieldReferenceConstant.KIND;
			case PlainMethodReferenceConstant.TAG:	     return PlainMethodReferenceConstant.KIND;
			case InterfaceMethodReferenceConstant.TAG:   return InterfaceMethodReferenceConstant.KIND;
			case NameAndTypeConstant.TAG:  				 return NameAndTypeConstant.KIND;
			case MethodHandleConstant.TAG: 				 return MethodHandleConstant.KIND;
			case MethodTypeConstant.TAG:   				 return MethodTypeConstant.KIND;
			case InvokeDynamicConstant.TAG:				 return InvokeDynamicConstant.KIND;
			//@formatter:on
			default:
				throw new UnknownConstantException( tag );
		}
	}

	public final ConstantKind kind;

	protected Constant( ConstantKind kind )
	{
		this.kind = kind;
	}

	@Override public abstract boolean equals( Object other );

	@Override public abstract int hashCode();

	@OverridingMethodsMustInvokeSuper public void intern( ConstantPool constantPool )
	{
		constantPool.addConstantAndGetIndex( this );
	}

	public abstract void write( ConstantPool constantPool, BufferWriter bufferWriter );

	//@formatter:off
	public <T extends Comparable<T>> ValueConstant<T> asValueConstant                   () { return Kit.fail(); }
	public Utf8Constant                               asUtf8Constant                    () { return Kit.fail(); }
	public IntegerConstant                            asIntegerConstant                 () { return Kit.fail(); }
	public FloatConstant                              asFloatConstant                   () { return Kit.fail(); }
	public LongConstant                               asLongConstant                    () { return Kit.fail(); }
	public DoubleConstant                             asDoubleConstant                  () { return Kit.fail(); }
	public ClassConstant                              asClassConstant                   () { return Kit.fail(); }
	public StringConstant                             asStringConstant                  () { return Kit.fail(); }
	public ReferenceConstant                          asReferenceConstant               () { return Kit.fail(); }
	public FieldReferenceConstant                     asFieldReferenceConstant          () { return Kit.fail(); }
	public MethodReferenceConstant                    asMethodReferenceConstant         () { return Kit.fail(); }
	public PlainMethodReferenceConstant               asPlainMethodReferenceConstant    () { return Kit.fail(); }
	public InterfaceMethodReferenceConstant           asInterfaceMethodReferenceConstant() { return Kit.fail(); }
	public NameAndTypeConstant                        asNameAndTypeConstant             () { return Kit.fail(); }
	public MethodHandleConstant                       asMethodHandleConstant            () { return Kit.fail(); }
	public MethodTypeConstant                         asMethodTypeConstant              () { return Kit.fail(); }
	public InvokeDynamicConstant                      asInvokeDynamicConstant           () { return Kit.fail(); }
	//@formatter:on

	public void writeIndex( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		int constantIndex = constantPool.getIndex( this );
		bufferWriter.writeUnsignedShort( constantIndex );
	}
}
