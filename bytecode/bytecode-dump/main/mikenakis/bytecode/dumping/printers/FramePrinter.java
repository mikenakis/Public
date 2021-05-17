package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Optional;

/**
 * {@link Frame} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class FramePrinter extends Printer
{
	private final Frame frame;
	public final Optional<Frame> previousFrame;

	protected FramePrinter( Frame frame, Optional<Frame> previousFrame )
	{
		this.frame = frame;
		this.previousFrame = previousFrame;
	}

	@Override @OverridingMethodsMustInvokeSuper
	public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( frame.getName( previousFrame ) ).append( ' ' );
		if( renderingContext.style.raw )
		{
			int offsetDelta = frame.getOffsetDelta( previousFrame );
			builder.append( "offsetDelta = " ).append( offsetDelta );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			String label = renderingContext.getLabel( frame.targetInstruction );
			builder.append( "target = " ).append( label );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
