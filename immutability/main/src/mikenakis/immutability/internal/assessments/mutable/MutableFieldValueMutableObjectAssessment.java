package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.type.assessments.nonimmutable.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it contains a provisory field which has mutable value.
 */
public final class MutableFieldValueMutableObjectAssessment extends MutableObjectAssessment
{
	public final Object object;
	public final ProvisoryTypeAssessment declaringTypeAssessment;
	public final ProvisoryFieldAssessment provisoryFieldAssessment;
	public final MutableObjectAssessment fieldValueAssessment;

	public MutableFieldValueMutableObjectAssessment( Object object, ProvisoryTypeAssessment declaringTypeAssessment, ProvisoryFieldAssessment provisoryFieldAssessment, //
		MutableObjectAssessment fieldValueAssessment )
	{
		assert declaringTypeAssessment.type.isAssignableFrom( object.getClass() );
		assert declaringTypeAssessment.type == provisoryFieldAssessment.field.getDeclaringClass();
		assert List.of( declaringTypeAssessment.type.getDeclaredFields() ).contains( provisoryFieldAssessment.field );
		assert fieldValueAssessment.object().equals( MyKit.getFieldValue( object, provisoryFieldAssessment.field ) );
		this.object = object;
		this.declaringTypeAssessment = declaringTypeAssessment;
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValueAssessment = fieldValueAssessment;
	}

	@Override public Object object() { return object; }
	@Override public NonImmutableTypeAssessment typeAssessment() { return declaringTypeAssessment; }
	@Override public List<Assessment> children() { return List.of( declaringTypeAssessment/*, provisoryFieldAssessment*/, fieldValueAssessment ); }
}
