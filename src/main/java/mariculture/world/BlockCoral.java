package mariculture.world;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import mariculture.Mariculture;
import mariculture.api.core.MaricultureTab;
import mariculture.core.Core;
import mariculture.core.blocks.base.BlockDecorative;
import mariculture.core.config.Gardening;
import mariculture.core.helpers.BlockHelper;
import mariculture.core.lib.CoralMeta;
import mariculture.core.lib.RockMeta;
import mariculture.core.util.IHasMeta;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCoral extends BlockDecorative implements IPlantable, IHasMeta {
    protected BlockCoral(boolean tick, String prefix) {
        super(Material.water);
        float f = 0.375F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        setTickRandomly(tick);
        setCreativeTab(MaricultureTab.tabCore);
        this.prefix = prefix;
    }

    @Override
    public String getToolType(int meta) {
        return null;
    }

    @Override
    public int getToolLevel(int meta) {
        return 0;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) <= CoralMeta.KELP_MIDDLE ? 0.05F : 1F;
    }

    @Override
    public Item getItemDropped(int i, Random rand, int j) {
        return Item.getItemFromBlock(WorldPlus.plantStatic);
    }

    @Override
    public int damageDropped(int dmg) {
        return dmg == CoralMeta.KELP_MIDDLE ? CoralMeta.KELP : dmg;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
        Block block = world.getBlock(x, y - 1, z);
        int metaDown = world.getBlockMetadata(x, y - 1, z);
        if (!canSustainPlant(block, metaDown)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlock(x, y, z, Blocks.water);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (stack == null || stack.getItemDamage() <= CoralMeta.KELP_MIDDLE) {
            Block block = world.getBlock(x, y - 1, z);
            int meta = world.getBlockMetadata(x, y - 1, z);

            if (isKelpTop(block, meta)) {
                world.setBlockMetadataWithNotify(x, y - 1, z, CoralMeta.KELP_MIDDLE, 2);
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (meta <= CoralMeta.KELP_MIDDLE && isKelp(world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z))) {
            world.setBlockMetadataWithNotify(x, y - 1, z, CoralMeta.KELP, 2);
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 1;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Water;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    //Static Methods
    public static boolean canSustainPlant(Block block, int meta) {
        return meta <= CoralMeta.KELP_MIDDLE ? canSustainKelp(block, meta) : canSustainCoral(block, meta);
    }

    public static boolean canSustainCoral(Block block, int meta) {
        return block == Core.sands || block == Core.limestone || block == Blocks.sand || block == Blocks.cobblestone || block == Blocks.mossy_cobblestone || block == Core.rocks && meta == RockMeta.CORAL_ROCK;
    }

    public static boolean canSustainKelp(Block block, int meta) {
        if (block == Blocks.cobblestone || block == Blocks.mossy_cobblestone || block == Blocks.sand || block == Blocks.gravel) return true;
        if (block == Core.rocks && meta == RockMeta.CORAL_ROCK || block == Core.limestone || block == Core.sands) return true;
        return isKelp(block, meta);
    }

    private static boolean isPlant(Block block) {
        return block == WorldPlus.plantGrowable || block == WorldPlus.plantStatic;
    }

    private static boolean isKelp(Block block, int meta) {
        return isPlant(block) && meta <= CoralMeta.KELP_MIDDLE;
    }

    private static boolean isKelpTop(Block block, int meta) {
        return isKelp(block, meta) && meta == CoralMeta.KELP;
    }

    /** End Block Data, Begin Functional Methods **/
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if (Gardening.KELP_GROWTH_ENABLED && meta == CoralMeta.KELP) {
                updateKelp(world, x, y, z, rand);
            } else if (Gardening.CORAL_SPREAD_ENABLED && meta > CoralMeta.KELP_MIDDLE) {
                updateCoral(world, x, y, z, rand);
            } else if (Gardening.MOSS_SPREAD_ENABLED && meta == CoralMeta.KELP_MIDDLE) {
                updateMoss(world, x, y, z, rand);
            }
        }
    }

    private void updateKelp(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(Gardening.KELP_GROWTH_CHANCE) == 0) {
            if (BlockHelper.isWater(world, x, y + 2, z)) {
                world.setBlock(x, y + 1, z, this);
                onBlockPlacedBy(world, x, y, z, null, null);
            }
        }
    }

    private void updateMoss(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(Gardening.KELP_SPREAD_CHANCE) == 0) {
            Block block = world.getBlock(x, y - 1, z);
            if (block == Blocks.mossy_cobblestone || block == Blocks.stonebrick && world.getBlockMetadata(x, y - 1, z) == 1) {
                int randX = rand.nextInt(8) - 4;
                int randZ = rand.nextInt(8) - 4;
                if (world.getBlock(randX, y + 1, randZ) == Blocks.water) {
                    Block theBlock = world.getBlock(randX, y - 1, randZ);
                    if (theBlock == Blocks.cobblestone) {
                        world.setBlock(x, y - 1, z, Blocks.mossy_cobblestone);
                    } else if (theBlock == Blocks.stonebrick && world.getBlockMetadata(x, y, z) != 1) {
                        world.setBlockMetadataWithNotify(x, y - 1, z, 1, 2);
                    }
                }
            }
        }
    }

    private void updateCoral(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(Gardening.CORAL_SPREAD_CHANCE) == 0) {
            Block block = world.getBlock(x, y - 1, z);
            if (block == Core.rocks && world.getBlockMetadata(x, y - 1, z) == RockMeta.CORAL_ROCK) {
                int randX = x + 1 + rand.nextInt(4) - 2 - rand.nextInt(2);
                int randY = y + rand.nextInt(3) - 1;
                int randZ = z + 1 + rand.nextInt(4) - 2 - rand.nextInt(2);
                if (world.getBlock(randX, randY, randZ) == WorldPlus.plantGrowable) {
                    int thisMeta = world.getBlockMetadata(x, y, z);
                    int thatMeta = world.getBlockMetadata(randX, randY, randZ);
                    int newMeta = getNewColor(thisMeta, thatMeta, rand);

                    randX = x + 1 + rand.nextInt(4) - 2 - rand.nextInt(2);
                    randY = y + rand.nextInt(3) - 1;
                    randZ = z + 1 + rand.nextInt(4) - 2 - rand.nextInt(2);
                    if (world.getBlock(randX, randY, randZ) == Blocks.water && world.getBlock(randX, randY + 1, randZ) == Blocks.water && canSustainCoral(world.getBlock(randX, randY - 1, randZ), world.getBlockMetadata(randX, randY - 1, randZ))) {
                        world.setBlock(randX, randY, randZ, WorldPlus.plantGrowable, newMeta, 2);
                    }
                }
            }
        }
    }

    private static HashMap<String, Integer[]> outcomes = new HashMap();
    static {
        outcomes.put(CoralMeta.RED + "|" + CoralMeta.WHITE, new Integer[] { CoralMeta.PINK, CoralMeta.YELLOW });
        outcomes.put(CoralMeta.RED + "|" + CoralMeta.LIGHT_BLUE, new Integer[] { CoralMeta.PURPLE, CoralMeta.MAGENTA });
        outcomes.put(CoralMeta.RED + "|" + CoralMeta.YELLOW, new Integer[] { CoralMeta.ORANGE, CoralMeta.BROWN });
        outcomes.put(CoralMeta.WHITE + "|" + CoralMeta.GREY, new Integer[] { CoralMeta.LIGHT_GREY });
        outcomes.put(CoralMeta.WHITE + "|" + CoralMeta.PURPLE, new Integer[] { CoralMeta.LIGHT_BLUE, CoralMeta.GREY, CoralMeta.LIGHT_GREY });
        outcomes.put(CoralMeta.WHITE + "|" + CoralMeta.LIGHT_BLUE, new Integer[] { CoralMeta.GREY });
        outcomes.put(CoralMeta.PURPLE + "|" + CoralMeta.RED, new Integer[] { CoralMeta.MAGENTA });
        outcomes.put(CoralMeta.ORANGE + "|" + CoralMeta.RED, new Integer[] { CoralMeta.YELLOW, CoralMeta.BROWN });
        outcomes.put(CoralMeta.ORANGE + "|" + CoralMeta.GREY, new Integer[] { CoralMeta.RED });
        outcomes.put(CoralMeta.ORANGE + "|" + CoralMeta.LIGHT_BLUE, new Integer[] { CoralMeta.RED });
        outcomes.put(CoralMeta.YELLOW + "|" + CoralMeta.GREY, new Integer[] { CoralMeta.RED });
        outcomes.put(CoralMeta.YELLOW + "|" + CoralMeta.LIGHT_GREY, new Integer[] { CoralMeta.WHITE });
    }

    private Integer getNewColor(int thisMeta, int thatMeta, Random rand) {
        Integer[] values = outcomes.get("" + thisMeta + "|" + thatMeta);
        if (values == null) {
            values = outcomes.get("" + thatMeta + "|" + thisMeta);
        }

        if (rand.nextInt(2) == 0) return thisMeta;
        if (rand.nextInt(2) == 0) return thatMeta;
        if (values != null) return values[rand.nextInt(values.length)];
        else return rand.nextInt(2) == 0 ? thisMeta : thatMeta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[getMetaCount()];

        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(Mariculture.modid + ":" + "coral_" + getName(i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        if (creativeTabs == MaricultureTab.tabWorld) {
            for (int meta = 0; meta < CoralMeta.COUNT; ++meta)
                if (meta != CoralMeta.KELP_MIDDLE) {
                    list.add(new ItemStack(item, 1, meta));
                }
        }
    }

    @Override
    public int getMetaCount() {
        return CoralMeta.COUNT;
    }

    @Override
    public Class<? extends ItemBlock> getItemClass() {
        return null;
    }
}
