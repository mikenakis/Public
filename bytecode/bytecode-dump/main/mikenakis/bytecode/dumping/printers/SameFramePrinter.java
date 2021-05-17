package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.stackmap.Frame;
import mikenakis.bytecode.attributes.stackmap.SameFrame;

import java.util.Optional;

/**
 * {@link SameFrame} {@link FramePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class SameFramePrinter extends FramePrinter
{
	@SuppressWarnings( { "FieldCanBeLocal", "unused" } ) private final SameFrame sameFrame;

	public SameFramePrinter( SameFrame sameFrame, Optional<Frame> previousFrame )
	{
		super( sameFrame, previousFrame );
		this.sameFrame = sameFrame;
	}
}
