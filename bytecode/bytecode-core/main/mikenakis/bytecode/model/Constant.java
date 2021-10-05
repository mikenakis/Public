package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.value.DoubleValueConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.value.FloatValueConstant;
import mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.value.LongValueConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.value.StringValueConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a java class file constant.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Constant
{
	public static final int tag_Mutf8                    /**/ = 1; // JVMS::CONSTANT_Utf8 addresses JVMS::CONSTANT_Utf8_info
	public static final int tag_Integer                  /**/ = 3; // JVMS::CONSTANT_Integer addresses JVMS::CONSTANT_Integer_info
	public static final int tag_Float                    /**/ = 4; // JVMS::CONSTANT_Float addresses JVMS::CONSTANT_Float_info
	public static final int tag_Long                     /**/ = 5; // JVMS::CONSTANT_Long addresses JVMS::CONSTANT_Long_info
	public static final int tag_Double                   /**/ = 6; // JVMS::CONSTANT_Double addresses JVMS::CONSTANT_Double_info
	public static final int tag_Class                    /**/ = 7; // JVMS::CONSTANT_Class addresses JVMS::CONSTANT_Class_info
	public static final int tag_String                   /**/ = 8; // JVMS::CONSTANT_String addresses JVMS::CONSTANT_String_info
	public static final int tag_FieldReference           /**/ = 9; // JVMS::CONSTANT_Fieldref addresses JVMS::CONSTANT_Fieldref_info
	public static final int tag_PlainMethodReference     /**/ = 10; // JVMS::CONSTANT_MethodRef addresses JVMS::CONSTANT_MethodRef_info
	public static final int tag_InterfaceMethodReference /**/ = 11; // JVMS::CONSTANT_InterfaceMethodRef addresses JVMS::CONSTANT_InterfaceMethodRef_info
	public static final int tag_NameAndDescriptor        /**/ = 12; // JVMS::CONSTANT_NameAndType addresses JVMS::CONSTANT_NameAndType_info
	public static final int tag_MethodHandle             /**/ = 15; // JVMS::CONSTANT_MethodHandle addresses JVMS::CONSTANT_MethodHandle_info
	public static final int tag_MethodType               /**/ = 16; // JVMS::CONSTANT_MethodType addresses JVMS::CONSTANT_MethodType_info
	public static final int tag_InvokeDynamic            /**/ = 18; // JVMS::CONSTANT_InvokeDynamic addresses JVMS::CONSTANT_InvokeDynamic_info

	public static String tagName( int constantTag )
	{
		return switch( constantTag )
			{
				case tag_Mutf8                    /**/ -> "Mutf8";
				case tag_Integer                  /**/ -> "Integer";
				case tag_Float                    /**/ -> "Float";
				case tag_Long                     /**/ -> "Long";
				case tag_Double                   /**/ -> "Double";
				case tag_Class                    /**/ -> "Class";
				case tag_String                   /**/ -> "String";
				case tag_FieldReference           /**/ -> "FieldReference";
				case tag_PlainMethodReference     /**/ -> "PlainMethodReference";
				case tag_InterfaceMethodReference /**/ -> "InterfaceMethodReference";
				case tag_NameAndDescriptor        /**/ -> "NameAndDescriptor";
				case tag_MethodHandle             /**/ -> "MethodHandle";
				case tag_MethodType               /**/ -> "MethodType";
				case tag_InvokeDynamic            /**/ -> "InvokeDynamic";
				default -> throw new InvalidConstantTagException( constantTag );
			};
	}

	public final int tag;

	protected Constant( int tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ValueConstant             /**/ asValueConstant             /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public Mutf8ValueConstant        /**/ asMutf8ValueConstant        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public IntegerValueConstant      /**/ asIntegerValueConstant      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FloatValueConstant        /**/ asFloatValueConstant        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LongValueConstant         /**/ asLongValueConstant         /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public DoubleValueConstant       /**/ asDoubleValueConstant       /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassConstant             /**/ asClassConstant             /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public StringValueConstant       /**/ asStringValueConstant       /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ReferenceConstant         /**/ asReferenceConstant         /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FieldReferenceConstant    /**/ asFieldReferenceConstant    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodReferenceConstant   /**/ asMethodReferenceConstant   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NameAndDescriptorConstant /**/ asNameAndDescriptorConstant /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodHandleConstant      /**/ asMethodHandleConstant      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodTypeConstant        /**/ asMethodTypeConstant        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InvokeDynamicConstant     /**/ asInvokeDynamicConstant     /**/() { return Kit.fail(); }

	@Override public abstract boolean equals( Object other );
	@Override public abstract int hashCode();
	public abstract void intern( Interner interner );
	public abstract void write( ConstantWriter constantWriter );
}
