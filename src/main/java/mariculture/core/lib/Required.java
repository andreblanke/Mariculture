package mariculture.core.lib;

public class Required {
    private static final String forge = "10.12.1.1117";
    private static final String enchiridion = "1.1";
    private static final String blood_magic = "v1.0.1";
    private static final String bop = "2.0.0";
    public static final String after = "required-after:Forge@[" + forge + ",);required-after:Enchiridion@[" + enchiridion + ",);" + "before:Mariculture|API;" 
                                                + "after:AWWayofTime@[" + blood_magic + ",);after:BiomesOPlenty@[" + bop + ",);after:TConstruct;after:ThermalFoundation";
}
