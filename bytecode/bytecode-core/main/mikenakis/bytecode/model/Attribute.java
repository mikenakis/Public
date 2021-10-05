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
import mikenakis.bytecode.model.attributes.KnownAttribute;
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
import mikenakis.bytecode.model.constants.Mutf8Constant;
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
	public final Mutf8Constant mutf8Name;

	protected Attribute( Mutf8Constant mutf8Name )
	{
		this.mutf8Name = mutf8Name;
	}

	public abstract boolean isKnown();
	public boolean isOptional() { return false; }

	@ExcludeFromJacocoGeneratedReport public UnknownAttribute                              /**/ asUnknownAttribute                              /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public KnownAttribute                                /**/ asKnownAttribute                                /**/() { return Kit.fail(); }
}
