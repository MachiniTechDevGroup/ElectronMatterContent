package electronmattercontent;

public interface IEMCAccepter extends IEMCTile {
	public boolean demandsEMC();
	
	public int consumeFromEMCNet();
}
