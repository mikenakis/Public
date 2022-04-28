package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Helpers;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Signifies that a class is assessed as provisory due to its content and possibly also due to its ancestor being provisory.
 */
public final class ProvisoryContentAssessment extends ProvisoryTypeAssessment
{
	public final Optional<ProvisoryContentAssessment> ancestorAssessment;
	public final List<ProvisoryFieldAssessment> fieldAssessments;

	public ProvisoryContentAssessment( Stringizer stringizer, Class<?> jvmClass, Optional<ProvisoryContentAssessment> ancestorAssessment, //
		List<ProvisoryFieldAssessment> fieldAssessments )
	{
		super( stringizer, jvmClass );
		assert Helpers.isClass( jvmClass );
		assert ancestorAssessment.isEmpty() || jvmClass.getSuperclass() == ancestorAssessment.get().type;
		this.ancestorAssessment = ancestorAssessment;
		this.fieldAssessments = fieldAssessments;
	}

	@Override public List<? extends Assessment> children()
	{
		Stream<? extends Assessment> result = Stream.of();
		if( ancestorAssessment.isPresent() )
			result = Stream.concat( result, Stream.of( ancestorAssessment.get() ) );
		result = Stream.concat( result, fieldAssessments.stream() );
//		if( ancestorAssessment instanceof ProvisoryFieldsAssessment provisoryFieldsAssessment )
//			result = Stream.concat( result, provisoryFieldsAssessment.provisoryFieldAssessments.stream() );
		return result.toList();
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " and provisory superclass" );
	}
}
