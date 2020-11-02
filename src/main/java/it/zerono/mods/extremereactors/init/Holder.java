//package it.zerono.mods.extremereactors.init;
//
//import it.zerono.mods.extremereactors.ExtremeReactors;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.*;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControllerEntity;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodBlock;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortBlock;
//import it.zerono.mods.zerocore.lib.block.ModBlock;
//import it.zerono.mods.zerocore.lib.item.ModItem;
//import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
//import net.minecraft.inventory.container.ContainerType;
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.Item;
//import net.minecraft.tileentity.TileEntityType;
//import net.minecraftforge.registries.ObjectHolder;
//
//public final class Holder {
//
//    public static final class Blocks {
//
//        //region metals
//
//        @ObjectHolder(Names.Blocks.YELLORIUM_BLOCK)
//        public static final ModBlock YELLORIUM_BLOCK = null;
//
//        @ObjectHolder(Names.Blocks.CYANITE_BLOCK)
//        public static final ModBlock CYANITE_BLOCK = null;
//
//        @ObjectHolder(Names.Blocks.GRAPHITE_BLOCK)
//        public static final ModBlock GRAPHITE_BLOCK = null;
//
//        //endregion
//        //region reactor
//
//        @ObjectHolder(Names.Blocks.REACTOR_CASING_BASIC)
//        public static final CasingBlock<ReactorPartType> REACTOR_CASING_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_GLASS_BASIC)
//        public static final GlassBlock<ReactorPartType> REACTOR_GLASS_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_CONTROLLER_BASIC)
//        public static final ControllerBlock<ReactorPartType> REACTOR_CONTROLLER_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_FUELROD_BASIC)
//        public static final ReactorFuelRodBlock REACTOR_FUELROD_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_CONTROLROD_BASIC)
//        public static final GenericDeviceBlock<ReactorPartType> REACTOR_CONTROLROD_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC)
//        public static final IOPortBlock<ReactorPartType> REACTOR_SOLID_ACCESSPORT_BASIC = null;
//
//        //@ObjectHolder(Names.Blocks.REACTOR_COOLANTPORT_BASIC)
//        //public static final IOPortBlock<ReactorPartType> REACTOR_COOLANTPORT_BASIC = null;
//
//        //@ObjectHolder(Names.Blocks.REACTOR_CREATIVECOOLANTPORT_BASIC)
//        //public static final IOPortBlock<ReactorPartType> REACTOR_CREATIVECOOLANTPORT_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_POWERTAP_FE_BASIC)
//        public static final PowerTapBlock<ReactorPartType> REACTOR_POWERTAP_FE_BASIC = null;
//
//        //@ObjectHolder(Names.Blocks.REACTOR_COMPUTERPORT_BASIC)
//        //public static final ComputerPortBlock<ReactorPartType> REACTOR_COMPUTERPORT_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_REDSTONEPORT_BASIC)
//        public static final ReactorRedstonePortBlock REACTOR_REDSTONEPORT_BASIC = null;
//
//        //endregion
//    }
//
//    public static final class Items {
//
//        //region metals
//
//        @ObjectHolder(Names.Items.YELLORIUM_INGOT)
//        public static final ModItem YELLORIUM_INGOT = null;
//
//        @ObjectHolder(Names.Items.CYANITE_INGOT)
//        public static final ModItem CYANITE_INGOT = null;
//
//        @ObjectHolder(Names.Items.GRAPHITE_INGOT)
//        public static final ModItem GRAPHITE_INGOT = null;
//
//
//
//        @ObjectHolder(Names.Items.YELLORIUM_DUST)
//        public static final ModItem YELLORIUM_DUST = null;
//
//        @ObjectHolder(Names.Items.CYANITE_DUST)
//        public static final ModItem CYANITE_DUST = null;
//
//        @ObjectHolder(Names.Items.GRAPHITE_DUST)
//        public static final ModItem GRAPHITE_DUST = null;
//
//
//        @ObjectHolder(Names.Blocks.YELLORIUM_BLOCK)
//        public static final BlockItem YELLORIUM_BLOCK = null;
//
//        @ObjectHolder(Names.Blocks.CYANITE_BLOCK)
//        public static final BlockItem CYANITE_BLOCK = null;
//
//        @ObjectHolder(Names.Blocks.GRAPHITE_BLOCK)
//        public static final BlockItem GRAPHITE_BLOCK = null;
//
//        //endregion
//        //region crystals
//
//        @ObjectHolder(Names.Items.ANGLESITE_CRYSTAL)
//        public static final ModItem ANGLESITE_CRYSTAL = null;
//
//        @ObjectHolder(Names.Items.BENITOITE_CRYSTAL)
//        public static final ModItem BENITOITE_CRYSTAL = null;
//
//        //endregion
//
//        //region reactor
//        //region basic
//
//        @ObjectHolder(Names.Blocks.REACTOR_CASING_BASIC)
//        public static final Item REACTOR_CASING_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_GLASS_BASIC)
//        public static final Item REACTOR_GLASS_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_CONTROLLER_BASIC)
//        public static final Item REACTOR_CONTROLLER_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_FUELROD_BASIC)
//        public static final Item REACTOR_FUELROD_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_CONTROLROD_BASIC)
//        public static final Item REACTOR_CONTROLROD_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC)
//        public static final Item REACTOR_ACCESSPORT_BASIC = null;
//
//        //@ObjectHolder(Names.Blocks.REACTOR_COOLANTPORT_BASIC)
//        //public static final Item REACTOR_COOLANTPORT_BASIC = null;
//
//        //@ObjectHolder(Names.Blocks.REACTOR_CREATIVECOOLANTPORT_BASIC)
//        //public static final Item REACTOR_CREATIVECOOLANTPORT_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_POWERTAP_FE_BASIC)
//        public static final Item REACTOR_POWERTAP_BASIC = null;
//
//        //@ObjectHolder(Names.Blocks.REACTOR_COMPUTERPORT_BASIC)
//        //public static final Item REACTOR_COMPUTERPORT_BASIC = null;
//
//        @ObjectHolder(Names.Blocks.REACTOR_REDSTONEPORT_BASIC)
//        public static final Item REACTOR_REDSTONEPORT_BASIC = null;
//
//        //endregion
//        //endregion
//    }
//
//    public static final class TileEntityTypes {
//
//        @ObjectHolder(Names.TileEntities.REACTOR_CASING)
//        public static final TileEntityType<?> REACTOR_CASING = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_GLASS)
//        public static final TileEntityType<?> REACTOR_GLASS = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_CONTROLLER)
//        public static final TileEntityType<?> REACTOR_CONTROLLER = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_FUELROD)
//        public static final TileEntityType<?> REACTOR_FUELROD = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_CONTROLROD)
//        public static final TileEntityType<?> REACTOR_CONTROLROD = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_SOLID_ACCESSPORT)
//        public static final TileEntityType<?> REACTOR_SOLOD_ACCESSPORT = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_COOLANTPORT)
//        public static final TileEntityType<?> REACTOR_COOLANTPORT = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_CREATIVECOOLANTPORT)
//        public static final TileEntityType<?> REACTOR_CREATIVECOOLANTPORT = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_POWERTAP_FE)
//        public static final TileEntityType<?> REACTOR_POWERTAP_FE = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_COMPUTERPORT)
//        public static final TileEntityType<?> REACTOR_COMPUTERPORT = null;
//
//        @ObjectHolder(Names.TileEntities.REACTOR_REDSTONEPORT)
//        public static final TileEntityType<?> REACTOR_REDSTONEPORT = null;
//    }
//
//    public static final class ContainerTypes {
//
//        //region Reactor
//        @ObjectHolder(Names.ContainerTypes.REACTOR_CONTROLLER)
//        public static final ContainerType<ModTileContainer<ReactorControllerEntity>> REACTOR_CONTROLLER = null;
//
//        @ObjectHolder(Names.ContainerTypes.REACTOR_SOLID_ACCESSPORT)
//        public static final ContainerType<ReactorSolidAccessPortContainer> REACTOR_SOLID_ACCESSPORT = null;
//
//
//
//
//        @ObjectHolder(ExtremeReactors.MOD_ID + ":test")
//        public static final ContainerType<ReactorSolidAccessPortContainer> TEST = null;
//
//
//        //endregion
//    }
//}
