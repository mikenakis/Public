package mikenakis.immutability.type;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.ExtensibleAssessment;
import mikenakis.immutability.type.assessments.provisory.IterableAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryCompositeAssessment;
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
		StandardPreassessments.apply( assessor );
		return assessor;
	}

	public final ImmutableTypeAssessment immutableClassAssessmentInstance = new ImmutableTypeAssessment( stringizer );
	private final UnderAssessmentTypeAssessment underAssessmentInstance = new UnderAssessmentTypeAssessment( stringizer );
	private final Map<Class<?>,TypeAssessment> assessmentsByType = new HashMap<>();
	private final Reflector reflector = new Reflector( this );

	private TypeImmutabilityAssessor( Stringizer stringizer )
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

	public TypeAssessment assess( Class<?> type )
	{
		return MyKit.sync.synchronize( assessmentsByType, () -> //
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

	void addDefaultExtensiblePreassessment( Class<?> jvmClass )
	{
		assert !(new TypeImmutabilityAssessor( stringizer ).assess( jvmClass ) instanceof ExtensibleAssessment);
		ExtensibleAssessment assessment = new ExtensibleAssessment( stringizer, TypeAssessment.Mode.PreassessedByDefault, jvmClass );
		addDefaultPreassessment( jvmClass, assessment );
	}

	void addDefaultImmutablePreassessment( Class<?> jvmClass )
	{
		assert !(new TypeImmutabilityAssessor( stringizer ).assess( jvmClass ) instanceof ImmutableTypeAssessment);
		addDefaultPreassessment( jvmClass, immutableClassAssessmentInstance );
	}

	void addDefaultIterablePreassessment( Class<? extends Iterable<?>> jvmClass )
	{
		assert !(new TypeImmutabilityAssessor( stringizer ).assess( jvmClass ) instanceof IterableAssessment);
		IterableAssessment assessment = new IterableAssessment( stringizer, TypeAssessment.Mode.PreassessedByDefault, jvmClass );
		addDefaultPreassessment( jvmClass, assessment );
	}

	<T,E> void addDefaultCompositePreassessment( Class<T> compositeType, Decomposer<T,E> decomposer )
	{
		ProvisoryCompositeAssessment<T,E> assessment = new ProvisoryCompositeAssessment<>( stringizer, TypeAssessment.Mode.PreassessedByDefault, compositeType, decomposer );
		addDefaultPreassessment( compositeType, assessment );
	}

	private void addDefaultPreassessment( Class<?> jvmClass, TypeAssessment classAssessment )
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
