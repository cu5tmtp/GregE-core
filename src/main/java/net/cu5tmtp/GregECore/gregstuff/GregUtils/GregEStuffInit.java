package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import net.cu5tmtp.GregECore.gregstuff.GregMachines.AcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.GiantAcceleratedEBF;
import net.cu5tmtp.GregECore.gregstuff.GregMachines.GiantChemicalReactor;
import net.cu5tmtp.GregECore.item.ModItems;

public class GregEStuffInit {
    public static void initGregEMulti(){
        AcceleratedEBF.init();
        GiantAcceleratedEBF.init();
        GiantChemicalReactor.init();
    }
}
