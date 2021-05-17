package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.ChopFrame;
import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.dumping.RenderingContext;

import java.util.Optional;

/**
 * {@link ChopFrame} {@link FramePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ChopFramePrinter extends FramePrinter
{
	private final ChopFrame chopFrame;

	public ChopFramePrinter( ChopFrame chopFrame, Optional<Frame> previousFrame )
	{
		super( chopFrame, previousFrame );
		this.chopFrame = chopFrame;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		super.appendTo( renderingContext, builder );
		builder.append( ' ' );
		chopFrame.toStringBuilder( builder );
	}
}
