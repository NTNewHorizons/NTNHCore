package com.ntnh.ntnhcore;

import com.ntnh.ntnhcore.modules.ModuleManager;
import com.ntnh.ntnhcore.modules.tterrag.betterplacement.BetterPlacement;

public class ClientProxy extends CommonProxy {

    @Override
    protected void registerModules() {
        super.registerModules();
        ModuleManager.register(new BetterPlacement());
    }
}
