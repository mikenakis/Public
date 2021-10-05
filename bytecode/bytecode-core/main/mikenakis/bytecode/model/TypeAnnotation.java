package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.InvalidTargetTagException;
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
import mikenakis.bytecode.model.attributes.target.TypePathEntry;
import mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collection;
import java.util.List;

public final class TypeAnnotation // "type_annotation" in jvms-4.7.20
{
	public static TypeAnnotation read( AttributeReader attributeReader )
	{
		int targetTag = attributeReader.readUnsignedByte();
		Target target = switch( targetTag )
			{
				case Target.tag_ClassTypeParameter, Target.tag_MethodTypeParameter -> TypeParameterTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_Supertype -> SupertypeTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_ClassTypeBound, Target.tag_MethodTypeBound -> TypeParameterBoundTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_FieldType, Target.tag_ReturnType, Target.tag_ReceiverType -> EmptyTarget.of( targetTag );
				case Target.tag_FormalParameter -> FormalParameterTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_Throws -> ThrowsTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_LocalVariable, Target.tag_ResourceLocalVariable -> LocalVariableTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_Catch -> CatchTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_InstanceOfOffset, Target.tag_NewExpressionOffset, Target.tag_NewMethodOffset, Target.tag_IdentifierMethodOffset -> //
					OffsetTarget.read( attributeReader.bufferReader, targetTag );
				case Target.tag_CastArgument, Target.tag_ConstructorArgument, Target.tag_MethodArgument, Target.tag_NewMethodArgument, //
					Target.tag_IdentifierMethodArgument -> TypeArgumentTarget.read( attributeReader.bufferReader, targetTag );
				default -> throw new InvalidTargetTagException( targetTag );
			};
		TypePath targetPath = TypePath.read( attributeReader );
		Mutf8ValueConstant annotationTypeNameConstant = attributeReader.readIndexAndGetConstant().asMutf8ValueConstant();
		List<AnnotationParameter> pairs = Annotation.readAnnotationParameters( attributeReader );
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
		//internTarget( typeAnnotation.target() ); //TODO
		//internTypePath( typeAnnotation.typePath() ); //TODO
		annotationTypeNameConstant.intern( interner );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.intern( interner );
	}

	public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( target.tag );
		target.write( constantWriter );
		targetPath.write( constantWriter );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( annotationTypeNameConstant ) );
		constantWriter.writeUnsignedShort( parameters.size() );
		for( AnnotationParameter annotationParameter : parameters )
			annotationParameter.write( constantWriter );
	}

	public static void writeTypeAnnotations( ConstantWriter constantWriter, Collection<TypeAnnotation> typeAnnotations )
	{
		constantWriter.writeUnsignedShort( typeAnnotations.size() );
		for( TypeAnnotation typeAnnotation : typeAnnotations )
			typeAnnotation.write( constantWriter );
	}
}
