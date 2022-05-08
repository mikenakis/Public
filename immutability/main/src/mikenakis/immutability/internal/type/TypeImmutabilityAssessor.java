package mikenakis.immutability.internal.type;

import mikenakis.debug.Debug;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.internal.type.exceptions.PreassessedClassMustNotAlreadyBeImmutableException;
import mikenakis.immutability.internal.type.exceptions.PreassessedClassMustNotBeExtensibleException;
import mikenakis.immutability.internal.type.exceptions.PreassessedClassMustNotBePreviouslyAssessedException;
import mikenakis.immutability.internal.type.exceptions.PreassessedTypeMustBeClassException;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Assesses the immutability of types. DO NOT USE; FOR INTERNAL USE ONLY.
 *
 * @author michael.gr
 */
public final class TypeImmutabilityAssessor extends Stringizable
{
	public static final TypeImmutabilityAssessor instance = create( Stringizer.defaultInstance );

	/**
	 * Note: normally, you want to use the instance field instead of this static factory method.
	 * This static factory method is public only so as to allow the tests to create their own instances for testing purposes.
	 */
	public static TypeImmutabilityAssessor create( Stringizer stringizer )
	{
		TypeImmutabilityAssessor assessor = new TypeImmutabilityAssessor( stringizer );
		DefaultPreassessments.apply( assessor );
		return assessor;
	}

	public final ImmutableTypeAssessment immutableClassAssessmentInstance = new ImmutableTypeAssessment( stringizer );
	private final UnderAssessmentTypeAssessment underAssessmentInstance = new UnderAssessmentTypeAssessment( stringizer );
	private final Map<Class<?>,TypeAssessment> assessmentsByType = new HashMap<>();
	private final Reflector reflector = new Reflector( this );

	TypeImmutabilityAssessor( Stringizer stringizer )
	{
		super( stringizer );
	}

	public void addImmutablePreassessment( Class<?> jvmClass )
	{
		assert addedClassMustNotBePreviouslyAssessedAssertion( jvmClass );
		assert addedClassMustBeClassTypeAssertion( jvmClass );
		assert addedClassMustNotBeExtensibleClassTypeAssertion( jvmClass );
		assert addedClassMustNotAlreadyBeImmutableAssertion( jvmClass );
		synchronized( assessmentsByType )
		{
			assessmentsByType.put( jvmClass, immutableClassAssessmentInstance );
		}
	}

	public TypeAssessment assess( Class<?> type )
	{
		synchronized( assessmentsByType )
		{
			return Debug.boundary( () ->
			{
				TypeAssessment existingAssessment = assessmentsByType.get( type );
				if( existingAssessment != null )
					return existingAssessment;
				assessmentsByType.put( type, underAssessmentInstance );
				TypeAssessment newAssessment = reflector.assess( type );
				TypeAssessment oldAssessment = assessmentsByType.put( type, newAssessment );
				assert oldAssessment == underAssessmentInstance;
				assert !(newAssessment instanceof UnderAssessmentTypeAssessment);
				return newAssessment;
			} );
		}
	}

	void addDefaultPreassessment( Class<?> jvmClass, TypeAssessment classAssessment )
	{
		assessmentsByType.put( jvmClass, classAssessment );
	}

	private boolean addedClassMustNotBePreviouslyAssessedAssertion( Class<?> jvmClass )
	{
		TypeAssessment previousClassAssessment = assessmentsByType.get( jvmClass );
		assert previousClassAssessment == null : new PreassessedClassMustNotBePreviouslyAssessedException( previousClassAssessment );
		return true;
	}

	private static boolean addedClassMustBeClassTypeAssertion( Class<?> jvmClass )
	{
		if( !Helpers.isClass( jvmClass ) )
			throw new PreassessedTypeMustBeClassException( jvmClass );
		return true;
	}

	private static boolean addedClassMustNotBeExtensibleClassTypeAssertion( Class<?> jvmClass )
	{
		if( !Modifier.isFinal( jvmClass.getModifiers() ) )
			throw new PreassessedClassMustNotBeExtensibleException( jvmClass );
		return true;
	}

	private boolean addedClassMustNotAlreadyBeImmutableAssertion( Class<?> jvmClass )
	{
		TypeAssessment assessment = reflector.assess( jvmClass );
		if( assessment instanceof ImmutableTypeAssessment ignore )
			throw new PreassessedClassMustNotAlreadyBeImmutableException( jvmClass );
		return true;
	}
}
