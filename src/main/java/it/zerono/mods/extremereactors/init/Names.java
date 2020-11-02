//package it.zerono.mods.extremereactors.init;
//
//import it.zerono.mods.extremereactors.ExtremeReactors;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
//
//public final class Names {
//
//    public static final class Blocks {
//
//        //region metals
//        public static final String YELLORIUM_BLOCK = ExtremeReactors.MOD_ID + ":yellorium_block";
//        public static final String CYANITE_BLOCK = ExtremeReactors.MOD_ID + ":cyanite_block";
//        public static final String GRAPHITE_BLOCK = ExtremeReactors.MOD_ID + ":graphite_block";
//        //endregion
//        //region reactor
//        //region basic
//        public static final String REACTOR_CASING_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorcasing";
//        public static final String REACTOR_GLASS_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorglass";
//        public static final String REACTOR_CONTROLLER_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorcontroller";
//        public static final String REACTOR_FUELROD_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorfuelrod";
//        public static final String REACTOR_CONTROLROD_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorcontrolrod";
//        public static final String REACTOR_SOLID_ACCESSPORT_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorsolidaccessport";
//        //public static final String REACTOR_COOLANTPORT_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorcoolantport";
//        //public static final String REACTOR_CREATIVECOOLANTPORT_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorcreativecoolantport";
//        public static final String REACTOR_POWERTAP_FE_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorpowertapfe";
//        public static final String REACTOR_COMPUTERPORT_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorcomputerport";
//        public static final String REACTOR_REDSTONEPORT_BASIC = ExtremeReactors.MOD_ID + ":basic_reactorredstoneport";
//        //endregion
//        //endregion
//    }
//
//    public static final class Items {
//
//        //region metals
//        public static final String YELLORIUM_INGOT = ExtremeReactors.MOD_ID + ":yellorium_ingot";
//        public static final String CYANITE_INGOT = ExtremeReactors.MOD_ID + ":cyanite_ingot";
//        public static final String GRAPHITE_INGOT = ExtremeReactors.MOD_ID + ":graphite_ingot";
//
//        public static final String YELLORIUM_DUST = ExtremeReactors.MOD_ID + ":yellorium_dust";
//        public static final String CYANITE_DUST = ExtremeReactors.MOD_ID + ":cyanite_dust";
//        public static final String GRAPHITE_DUST = ExtremeReactors.MOD_ID + ":graphite_dust";
//
//        //endregion
//        //region crystals
//        public static final String ANGLESITE_CRYSTAL = ExtremeReactors.MOD_ID + ":anglesite_crystal";
//        public static final String BENITOITE_CRYSTAL = ExtremeReactors.MOD_ID + ":benitoite_crystal";
//        //endregion
//    }
//
//    public static final class TileEntities {
//
//        //region reactor
//        public static final String REACTOR_CASING = ExtremeReactors.MOD_ID + ":reactorcasing";
//        public static final String REACTOR_GLASS = ExtremeReactors.MOD_ID + ":reactorglass";
//        public static final String REACTOR_CONTROLLER = ExtremeReactors.MOD_ID + ":reactorcontroller";
//        public static final String REACTOR_FUELROD = ExtremeReactors.MOD_ID + ":reactorfuelrod";
//        public static final String REACTOR_CONTROLROD = ExtremeReactors.MOD_ID + ":reactorcontrolrod";
//        public static final String REACTOR_SOLID_ACCESSPORT = ExtremeReactors.MOD_ID + ":reactoraccessport";
//        public static final String REACTOR_COOLANTPORT = ExtremeReactors.MOD_ID + ":reactorcoolantport";
//        public static final String REACTOR_CREATIVECOOLANTPORT = ExtremeReactors.MOD_ID + ":reactorcreativecoolantport";
//        public static final String REACTOR_POWERTAP_FE = ExtremeReactors.MOD_ID + ":reactorpowertap_fe";
//        public static final String REACTOR_COMPUTERPORT = ExtremeReactors.MOD_ID + ":reactorcomputerport";
//        public static final String REACTOR_REDSTONEPORT = ExtremeReactors.MOD_ID + ":reactorredstoneport";
//        //endregion
//    }
//
//    public static final class ContainerTypes {
//
//        //region reactor
//        public static final String REACTOR_CONTROLLER = TileEntities.REACTOR_CONTROLLER;
//        public static final String REACTOR_SOLID_ACCESSPORT = TileEntities.REACTOR_SOLID_ACCESSPORT;
//
//        //endregion
//    }
//
//    public static String forGeneric(final String blockName) {
//        return ExtremeReactors.MOD_ID + ":" + blockName;
//    }
//
//    public static String forReactorPart(final ReactorPartType partType, ReactorVariant variant) {
//        return ExtremeReactors.MOD_ID + ":" + variant.getName() + "_reactor" + partType.getNameForId();
//    }
//}
