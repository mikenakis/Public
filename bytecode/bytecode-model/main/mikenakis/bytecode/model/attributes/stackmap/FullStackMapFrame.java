package mikenakis.bytecode.model.attributes.stackmap;

import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.stackmap.verification.VerificationType;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 'Full' {@link StackMapFrame}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FullStackMapFrame extends StackMapFrame
{
	public static FullStackMapFrame of( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		return new FullStackMapFrame( targetInstruction, localVerificationTypes, stackVerificationTypes );
	}

	public static final int type = 255;
	public static final String typeName = "FullFrame";

	private final List<VerificationType> localVerificationTypes;
	private final List<VerificationType> stackVerificationTypes;

	private FullStackMapFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		super( typeName, targetInstruction );
		this.localVerificationTypes = localVerificationTypes;
		this.stackVerificationTypes = stackVerificationTypes;
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame )
	{
		return typeName;
	}

	public List<VerificationType> localVerificationTypes() { return Collections.unmodifiableList( localVerificationTypes ); }
	public List<VerificationType> stackVerificationTypes() { return Collections.unmodifiableList( stackVerificationTypes ); }

	@Deprecated @Override public Optional<FullStackMapFrame> tryAsFullStackMapFrame() { return Optional.of( this ); }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return localVerificationTypes.size() + " localVerificationTypes, " + stackVerificationTypes.size() + " stackVerificationTypes";
	}
}
