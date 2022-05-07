package mikenakis.immutability.type;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.type.assessments.ImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.UnderAssessmentTypeImmutabilityAssessment;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotAlreadyBeImmutableException;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotBeExtensibleException;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotBePreviouslyAssessedException;
import mikenakis.immutability.type.exceptions.PreassessedTypeMustBeClassException;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Assesses the immutability of types.
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

	public final ImmutableTypeImmutabilityAssessment immutableClassAssessmentInstance = new ImmutableTypeImmutabilityAssessment( stringizer );
	private final UnderAssessmentTypeImmutabilityAssessment underAssessmentInstance = new UnderAssessmentTypeImmutabilityAssessment( stringizer );
	private final Map<Class<?>,TypeImmutabilityAssessment> assessmentsByType = new HashMap<>();
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
		MyKit.sync.synchronize( assessmentsByType, () -> //
		{
			assessmentsByType.put( jvmClass, immutableClassAssessmentInstance );
		} );
	}

	public TypeImmutabilityAssessment assess( Class<?> type )
	{
		return MyKit.sync.synchronize( assessmentsByType, () -> //
		{
			TypeImmutabilityAssessment existingAssessment = assessmentsByType.get( type );
			if( existingAssessment != null )
				return existingAssessment;
			assessmentsByType.put( type, underAssessmentInstance );
			TypeImmutabilityAssessment newAssessment = reflector.assess( type );
			TypeImmutabilityAssessment oldAssessment = assessmentsByType.put( type, newAssessment );
			assert oldAssessment == underAssessmentInstance;
			assert !(newAssessment instanceof UnderAssessmentTypeImmutabilityAssessment);
			return newAssessment;
		} );
	}

	void addDefaultPreassessment( Class<?> jvmClass, TypeImmutabilityAssessment classAssessment )
	{
		assessmentsByType.put( jvmClass, classAssessment );
	}

	private boolean addedClassMustNotBePreviouslyAssessedAssertion( Class<?> jvmClass )
	{
		TypeImmutabilityAssessment previousClassAssessment = assessmentsByType.get( jvmClass );
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
		TypeImmutabilityAssessment assessment = reflector.assess( jvmClass );
		if( assessment instanceof ImmutableTypeImmutabilityAssessment ignore )
			throw new PreassessedClassMustNotAlreadyBeImmutableException( jvmClass );
		return true;
	}
}
