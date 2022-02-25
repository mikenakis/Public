package mikenakis.io.test.t01_sync;

import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A Linear Congruential Pseudo-Random Number Generator for when speed is of paramount importance.
 *
 * @author michael.gr
 */
public final class RandomNumberGenerator extends Mutable
{
	private static final int BITS = 48;
	private static final long MULTIPLIER = 0x5_DEEC_E66DL;
	private static final long ADDEND = 0xBL;
	private static final long MASK = (1L << BITS) - 1;
	private long seed;

	public RandomNumberGenerator( MutationContext mutationContext )
	{
		super( mutationContext );
		setSeed( 1 );
	}

	public void setSeed( int seed )
	{
		assert canMutateAssertion();
		this.seed = (seed ^ MULTIPLIER) & MASK;
	}

	public int nextBits( int bits )
	{
		assert canMutateAssertion();
		assert bits > 0;
		assert bits < BITS;
		seed *= MULTIPLIER;
		seed += ADDEND;
		seed &= MASK;
		return (int)(seed >>> (BITS - bits));
	}

	public int nextInt()
	{
		return nextBits( 31 );
	}
}
