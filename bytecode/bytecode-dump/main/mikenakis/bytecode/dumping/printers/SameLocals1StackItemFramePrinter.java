package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.attributes.stackmap.SameLocals1StackItemFrame;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.Optional;

/**
 * {@link SameLocals1StackItemFrame} {@link FramePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameLocals1StackItemFramePrinter extends FramePrinter
{
	private final SameLocals1StackItemFrame sameLocals1StackItemFrame;

	public SameLocals1StackItemFramePrinter( SameLocals1StackItemFrame sameLocals1StackItemFrame, Optional<Frame> previousFrame )
	{
		super( sameLocals1StackItemFrame, previousFrame );
		this.sameLocals1StackItemFrame = sameLocals1StackItemFrame;
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of(
			Twig.of( "stackVerificationType",
				List.of( RenderingContext.newPrinter( sameLocals1StackItemFrame.stackVerificationType ).toTwig( renderingContext, "" ) ) ) );
	}
}
