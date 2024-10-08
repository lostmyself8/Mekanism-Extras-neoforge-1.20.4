package com.jerry.mekextras.common.registry;

import com.jerry.mekextras.MekanismExtras;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.registration.impl.InfuseTypeDeferredRegister;
import net.neoforged.bus.api.IEventBus;

public class ExtraInfuseTypes {
    private ExtraInfuseTypes() {

    }

    public static final InfuseTypeDeferredRegister EXTRA_INFUSE_TYPES = new InfuseTypeDeferredRegister(MekanismExtras.MOD_ID);
    public static final DeferredChemical.DeferredInfuseType<InfuseType> RADIANCE = EXTRA_INFUSE_TYPES.register("radiance", 0xC4C604);
    public static final DeferredChemical.DeferredInfuseType<InfuseType> THERMONUCLEAR = EXTRA_INFUSE_TYPES.register("thermonuclear", 0x810C0C);
//    public static final DeferredChemical.DeferredInfuseType<InfuseType> SHINING = EXTRA_INFUSE_TYPES.register("shining", MekanismExtras.rl("infuse_type/shining"), 0xF5E8F6);
//    public static final DeferredChemical.DeferredInfuseType<InfuseType> SPECTRUM = EXTRA_INFUSE_TYPES.register("spectrum", MekanismExtras.rl("infuse_type/spectrum"), 0x74656A);
    public static final DeferredChemical.DeferredInfuseType<InfuseType> SHINING = EXTRA_INFUSE_TYPES.register("shining",0xFBE0FE);
    public static final DeferredChemical.DeferredInfuseType<InfuseType> SPECTRUM = EXTRA_INFUSE_TYPES.register("spectrum",0x1D1D29);

    public static void register(IEventBus eventBus) {
        EXTRA_INFUSE_TYPES.register(eventBus);
    }
}
