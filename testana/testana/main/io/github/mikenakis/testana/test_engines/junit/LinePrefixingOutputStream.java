package io.github.mikenakis.testana.test_engines.junit;

import io.github.mikenakis.kit.Kit;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

class LinePrefixingOutputStream extends OutputStream
{
	static OutputStream of( OutputStream delegee, String prefix )
	{
		return new BufferedOutputStream( new LinePrefixingOutputStream( new BufferedOutputStream( delegee ), prefix ) );
	}

	private final OutputStream delegee;
	private boolean bol = true;
	private final byte[] prefix;

	private LinePrefixingOutputStream( OutputStream delegee, String prefix )
	{
		this.delegee = delegee;
		this.prefix = prefix.getBytes( StandardCharsets.UTF_8 );
	}

	@Override public void write( byte[] bytes, int off, int len ) throws IOException
	{
		while( len > 0 )
		{
			if( bol )
			{
				delegee.write( prefix, 0, prefix.length );
				bol = false;
			}
			int indexOfNewLine = Kit.bytes.indexOf( bytes, off, len, (byte)'\n' );
			if( indexOfNewLine == -1 )
			{
				delegee.write( bytes, off, len );
				break;
			}
			assert bytes[indexOfNewLine] == '\n';
			int n = indexOfNewLine - off + 1;
			delegee.write( bytes, off, n );
			off += n;
			len -= n;
			bol = true;
		}
	}

	@Override public void flush() throws IOException
	{
		delegee.flush();
	}

	@Override public void close() throws IOException
	{
		flush();
		delegee.close();
	}

	@Override public void write( int b )
	{
		assert false; //we do not expect this to be invoked because we are using a BufferedOutputStream.
	}
}
