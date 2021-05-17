package mikenakis.rumination.agentclaire_interceptor;

import mikenakis.agentclaire.AgentClaire;
import mikenakis.bytecode.Annotatable;
import mikenakis.bytecode.AnnotationParameter;
import mikenakis.bytecode.AnnotationValue;
import mikenakis.bytecode.ByteCodeAnnotation;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.rumination.Ruminizer;
import mikenakis.rumination.annotations.Ruminant;
import mikenakis.services.bytecode.ByteCodeService;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Receives instances of {@link ByteCodeType} as they are loaded by the java agent and processes those of them that are ruminants.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class RuminizingInterceptor
{
	private static final String RUMINANT_ANNOTATION_CLASS_NAME = Ruminant.class.getName();
	private static final String RUMINATOR_METHOD_NAME_ANNOTATION_PARAMETER_NAME = Kit.unchecked( () -> Ruminant.class.getMethod( "ruminatorMethodName" ) ).getName();
	private static final String PROCESSED_ANNOTATION_PARAMETER_NAME = Kit.unchecked( () -> Ruminant.class.getMethod( "processed" ) ).getName();

	private final Ruminizer ruminizer = new Ruminizer();
	private final String defaultRuminatorMethodName;

	private boolean interceptor( ByteCodeType byteCodeType, ClassLoader classLoader )
	{
		Optional<String> ruminatorMethodName = tryGetRuminatorMethodName( byteCodeType, classLoader );
		if( ruminatorMethodName.isEmpty() )
			return false;
		Log.debug( "Ruminant: " + byteCodeType.getName() );
		ruminizer.ruminize( byteCodeType, ruminatorMethodName.get() );
		markAsProcessed( byteCodeType );
		return true;
	}

	public RuminizingInterceptor( AgentClaire agentClaire )
	{
		Method method = Kit.unchecked( () -> Ruminant.class.getMethod( RUMINATOR_METHOD_NAME_ANNOTATION_PARAMETER_NAME ) );
		defaultRuminatorMethodName = (String)method.getDefaultValue();
		agentClaire.addInterceptor( this::interceptor );
	}

	private static final ByteCodeAnnotation.Factory ruminantAnnotationFactory = r -> new ByteCodeAnnotation( r, RUMINANT_ANNOTATION_CLASS_NAME );
	private static final AnnotationValue.Factory booleanTrueAnnotationValueFactory = r -> new ConstAnnotationValue( r, ConstAnnotationValue.BOOLEAN_KIND, new IntegerConstant( 1 ) );
	private static final AnnotationParameter.Factory processedAnnotationParameterFactory = r -> new AnnotationParameter( r, PROCESSED_ANNOTATION_PARAMETER_NAME, booleanTrueAnnotationValueFactory );

	private static void markAsProcessed( Annotatable byteCodeType )
	{
		ByteCodeAnnotation ruminantAnnotation = byteCodeType.tryGetAnnotationByName( RUMINANT_ANNOTATION_CLASS_NAME )
			.orElseGet( () -> byteCodeType.addAnnotation( ruminantAnnotationFactory ) );
		AnnotationParameter processedAnnotationParameter = ruminantAnnotation.tryGetParameter( PROCESSED_ANNOTATION_PARAMETER_NAME )
			.orElseGet( () -> ruminantAnnotation.putParameter( processedAnnotationParameterFactory ) );
		assert processedAnnotationParameter.annotationValue.asConstAnnotationValue().valueConstant.asIntegerConstant().value == 1;
	}

	private Optional<String> tryGetRuminatorMethodName( ByteCodeType byteCodeType, ClassLoader classLoader )
	{
		for( ; ; )
		{
			Optional<ByteCodeAnnotation> ruminantByteCodeAnnotation = byteCodeType.tryGetAnnotationByName( RUMINANT_ANNOTATION_CLASS_NAME );
			if( ruminantByteCodeAnnotation.isPresent() )
			{
				Optional<AnnotationParameter> ruminatorMethodNameAnnotationParameter = ruminantByteCodeAnnotation.get().tryGetParameter( RUMINATOR_METHOD_NAME_ANNOTATION_PARAMETER_NAME );
				if( ruminatorMethodNameAnnotationParameter.isEmpty() )
					return Optional.of( defaultRuminatorMethodName );
				return Optional.of( ruminatorMethodNameAnnotationParameter.get().annotationValue.asConstAnnotationValue().valueConstant.asUtf8Constant().getStringValue() );
			}
			Optional<String> superClassName = byteCodeType.getSuperClassName();
			if( superClassName.isEmpty() )
				return Optional.empty();
			Optional<ByteCodeType> optionalByteCodeType = ByteCodeService.instance.tryGetByteCodeTypeByName( superClassName.get(), classLoader );
			if( optionalByteCodeType.isEmpty() )
				return Optional.empty();
			byteCodeType = optionalByteCodeType.get();
		}
	}
}
