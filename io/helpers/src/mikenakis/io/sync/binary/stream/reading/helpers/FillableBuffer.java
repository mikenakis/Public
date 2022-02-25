package mikenakis.io.sync.binary.stream.reading.helpers;

import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.util.Optional;

/**
 * A fillable buffer.
 *
 * @author michael.gr
 */
final class FillableBuffer extends Mutable
{
	private final byte[] bytes;
	private int position = 0;
	private int length = 0;

	public FillableBuffer( MutationContext mutationContext, byte[] bytes )
	{
		super( mutationContext );
		this.bytes = bytes;
	}

	public byte[] getBytes()
	{
		assert canMutateAssertion();
		return bytes;
	}

	public int getLength()
	{
		assert canMutateAssertion();
		return length;
	}

	public int getFreeOffset()
	{
		assert canMutateAssertion();
		return position + length;
	}

	public int getFreeLength()
	{
		assert canMutateAssertion();
		return bytes.length - (position + length);
	}

	public Buffer readBuffer( int count )
	{
		assert canMutateAssertion();
		assert count > 0;
		assert count <= length;
		Buffer result = Buffer.of( bytes, position, count );
		skip( count );
		return result;
	}

	public Optional<Buffer> readUntilDelimiter( Buffer delimiter, boolean endHasBeenReached )
	{
		assert canMutateAssertion();
		assert delimiter.size() > 0;
		int skipCount = delimiter.size();
		int n = Buffer.indexOf( bytes, position, length, delimiter );
		if( n == -1 )
		{
			if( !endHasBeenReached )
				return Optional.empty();
			if( length == 0 )
				return Optional.empty();
			n = length;
			skipCount = 0;
		}
		else
			n -= position;
		Buffer result = n == 0 ? Buffer.EMPTY : readBuffer( n );
		if( skipCount > 0 )
			skip( skipCount );
		return Optional.of( result );
	}

	public void fill( int count )
	{
		assert canMutateAssertion();
		assert count > 0;
		assert count <= getFreeLength();
		length += count;
	}

	public boolean pack()
	{
		assert canMutateAssertion();
		if( position == 0 )
			return false;
		System.arraycopy( bytes, position, bytes, 0, length );
		position = 0;
		return true;
	}

	public int read( byte[] buffer, int index, int count )
	{
		assert canMutateAssertion();
		assert index >= 0;
		assert index < buffer.length;
		assert count > 0;
		assert index + count <= buffer.length;
		if( length == 0 )
			return 0;
		int chunkLength = Math.min( count, length );
		assert chunkLength > 0;
		System.arraycopy( bytes, position, buffer, index, chunkLength );
		skip( chunkLength );
		return chunkLength;
	}

	private void skip( int count )
	{
		assert count > 0;
		assert count <= length;
		position += count;
		length -= count;
		if( length == 0 )
			position = 0;
	}
}
