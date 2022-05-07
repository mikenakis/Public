package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Signifies that a class is assessed as provisory due to its content and possibly also due to its ancestor being provisory.
 */
public final class HasProvisoryContentProvisoryTypeImmutabilityAssessment extends ProvisoryTypeImmutabilityAssessment
{
	public final Optional<ProvisoryTypeImmutabilityAssessment> ancestorAssessment;
	public final List<ProvisoryFieldImmutabilityAssessment> fieldAssessments;

	public HasProvisoryContentProvisoryTypeImmutabilityAssessment( Stringizer stringizer, Class<?> jvmClass, Optional<ProvisoryTypeImmutabilityAssessment> ancestorAssessment, //
		List<ProvisoryFieldImmutabilityAssessment> fieldAssessments )
	{
		super( stringizer, jvmClass );
		assert Helpers.isClass( jvmClass );
		assert ancestorAssessment.isEmpty() || jvmClass.getSuperclass() == ancestorAssessment.get().type;
		this.ancestorAssessment = ancestorAssessment;
		this.fieldAssessments = fieldAssessments;
	}

	@Override public List<? extends ImmutabilityAssessment> children()
	{
		Stream<? extends ImmutabilityAssessment> result = Stream.of();
		if( ancestorAssessment.isPresent() )
			result = Stream.concat( result, Stream.of( ancestorAssessment.get() ) );
		result = Stream.concat( result, fieldAssessments.stream() );
		return result.toList();
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because" );
		ancestorAssessment.ifPresent( p -> stringBuilder.append( " it extends provisory type " ).append( stringizer.stringizeClassName( p.type ) ) );
		if( !fieldAssessments.isEmpty() )
		{
			if( ancestorAssessment.isPresent() )
				stringBuilder.append( " and" );
			stringBuilder.append( " it contains provisory fields" );
		}
	}
}
