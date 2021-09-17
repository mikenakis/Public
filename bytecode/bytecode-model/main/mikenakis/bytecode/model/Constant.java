package mikenakis.bytecode.model;

import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.NameAndTypeConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.exceptions.UnknownConstantException;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a java class file constant.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Constant
{
	public static String getTagNameByTag( int tag )
	{
		return switch( tag )
			{
				case Utf8Constant.TAG -> Utf8Constant.tagName;
				case IntegerConstant.TAG -> IntegerConstant.tagName;
				case FloatConstant.TAG -> FloatConstant.tagName;
				case LongConstant.TAG -> LongConstant.tagName;
				case DoubleConstant.TAG -> DoubleConstant.tagName;
				case ClassConstant.TAG -> ClassConstant.tagName;
				case StringConstant.TAG -> StringConstant.tagName;
				case FieldReferenceConstant.TAG -> FieldReferenceConstant.tagName;
				case PlainMethodReferenceConstant.TAG -> PlainMethodReferenceConstant.tagName;
				case InterfaceMethodReferenceConstant.TAG -> InterfaceMethodReferenceConstant.tagName;
				case NameAndTypeConstant.TAG -> NameAndTypeConstant.tagName;
				case MethodHandleConstant.TAG -> MethodHandleConstant.tagName;
				case MethodTypeConstant.TAG -> MethodTypeConstant.tagName;
				case InvokeDynamicConstant.TAG -> InvokeDynamicConstant.tagName;
				default -> throw new UnknownConstantException( tag );
			};
	}

	public final int tag;

	protected Constant( int tag )
	{
		this.tag = tag;
	}

	@Override public abstract boolean equals( Object other );

	@Override public abstract int hashCode();

	@ExcludeFromJacocoGeneratedReport public <T extends Comparable<T>> ValueConstant<T> /**/ asValueConstant                    /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public Utf8Constant                               /**/ asUtf8Constant                     /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public IntegerConstant                            /**/ asIntegerConstant                  /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FloatConstant                              /**/ asFloatConstant                    /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LongConstant                               /**/ asLongConstant                     /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public DoubleConstant                             /**/ asDoubleConstant                   /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassConstant                              /**/ asClassConstant                    /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public StringConstant                             /**/ asStringConstant                   /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ReferenceConstant                          /**/ asReferenceConstant                /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FieldReferenceConstant                     /**/ asFieldReferenceConstant           /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodReferenceConstant                    /**/ asMethodReferenceConstant          /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public PlainMethodReferenceConstant               /**/ asPlainMethodReferenceConstant     /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InterfaceMethodReferenceConstant           /**/ asInterfaceMethodReferenceConstant /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NameAndTypeConstant                        /**/ asNameAndTypeConstant              /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodHandleConstant                       /**/ asMethodHandleConstant             /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodTypeConstant                         /**/ asMethodTypeConstant               /**/ () { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InvokeDynamicConstant                      /**/ asInvokeDynamicConstant            /**/ () { return Kit.fail(); }
}
