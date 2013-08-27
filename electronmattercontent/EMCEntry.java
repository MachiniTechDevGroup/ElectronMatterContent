package electronmattercontent;

import java.util.ArrayList;
import java.util.List;

public class EMCEntry {
	private int EMC = -1;// The EMC of the item
	private float Ref = 0.5f;// The refinedness of the item
	private List<Integer> up = new ArrayList<Integer>();// Stores the upgrades needed. For our Java 6 friends.

	public EMCEntry(int emc, float ref, int... up) {
		this.EMC = emc;
		this.Ref = ref;
		for (int i = 0; i < up.length; i++) {
			this.up.add(up[i]);
		}
	}

	public EMCEntry(int emc, float ref) {
		this(emc, ref, EMCUpgrade.NONE);
	}

	public EMCEntry(int emc, int... up) {
		this(emc, .5f, up);
	}

	public EMCEntry(int emc) {
		this(emc, .5f, EMCUpgrade.NONE);
	}
	
	public EMCEntry() {//Empty entry
		this(-1, .5f, EMCUpgrade.NONE);
	}

	public int getEMC() {
		return this.EMC;
	}

	public float getRef() {
		return this.Ref;
	}

	public List<Integer> getUps() {
		return this.up;
	}
	
	public boolean isEmptyEntry() {
		return this.EMC == -1;
	}

	public boolean hasUps() {
		return !(this.up.size() == 1 && this.up.get(0) == EMCUpgrade.NONE);
	}
	
	@Override
	public String toString() {
		return "EMC: " + this.EMC + " Ref: " + this.Ref + "Upgrades: " + this.up;
	}

}
