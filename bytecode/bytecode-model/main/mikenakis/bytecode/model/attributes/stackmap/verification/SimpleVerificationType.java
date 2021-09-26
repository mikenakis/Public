package mikenakis.bytecode.model.attributes.stackmap.verification;

/**
 * 'Simple' {@link VerificationType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SimpleVerificationType extends VerificationType
{
	public SimpleVerificationType( int tag )
	{
		super( tag );
		assert tag == tag_Top ||tag == tag_Integer || tag == tag_Float || tag == tag_Double || tag == tag_Long || tag == tag_Null || tag == tag_UninitializedThis;
	}

	@Deprecated @Override public SimpleVerificationType asSimpleVerificationType() { return this; }
}
