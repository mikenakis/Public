package mikenakis.immutability.internal.type;

import mikenakis.debug.Debug;
import mikenakis.immutability.internal.helpers.Helpers;
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
public final class TypeImmutabilityAssessor
{
	/**
	 * DO NOT USE; FOR INTERNAL USE ONLY.
	 */
	public static TypeImmutabilityAssessor create()
	{
		TypeImmutabilityAssessor assessor = new TypeImmutabilityAssessor();
		DefaultPreassessments.apply( assessor );
		return assessor;
	}

	private final Map<Class<?>,TypeAssessment> assessmentsByType = new HashMap<>();
	private final Reflector reflector = new Reflector( this );

	TypeImmutabilityAssessor()
	{
	}

	public void addImmutablePreassessment( Class<?> jvmClass )
	{
		assert addedClassMustNotBePreviouslyAssessedAssertion( jvmClass );
		assert addedClassMustBeClassTypeAssertion( jvmClass );
		assert addedClassMustNotBeExtensibleClassTypeAssertion( jvmClass );
		assert addedClassMustNotAlreadyBeImmutableAssertion( jvmClass );
		synchronized( assessmentsByType )
		{
			assessmentsByType.put( jvmClass, ImmutableTypeAssessment.instance );
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
				assessmentsByType.put( type, UnderAssessmentTypeAssessment.instance );
				TypeAssessment newAssessment = reflector.assess( type );
				TypeAssessment oldAssessment = assessmentsByType.put( type, newAssessment );
				assert oldAssessment == UnderAssessmentTypeAssessment.instance;
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
