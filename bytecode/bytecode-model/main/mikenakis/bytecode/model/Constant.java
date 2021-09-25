package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.InvalidConstantTagException;
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
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a java class file constant.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Constant
{
	public static final int tagMutf8                    /**/ = 1; // JVMS::CONSTANT_Utf8 addresses JVMS::CONSTANT_Utf8_info
	public static final int tagInteger                  /**/ = 3; // JVMS::CONSTANT_Integer addresses JVMS::CONSTANT_Integer_info
	public static final int tagFloat                    /**/ = 4; // JVMS::CONSTANT_Float addresses JVMS::CONSTANT_Float_info
	public static final int tagLong                     /**/ = 5; // JVMS::CONSTANT_Long addresses JVMS::CONSTANT_Long_info
	public static final int tagDouble                   /**/ = 6; // JVMS::CONSTANT_Double addresses JVMS::CONSTANT_Double_info
	public static final int tagClass                    /**/ = 7; // JVMS::CONSTANT_Class addresses JVMS::CONSTANT_Class_info
	public static final int tagString                   /**/ = 8; // JVMS::CONSTANT_String addresses JVMS::CONSTANT_String_info
	public static final int tagFieldReference           /**/ = 9; // JVMS::CONSTANT_Fieldref addresses JVMS::CONSTANT_Fieldref_info
	public static final int tagMethodReference          /**/ = 10; // JVMS::CONSTANT_MethodRef addresses JVMS::CONSTANT_MethodRef_info
	public static final int tagInterfaceMethodReference /**/ = 11; // JVMS::CONSTANT_InterfaceMethodRef addresses JVMS::CONSTANT_InterfaceMethodRef_info
	public static final int tagNameAndDescriptor        /**/ = 12; // JVMS::CONSTANT_NameAndType addresses JVMS::CONSTANT_NameAndType_info
	public static final int tagMethodHandle             /**/ = 15; // JVMS::CONSTANT_MethodHandle addresses JVMS::CONSTANT_MethodHandle_info
	public static final int tagMethodType               /**/ = 16; // JVMS::CONSTANT_MethodType addresses JVMS::CONSTANT_MethodType_info
	public static final int tagInvokeDynamic            /**/ = 18; // JVMS::CONSTANT_InvokeDynamic addresses JVMS::CONSTANT_InvokeDynamic_info

	public static String tagName( int constantTag )
	{
		return switch( constantTag )
			{
				case tagMutf8                    /**/ -> "Mutf8";
				case tagInteger                  /**/ -> "Integer";
				case tagFloat                    /**/ -> "Float";
				case tagLong                     /**/ -> "Long";
				case tagDouble                   /**/ -> "Double";
				case tagClass                    /**/ -> "Class";
				case tagString                   /**/ -> "String";
				case tagFieldReference           /**/ -> "FieldReference";
				case tagMethodReference          /**/ -> "MethodReference";
				case tagInterfaceMethodReference /**/ -> "InterfaceMethodReference";
				case tagNameAndDescriptor        /**/ -> "NameAndDescriptor";
				case tagMethodHandle             /**/ -> "MethodHandle";
				case tagMethodType               /**/ -> "MethodType";
				case tagInvokeDynamic            /**/ -> "InvokeDynamic";
				default -> throw new InvalidConstantTagException( constantTag );
			};
	}

	public final int tag;

	protected Constant( int tag )
	{
		this.tag = tag;
	}

	@Override public abstract boolean equals( Object other );

	@Override public abstract int hashCode();

	@ExcludeFromJacocoGeneratedReport public <T extends Comparable<T>> ValueConstant<T> /**/ asValueConstant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public Mutf8Constant                              /**/ asMutf8Constant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public IntegerConstant                            /**/ asIntegerConstant                  /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FloatConstant                              /**/ asFloatConstant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LongConstant                               /**/ asLongConstant                     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public DoubleConstant                             /**/ asDoubleConstant                   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassConstant                              /**/ asClassConstant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public StringConstant                             /**/ asStringConstant                   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ReferenceConstant                          /**/ asReferenceConstant                /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FieldReferenceConstant                     /**/ asFieldReferenceConstant           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodReferenceConstant                    /**/ asMethodReferenceConstant          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public PlainMethodReferenceConstant               /**/ asPlainMethodReferenceConstant     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InterfaceMethodReferenceConstant           /**/ asInterfaceMethodReferenceConstant /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NameAndDescriptorConstant                  /**/ asNameAndDescriptorConstant        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodHandleConstant                       /**/ asMethodHandleConstant             /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodTypeConstant                         /**/ asMethodTypeConstant               /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InvokeDynamicConstant                      /**/ asInvokeDynamicConstant            /**/() { return Kit.fail(); }
}
