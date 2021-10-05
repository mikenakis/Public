package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.InvalidKnownAttributeTagException;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.model.attributes.AnnotationDefaultAttribute;
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
import mikenakis.bytecode.model.attributes.UnknownAttribute;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

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
	public static Attribute read( AttributeReader attributeReader, Mutf8ValueConstant nameConstant, int attributeLength )
	{
		Optional<Integer> knownAttributeTag = KnownAttribute.tagFromName( nameConstant );
		if( knownAttributeTag.isPresent() )
		{
			return switch( knownAttributeTag.get() )
				{
					case KnownAttribute.tag_AnnotationDefault -> AnnotationDefaultAttribute.read( attributeReader );
					case KnownAttribute.tag_BootstrapMethods -> BootstrapMethodsAttribute.read( attributeReader );
					case KnownAttribute.tag_Code -> CodeAttribute.read( attributeReader );
					case KnownAttribute.tag_ConstantValue -> ConstantValueAttribute.read( attributeReader );
					case KnownAttribute.tag_Deprecated -> DeprecatedAttribute.of();
					case KnownAttribute.tag_EnclosingMethod -> EnclosingMethodAttribute.read( attributeReader );
					case KnownAttribute.tag_Exceptions -> ExceptionsAttribute.read( attributeReader );
					case KnownAttribute.tag_InnerClasses -> InnerClassesAttribute.read( attributeReader );
					case KnownAttribute.tag_LineNumberTable -> LineNumberTableAttribute.read( attributeReader );
					case KnownAttribute.tag_LocalVariableTable -> LocalVariableTableAttribute.read( attributeReader );
					case KnownAttribute.tag_LocalVariableTypeTable -> LocalVariableTypeTableAttribute.read( attributeReader );
					case KnownAttribute.tag_MethodParameters -> MethodParametersAttribute.read( attributeReader );
					case KnownAttribute.tag_NestHost -> NestHostAttribute.read( attributeReader );
					case KnownAttribute.tag_NestMembers -> NestMembersAttribute.read( attributeReader );
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> RuntimeInvisibleAnnotationsAttribute.read( attributeReader );
					case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> RuntimeInvisibleParameterAnnotationsAttribute.read( attributeReader );
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> RuntimeInvisibleTypeAnnotationsAttribute.read( attributeReader );
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> RuntimeVisibleAnnotationsAttribute.read( attributeReader );
					case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> RuntimeVisibleParameterAnnotationsAttribute.read( attributeReader );
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> RuntimeVisibleTypeAnnotationsAttribute.read( attributeReader );
					case KnownAttribute.tag_Signature -> SignatureAttribute.read( attributeReader );
					case KnownAttribute.tag_SourceFile -> SourceFileAttribute.read( attributeReader );
					case KnownAttribute.tag_StackMapTable -> StackMapTableAttribute.read( attributeReader );
					case KnownAttribute.tag_Synthetic -> SyntheticAttribute.of();
					default -> throw new InvalidKnownAttributeTagException( knownAttributeTag.get() );
				};
		}
		else
		{
			Buffer buffer = attributeReader.readBuffer( attributeLength );
			return UnknownAttribute.of( nameConstant, buffer );
		}
	}

	final Mutf8ValueConstant mutf8Name; // needs to be package-private because it is used by AttributeSet.

	protected Attribute( Mutf8ValueConstant mutf8Name )
	{
		this.mutf8Name = mutf8Name;
	}

	public String name() { return mutf8Name.stringValue(); }
	public abstract boolean isKnown();
	public boolean isOptional() { return false; }

	@ExcludeFromJacocoGeneratedReport public UnknownAttribute                              /**/ asUnknownAttribute                              /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public KnownAttribute                                /**/ asKnownAttribute                                /**/() { return Kit.fail(); }

	public abstract void intern( Interner interner );
	public abstract void write( ConstantWriter constantWriter );
}
