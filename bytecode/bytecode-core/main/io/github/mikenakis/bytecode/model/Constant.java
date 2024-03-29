package io.github.mikenakis.bytecode.model;

import io.github.mikenakis.bytecode.exceptions.InvalidConstantTagException;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.constants.ClassConstant;
import io.github.mikenakis.bytecode.model.constants.FieldReferenceConstant;
import io.github.mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import io.github.mikenakis.bytecode.model.constants.MethodHandleConstant;
import io.github.mikenakis.bytecode.model.constants.MethodReferenceConstant;
import io.github.mikenakis.bytecode.model.constants.MethodTypeConstant;
import io.github.mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import io.github.mikenakis.bytecode.model.constants.ReferenceConstant;
import io.github.mikenakis.bytecode.model.constants.ValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.DoubleValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.FloatValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.LongValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.StringValueConstant;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingBootstrapPool;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a java class file constant.
 *
 * @author michael.gr
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

	@ExcludeFromJacocoGeneratedReport public ValueConstant             /**/ asValueConstant             /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public Mutf8ValueConstant        /**/ asMutf8ValueConstant        /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public IntegerValueConstant      /**/ asIntegerValueConstant      /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public FloatValueConstant        /**/ asFloatValueConstant        /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public LongValueConstant         /**/ asLongValueConstant         /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public DoubleValueConstant       /**/ asDoubleValueConstant       /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public ClassConstant             /**/ asClassConstant             /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public StringValueConstant       /**/ asStringValueConstant       /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public ReferenceConstant         /**/ asReferenceConstant         /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public FieldReferenceConstant    /**/ asFieldReferenceConstant    /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public MethodReferenceConstant   /**/ asMethodReferenceConstant   /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public NameAndDescriptorConstant /**/ asNameAndDescriptorConstant /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public MethodHandleConstant      /**/ asMethodHandleConstant      /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public MethodTypeConstant        /**/ asMethodTypeConstant        /**/() { throw new AssertionError(); }
	@ExcludeFromJacocoGeneratedReport public InvokeDynamicConstant     /**/ asInvokeDynamicConstant     /**/() { throw new AssertionError(); }

	@Override public abstract boolean equals( Object other );
	@Override public abstract int hashCode();
	public abstract void intern( Interner interner );
	public abstract void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingBootstrapPool bootstrapPool );
}
