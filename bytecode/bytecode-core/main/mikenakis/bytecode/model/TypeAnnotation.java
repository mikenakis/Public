package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.InvalidTargetTagException;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.attributes.target.CatchTarget;
import mikenakis.bytecode.model.attributes.target.EmptyTarget;
import mikenakis.bytecode.model.attributes.target.FormalParameterTarget;
import mikenakis.bytecode.model.attributes.target.LocalVariableTarget;
import mikenakis.bytecode.model.attributes.target.OffsetTarget;
import mikenakis.bytecode.model.attributes.target.SupertypeTarget;
import mikenakis.bytecode.model.attributes.target.Target;
import mikenakis.bytecode.model.attributes.target.ThrowsTarget;
import mikenakis.bytecode.model.attributes.target.TypeArgumentTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterBoundTarget;
import mikenakis.bytecode.model.attributes.target.TypeParameterTarget;
import mikenakis.bytecode.model.attributes.target.TypePath;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class TypeAnnotation // "type_annotation" in jvms-4.7.20
{
	public static TypeAnnotation read( BufferReader bufferReader, ReadingConstantPool constantPool, Optional<ReadingLocationMap> locationMap )
	{
		int targetTag = bufferReader.readUnsignedByte();
		Target target = switch( targetTag )
			{
				case Target.tag_ClassTypeParameter, Target.tag_MethodTypeParameter -> TypeParameterTarget.read( bufferReader, targetTag );
				case Target.tag_Supertype -> SupertypeTarget.read( bufferReader, targetTag );
				case Target.tag_ClassTypeBound, Target.tag_MethodTypeBound -> TypeParameterBoundTarget.read( bufferReader, targetTag );
				case Target.tag_FieldType, Target.tag_ReturnType, Target.tag_ReceiverType -> EmptyTarget.of( targetTag );
				case Target.tag_FormalParameter -> FormalParameterTarget.read( bufferReader, targetTag );
				case Target.tag_Throws -> ThrowsTarget.read( bufferReader, targetTag );
				case Target.tag_LocalVariable, Target.tag_ResourceLocalVariable -> LocalVariableTarget.read( bufferReader, locationMap.orElseThrow(), targetTag );
				case Target.tag_Catch -> CatchTarget.read( bufferReader, targetTag );
				case Target.tag_InstanceOfOffset, Target.tag_NewExpressionOffset, Target.tag_NewMethodOffset, Target.tag_IdentifierMethodOffset -> //
					OffsetTarget.read( bufferReader, targetTag );
				case Target.tag_CastArgument, Target.tag_ConstructorArgument, Target.tag_MethodArgument, Target.tag_NewMethodArgument, //
					Target.tag_IdentifierMethodArgument -> TypeArgumentTarget.read( bufferReader, targetTag );
				default -> throw new InvalidTargetTagException( targetTag );
			};
		TypePath targetPath = TypePath.read( bufferReader );
		Mutf8ValueConstant annotationTypeNameConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		List<AnnotationParameter> pairs = Annotation.readAnnotationParameters( bufferReader, constantPool );
		return of( target, targetPath, annotationTypeNameConstant, pairs );
	}

	public static TypeAnnotation of( Target target, TypePath targetPath, Mutf8ValueConstant annotationTypeNameConstant, List<AnnotationParameter> parameters )
	{
		return new TypeAnnotation( target, targetPath, annotationTypeNameConstant, parameters );
	}

	public final Target target;
	public final TypePath targetPath;
	private final Mutf8ValueConstant annotationTypeNameConstant;
	public final List<AnnotationParameter> parameters;

	private TypeAnnotation( Target target, TypePath targetPath, Mutf8ValueConstant annotationTypeNameConstant, List<AnnotationParameter> parameters )
	{
		this.target = target;
		this.targetPath = targetPath;
		this.annotationTypeNameConstant = annotationTypeNameConstant;
		this.parameters = parameters;
	}

	public TypeDescriptor typeDescriptor() { return ByteCodeHelpers.typeDescriptorFromDescriptorStringConstant( annotationTypeNameConstant ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "targetType = " + String.format( "0x%02x ", target.tag ) + "targetPath = " + targetPath + "type = " + annotationTypeNameConstant + ", " + parameters.size() + " elementValuePairs"; }

	public void intern( Interner interner )
	{
		target.intern( interner );
		targetPath.intern( interner );
		annotationTypeNameConstant.intern( interner );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.intern( interner );
	}

	public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedByte( target.tag );
		target.write( bufferWriter, constantPool, locationMap );
		targetPath.write( bufferWriter );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( annotationTypeNameConstant ) );
		bufferWriter.writeUnsignedShort( parameters.size() );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.write( bufferWriter, constantPool );
	}

	public static void writeTypeAnnotations( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap, Collection<TypeAnnotation> typeAnnotations )
	{
		bufferWriter.writeUnsignedShort( typeAnnotations.size() );
		for( TypeAnnotation typeAnnotation : typeAnnotations )
			typeAnnotation.write( bufferWriter, constantPool, locationMap );
	}
}
