package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.ExceptionInfo;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ExceptionInfo} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ExceptionInfoPrinter extends Printer
{
	private final ExceptionInfo exceptionInfo;

	public ExceptionInfoPrinter( ExceptionInfo exceptionInfo )
	{
		this.exceptionInfo = exceptionInfo;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "startPc = " );
		RenderingContext.newPrinter( exceptionInfo.startPc ).appendTo( renderingContext, builder );
		builder.append( " endPc = " );
		RenderingContext.newPrinter( exceptionInfo.endPc ).appendTo( renderingContext, builder );
		builder.append( " handlerPc = " );
		RenderingContext.newPrinter( exceptionInfo.handlerPc ).appendTo( renderingContext, builder );
		if( exceptionInfo.catchTypeConstant.isPresent() )
		{
			builder.append( " catchType = " );
			RenderingContext.newPrinter( exceptionInfo.catchTypeConstant.get() ).appendIndexTo( renderingContext, builder );
		}
	}
}
