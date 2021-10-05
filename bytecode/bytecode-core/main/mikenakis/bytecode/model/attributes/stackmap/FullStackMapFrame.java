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

	public final List<VerificationType> localVerificationTypes;
	public final List<VerificationType> stackVerificationTypes;

	private FullStackMapFrame( Instruction targetInstruction, List<VerificationType> localVerificationTypes, List<VerificationType> stackVerificationTypes )
	{
		super( tag_Full, targetInstruction );
		this.localVerificationTypes = localVerificationTypes;
		this.stackVerificationTypes = stackVerificationTypes;
	}

	@Override public String getName( Optional<StackMapFrame> previousFrame ) { return typeName; }
	@Deprecated @Override public FullStackMapFrame asFullStackMapFrame() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return localVerificationTypes.size() + " localVerificationTypes, " + stackVerificationTypes.size() + " stackVerificationTypes"; }
}
