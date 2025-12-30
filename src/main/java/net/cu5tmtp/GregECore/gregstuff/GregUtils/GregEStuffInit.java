package net.cu5tmtp.GregECore.gregstuff.GregUtils;

import net.cu5tmtp.GregECore.gregstuff.GregMachines.*;
import net.cu5tmtp.GregECore.item.ModItems;

public class GregEStuffInit {
    public static void initGregEMulti(){
        AcceleratedEBF.init();
        GiantAcceleratedEBF.init();
        GiantChemicalReactor.init();
        DysonSwarmLauncher.init();
        DysonSwarmEnergyCollector.init();

    }
}
