package tconstruct.util;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TDispenserBehaviorArrow extends BehaviorDefaultDispenseItem
{

    @Override
    public ItemStack dispenseStack (IBlockSource dispenser, ItemStack stack)
    {
        World world = dispenser.getWorld();
        // TODO getIPositionFromBlockSource
        IPosition iposition = BlockDispenser.func_149939_a(dispenser);
        // TODO getFacing
        EnumFacing enumfacing = BlockDispenser.func_149937_b(dispenser.getBlockMetadata());

        ItemStack arrowItem = stack.splitStack(1);

     

        return stack;
    }

    @Override
    protected void playDispenseSound (IBlockSource dispenser)
    {
        dispenser.getWorld().playAuxSFX(1002, dispenser.getXInt(), dispenser.getYInt(), dispenser.getZInt(), 0);
    }

    protected float func_82498_a ()
    {
        return 6.0F;
    }

    protected float func_82500_b ()
    {
        return 1.1F;
    }
}
