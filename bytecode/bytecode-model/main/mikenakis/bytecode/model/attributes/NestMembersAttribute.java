package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "NestMembers" {@link Attribute} of a java class file.
 * <p>
 * See JVMS 4.7.29 "The NestMembers Attribute", https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.7.29
 *
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NestMembersAttribute extends Attribute
{
	public static NestMembersAttribute of()
	{
		return of( new ArrayList<>() );
	}

	public static NestMembersAttribute of( List<ClassConstant> memberClassConstants )
	{
		return new NestMembersAttribute( memberClassConstants );
	}

	public static final String name = "NestMembers";
	public static final Kind kind = new Kind( name );

	private final List<ClassConstant> memberClassConstants;

	private NestMembersAttribute( List<ClassConstant> memberClassConstants )
	{
		super( kind );
		this.memberClassConstants = memberClassConstants;
	}

	public List<ClassConstant> memberClassConstants()
	{
		return memberClassConstants;
	}

	@Deprecated @Override public NestMembersAttribute asNestMembersAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return memberClassConstants.size() + " entries";
	}
}
