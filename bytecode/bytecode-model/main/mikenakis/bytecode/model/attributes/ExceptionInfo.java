package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.attributes.code.AbsoluteInstructionReference;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents an entry of the "exception table" of the "code" attribute of a java class file.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionInfo
{
	public static ExceptionInfo of( AbsoluteInstructionReference startInstructionReference, AbsoluteInstructionReference endInstructionReference, //
		AbsoluteInstructionReference handlerInstructionReference, Optional<ClassConstant> catchTypeConstant )
	{
		return new ExceptionInfo( startInstructionReference, endInstructionReference, handlerInstructionReference, catchTypeConstant );
	}

	public final AbsoluteInstructionReference startInstructionReference;
	public final AbsoluteInstructionReference endInstructionReference;
	public final AbsoluteInstructionReference handlerInstructionReference;
	public final Optional<ClassConstant> catchTypeConstant;

	private ExceptionInfo( AbsoluteInstructionReference startInstructionReference, AbsoluteInstructionReference endInstructionReference, //
		AbsoluteInstructionReference handlerInstructionReference, Optional<ClassConstant> catchTypeConstant )
	{
		this.startInstructionReference = startInstructionReference;
		this.endInstructionReference = endInstructionReference;
		this.handlerInstructionReference = handlerInstructionReference;
		this.catchTypeConstant = catchTypeConstant;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "start = {" + startInstructionReference + "}, handler = {" + handlerInstructionReference + "}" + //
			catchTypeConstant.map( classConstant -> ", catchType = " + classConstant.getClassName() ).orElse( "" );
	}
}
