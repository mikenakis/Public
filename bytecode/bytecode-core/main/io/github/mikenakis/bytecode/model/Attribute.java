package io.github.mikenakis.bytecode.model;

import io.github.mikenakis.bytecode.exceptions.InvalidKnownAttributeTagException;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.bytecode.kit.Buffer;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.attributes.AnnotationDefaultAttribute;
import io.github.mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import io.github.mikenakis.bytecode.model.attributes.CodeAttribute;
import io.github.mikenakis.bytecode.model.attributes.ConstantValueAttribute;
import io.github.mikenakis.bytecode.model.attributes.DeprecatedAttribute;
import io.github.mikenakis.bytecode.model.attributes.EnclosingMethodAttribute;
import io.github.mikenakis.bytecode.model.attributes.ExceptionsAttribute;
import io.github.mikenakis.bytecode.model.attributes.InnerClassesAttribute;
import io.github.mikenakis.bytecode.model.attributes.KnownAttribute;
import io.github.mikenakis.bytecode.model.attributes.LineNumberTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.LocalVariableTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.LocalVariableTypeTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.MethodParametersAttribute;
import io.github.mikenakis.bytecode.model.attributes.NestHostAttribute;
import io.github.mikenakis.bytecode.model.attributes.NestMembersAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeInvisibleAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeInvisibleParameterAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeInvisibleTypeAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeVisibleAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeVisibleParameterAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.RuntimeVisibleTypeAnnotationsAttribute;
import io.github.mikenakis.bytecode.model.attributes.SignatureAttribute;
import io.github.mikenakis.bytecode.model.attributes.SourceFileAttribute;
import io.github.mikenakis.bytecode.model.attributes.StackMapTableAttribute;
import io.github.mikenakis.bytecode.model.attributes.SyntheticAttribute;
import io.github.mikenakis.bytecode.model.attributes.UnknownAttribute;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
 * @author michael.gr
 */
public abstract class Attribute
{
	public static Attribute read( BufferReader bufferReader, ReadingConstantPool constantPool, Optional<ReadingLocationMap> locationMap, Mutf8ValueConstant nameConstant, int attributeLength )
	{
		Optional<Integer> knownAttributeTag = KnownAttribute.tagFromName( nameConstant );
		if( knownAttributeTag.isPresent() )
		{
			return switch( knownAttributeTag.get() )
				{
					case KnownAttribute.tag_AnnotationDefault -> AnnotationDefaultAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_BootstrapMethods -> BootstrapMethodsAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_Code -> CodeAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_ConstantValue -> ConstantValueAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_Deprecated -> DeprecatedAttribute.of();
					case KnownAttribute.tag_EnclosingMethod -> EnclosingMethodAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_Exceptions -> ExceptionsAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_InnerClasses -> InnerClassesAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_LineNumberTable -> LineNumberTableAttribute.read( bufferReader, locationMap.orElseThrow() );
					case KnownAttribute.tag_LocalVariableTable -> LocalVariableTableAttribute.read( bufferReader, constantPool, locationMap.orElseThrow() );
					case KnownAttribute.tag_LocalVariableTypeTable -> LocalVariableTypeTableAttribute.read( bufferReader, constantPool, locationMap.orElseThrow() );
					case KnownAttribute.tag_MethodParameters -> MethodParametersAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_NestHost -> NestHostAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_NestMembers -> NestMembersAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_RuntimeInvisibleAnnotations -> RuntimeInvisibleAnnotationsAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_RuntimeInvisibleParameterAnnotations -> RuntimeInvisibleParameterAnnotationsAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_RuntimeInvisibleTypeAnnotations -> RuntimeInvisibleTypeAnnotationsAttribute.read( bufferReader, constantPool, locationMap );
					case KnownAttribute.tag_RuntimeVisibleAnnotations -> RuntimeVisibleAnnotationsAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_RuntimeVisibleParameterAnnotations -> RuntimeVisibleParameterAnnotationsAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_RuntimeVisibleTypeAnnotations -> RuntimeVisibleTypeAnnotationsAttribute.read( bufferReader, constantPool, locationMap );
					case KnownAttribute.tag_Signature -> SignatureAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_SourceFile -> SourceFileAttribute.read( bufferReader, constantPool );
					case KnownAttribute.tag_StackMapTable -> StackMapTableAttribute.read( bufferReader, constantPool, locationMap.orElseThrow() );
					case KnownAttribute.tag_Synthetic -> SyntheticAttribute.of();
					default -> throw new InvalidKnownAttributeTagException( knownAttributeTag.get() );
				};
		}
		else
		{
			Buffer buffer = bufferReader.readBuffer( attributeLength );
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

	@ExcludeFromJacocoGeneratedReport public UnknownAttribute /**/ asUnknownAttribute /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public KnownAttribute   /**/ asKnownAttribute   /**/() { return Kit.fail(); }

	public abstract void intern( Interner interner );
	public abstract void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap );
}
