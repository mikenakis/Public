package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.object.fieldvalue.MutableFieldValueAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;

import java.util.List;
import java.util.stream.Stream;

/**
 * Signifies that an object is mutable because its class was assessed as provisory due to provisory fields, and one or more of the provisory
 * fields has a value which has been assessed as mutable.
 */
public final class MutableFieldValuesAssessment extends MutableObjectAssessment
{
	public final ProvisoryContentAssessment typeAssessment;
	public final List<MutableFieldValueAssessment> fieldValueAssessments;

	public MutableFieldValuesAssessment( Stringizer stringizer, Object object, ProvisoryContentAssessment typeAssessment, //
		List<MutableFieldValueAssessment> fieldValueAssessments )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.fieldValueAssessments = fieldValueAssessments;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of provisory type " ).append( stringizer.stringizeClassName( typeAssessment.type ) );
		stringBuilder.append( " and it has one or more fields that contain mutable values" );
	}

	@Override public List<? extends Assessment> children()
	{
		return Stream.concat( Stream.of( typeAssessment ), fieldValueAssessments.stream() ).toList();
	}
}
