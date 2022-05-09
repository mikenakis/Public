package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it contains a provisory field which has mutable value.
 */
public final class MutableFieldValueMutableObjectAssessment extends MutableObjectAssessment
{
	public final ProvisoryTypeAssessment declaringTypeAssessment;
	public final ProvisoryFieldAssessment provisoryFieldAssessment;
	public final MutableObjectAssessment fieldValueAssessment;

	public MutableFieldValueMutableObjectAssessment( Object object, ProvisoryTypeAssessment declaringTypeAssessment, ProvisoryFieldAssessment provisoryFieldAssessment, MutableObjectAssessment fieldValueAssessment )
	{
		super( object );
		this.declaringTypeAssessment = declaringTypeAssessment;
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValueAssessment = fieldValueAssessment;
	}

	@Override public Iterable<? extends Assessment> children() { return List.of( declaringTypeAssessment, provisoryFieldAssessment, fieldValueAssessment ); }
}
