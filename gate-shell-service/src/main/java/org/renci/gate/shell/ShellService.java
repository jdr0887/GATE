package org.renci.gate.shell;

public interface ShellService {

	public ShellOutput run(ShellInput bean) throws ShellException;	
	
}
