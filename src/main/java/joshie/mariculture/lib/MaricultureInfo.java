package joshie.mariculture.lib;

public class MaricultureInfo {
	public static final String JAVAPATH = "joshie.mariculture.";
	public static final String MODID = "Mariculture2";
	public static final String MODNAME = "Mariculture 2";
	public static final String MODPATH = "mariculture";
    public static final String INITIALS = "M";
    public static final String PENGUINCORE_VERSION = "2.0.0";
    public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "required-after:PenguinCore@[" + PENGUINCORE_VERSION + ",)";
    
    public static final String[] MODULES = new String[] { 
    	"diving", "exploration", "fishery", "sealife"
    };
}
