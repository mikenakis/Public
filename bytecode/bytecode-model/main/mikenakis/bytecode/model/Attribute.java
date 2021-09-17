package mikenakis.bytecode.model;

import mikenakis.bytecode.model.attributes.AnnotationDefaultAttribute;
import mikenakis.bytecode.model.attributes.AnnotationsAttribute;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.attributes.ConstantValueAttribute;
import mikenakis.bytecode.model.attributes.DeprecatedAttribute;
import mikenakis.bytecode.model.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import mikenakis.bytecode.model.attributes.NestHostAttribute;
import mikenakis.bytecode.model.attributes.NestMembersAttribute;
import mikenakis.bytecode.model.attributes.ParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeInvisibleAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeInvisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeVisibleAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeVisibleParameterAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.SignatureAttribute;
import mikenakis.bytecode.model.attributes.SourceFileAttribute;
import mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import mikenakis.bytecode.model.attributes.SyntheticAttribute;
import mikenakis.bytecode.model.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents an "Attribute" in a java class file.
 * <p>
 * Predefined attribute names:
 * (+ = must be recognized and correctly read)
 * <pre>
 * + ConstantValue
 * + Code
 * + StackMapTable
 * + Exceptions
 * + InnerClasses
 * + EnclosingMethod
 * + Synthetic
 * + Signature
 *   SourceFile
 *   SourceDebugExtension
 *   LineNumberTable
 *   LocalVariableTable
 *   LocalVariableTypeTable
 *   Deprecated
 * + RuntimeVisibleAnnotations
 * + RuntimeInvisibleAnnotations
 * + RuntimeVisibleParameterAnnotations
 * + RuntimeInvisibleParameterAnnotations
 * + AnnotationDefault
 * + BootstrapMethods
 * </pre>
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Attribute
{
	public static class Kind
	{
		public final String name;
		public final Utf8Constant utf8Name;

		public Kind( String name )
		{
			this.name = name;
			utf8Name = Utf8Constant.of( name );
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return name;
		}
	}

	public final Kind kind;

	protected Attribute( Kind kind )
	{
		this.kind = kind;
	}

	@ExcludeFromJacocoGeneratedReport public AnnotationDefaultAttribute                    /**/ asAnnotationDefaultAttribute                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public BootstrapMethodsAttribute                     /**/ asBootstrapMethodsAttribute                     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public CodeAttribute                                 /**/ asCodeAttribute                                 /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ConstantValueAttribute                        /**/ asConstantValueAttribute                        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public DeprecatedAttribute                           /**/ asDeprecatedAttribute                           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EnclosingMethodAttribute                      /**/ asEnclosingMethodAttribute                      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ExceptionsAttribute                           /**/ asExceptionsAttribute                           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InnerClassesAttribute                         /**/ asInnerClassesAttribute                         /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NestHostAttribute                             /**/ asNestHostAttribute                             /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NestMembersAttribute                          /**/ asNestMembersAttribute                          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LineNumberTableAttribute                      /**/ asLineNumberTableAttribute                      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTableAttribute                   /**/ asLocalVariableTableAttribute                   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LocalVariableTypeTableAttribute               /**/ asLocalVariableTypeTableAttribute               /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodParametersAttribute                     /**/ asMethodParametersAttribute                     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationsAttribute                          /**/ asAnnotationsAttribute                          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleAnnotationsAttribute            /**/ asRuntimeVisibleAnnotationsAttribute            /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleAnnotationsAttribute          /**/ asRuntimeInvisibleAnnotationsAttribute          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ParameterAnnotationsAttribute                 /**/ asParameterAnnotationsAttribute                 /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleParameterAnnotationsAttribute /**/ asRuntimeInvisibleParameterAnnotationsAttribute /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleParameterAnnotationsAttribute   /**/ asRuntimeVisibleParameterAnnotationsAttribute   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public TypeAnnotationsAttribute                      /**/ asTypeAnnotationsAttribute                      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeInvisibleTypeAnnotationsAttribute      /**/ asRuntimeInvisibleTypeAnnotationsAttribute      /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public RuntimeVisibleTypeAnnotationsAttribute        /**/ asRuntimeVisibleTypeAnnotationsAttribute        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SignatureAttribute                            /**/ asSignatureAttribute                            /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SourceFileAttribute                           /**/ asSourceFileAttribute                           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public StackMapTableAttribute                        /**/ asStackMapTableAttribute                        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SyntheticAttribute                            /**/ asSyntheticAttribute                            /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public UnknownAttribute                              /**/ asUnknownAttribute                              /**/() { return Kit.fail(); }
}
