package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.attributes.stackmap.FullFrame;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link FullFrame} {@link FramePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FullFramePrinter extends FramePrinter
{
	public final FullFrame fullFrame;

	public FullFramePrinter( FullFrame fullFrame, Optional<Frame> previousFrame )
	{
		super( fullFrame, previousFrame );
		this.fullFrame = fullFrame;
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of(
			Twig.of( "localVerificationTypes (" + fullFrame.localVerificationTypes.size() + " entries)",
				fullFrame.localVerificationTypes.stream().map( t -> renderingContext.newPrinter( t ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ),
			Twig.of( "stackVerificationTypes (" + fullFrame.stackVerificationTypes.size() + " entries)",
				fullFrame.stackVerificationTypes.stream().map( t -> renderingContext.newPrinter( t ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ) );
	}
}
