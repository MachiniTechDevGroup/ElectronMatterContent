package electronmattercontent;

import net.minecraft.inventory.IInventory;

public class SlotEMCIn extends SlotEMC
{
    public SlotEMCIn(IInventory par1iInventory, int par2, int par3, int par4)
    {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 64;
    }
}
