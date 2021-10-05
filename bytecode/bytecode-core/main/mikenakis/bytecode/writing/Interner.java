package mikenakis.bytecode.writing;

import mikenakis.bytecode.model.Constant;

public interface Interner
{
	void intern( Constant constant );
}
