package io.github.mikenakis.bytecode.writing;

import io.github.mikenakis.bytecode.model.Constant;

public interface Interner
{
	void intern( Constant constant );
}
