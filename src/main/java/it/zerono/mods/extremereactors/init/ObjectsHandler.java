//package it.zerono.mods.extremereactors.init;
//
////import it.zerono.mods.extremereactors.gamecontent.ReactorGameData;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
//import it.zerono.mods.zerocore.lib.block.ModBlock;
//import it.zerono.mods.zerocore.lib.init.GameObjectsHandler;
//import it.zerono.mods.zerocore.lib.item.ModItem;
//import net.minecraft.block.Block;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.inventory.container.ContainerType;
//import net.minecraft.item.Item;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.tileentity.TileEntityType;
//import net.minecraftforge.common.extensions.IForgeContainerType;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//import net.minecraftforge.fml.network.IContainerFactory;
//import net.minecraftforge.registries.IForgeRegistry;
//
//import java.util.function.Supplier;
//
//public class ObjectsHandler extends GameObjectsHandler {
//
//    public ObjectsHandler() {
//    }
//
//    @SubscribeEvent
//    @SuppressWarnings({"unused", "ConstantConditions"})
//    public void onRegisterContainers(final RegistryEvent.Register<ContainerType<?>> event) {
//
////        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> ModTileContainer.empty(Holder.ContainerTypes.REACTOR_CONTROLLER, windowId,
////                WorldHelper.<ReactorControllerEntity>getClientTile(data.readBlockPos()).orElseThrow(NullPointerException::new)
////        )).setRegistryName(Names.ContainerTypes.REACTOR_CONTROLLER));
////
////
////        register(event.getRegistry(), ReactorSolidAccessPortContainer::new, Names.ContainerTypes.REACTOR_SOLID_ACCESSPORT);
////
////        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> ModTileContainer.empty(Holder.ContainerTypes.TEST, windowId,
////                WorldHelper.<ReactorSolidAccessPortEntity>getClientTile(data.readBlockPos()).orElseThrow(NullPointerException::new)
////        )).setRegistryName(ExtremeReactors.MOD_ID + ":test"));
//
//    }
//
//    /**
//     * Create all the blocks instances for this mod and register them
//     * Override in your subclass to create your blocks instances and register them with the provided registry
//     *
//     * @param registry the block registry
//     */
//    @Override
//    protected void onRegisterBlocks(final IForgeRegistry<Block> registry) {
////
////        registerMetalBlocks(registry);
////        registerReactorParts(registry);
//    }
//
//    /**
//     * Register all the tile entities for this mod
//     *
//     * @param registry
//     */
//    @Override
//    protected void onRegisterTileEntities(final IForgeRegistry<TileEntityType<?>> registry) {
//
//        // reactor tile entities
//
////        registry.registerAll(
////                createTileType(ReactorCasingEntity::new, Names.TileEntities.REACTOR_CASING, Holder.Blocks.REACTOR_CASING_BASIC),
////                createTileType(ReactorGlassEntity::new, Names.TileEntities.REACTOR_GLASS, Holder.Blocks.REACTOR_GLASS_BASIC),
////                createTileType(ReactorControllerEntity::new, Names.TileEntities.REACTOR_CONTROLLER, Holder.Blocks.REACTOR_CONTROLLER_BASIC),
////                createTileType(ReactorFuelRodEntity::new, Names.TileEntities.REACTOR_FUELROD, Holder.Blocks.REACTOR_FUELROD_BASIC),
////                createTileType(ReactorControlRodEntity::new, Names.TileEntities.REACTOR_CONTROLROD, Holder.Blocks.REACTOR_CONTROLROD_BASIC),
////                createTileType(ReactorSolidAccessPortEntity::new, Names.TileEntities.REACTOR_SOLID_ACCESSPORT, Holder.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC),
////                //createTileType(ReactorCoolantPortEntity::new, Names.TileEntities.REACTOR_COOLANTPORT, Holder.Blocks.REACTOR_COOLANTPORT_BASIC),
////                //createTileType(ReactorCreativeCoolantPort::new, Names.TileEntities.REACTOR_CREATIVECOOLANTPORT, Holder.Blocks.REACTOR_CREATIVECOOLANTPORT_BASIC),
////                createTileType(() -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy), Names.TileEntities.REACTOR_POWERTAP_FE, Holder.Blocks.REACTOR_POWERTAP_FE_BASIC),
////                //createTileType(ReactorComputerPortEntity::new, Names.TileEntities.REACTOR_COMPUTERPORT, Holder.Blocks.REACTOR_COMPUTERPORT_BASIC),
////                createTileType(ReactorRedstonePortEntity::new, Names.TileEntities.REACTOR_REDSTONEPORT, Holder.Blocks.REACTOR_REDSTONEPORT_BASIC)
////        );
//    }
//
//    /**
//     * Create all the items instances for this mod and register them
//     * Override in your subclass to create your items instances and register them with the provided registry
//     *
//     * @param registry the Item(s) registry
//     */
//    @Override
//    @SuppressWarnings("ConstantConditions")
//    protected void onRegisterItems(final IForgeRegistry<Item> registry) {
//
////        final Item.Properties generalMax64 = new Item.Properties().group(ItemGroups.GENERAL).maxStackSize(64);
////
////        // general items
////
////        register(registry, generalMax64,
////                Names.Items.YELLORIUM_INGOT, Names.Items.YELLORIUM_DUST,
////                Names.Items.CYANITE_INGOT, Names.Items.CYANITE_DUST,
////                Names.Items.GRAPHITE_INGOT, Names.Items.GRAPHITE_DUST,
////
////                Names.Items.ANGLESITE_CRYSTAL, Names.Items.BENITOITE_CRYSTAL
////        );
////
////        // general item-blocks
////
////        register(registry, generalMax64,
////                Holder.Blocks.YELLORIUM_BLOCK, Holder.Blocks.CYANITE_BLOCK, Holder.Blocks.GRAPHITE_BLOCK);
////
////        // reactor item-blocks
////
////        register(registry, new Item.Properties().group(ItemGroups.REACTOR),
////                Holder.Blocks.REACTOR_CASING_BASIC, Holder.Blocks.REACTOR_GLASS_BASIC,
////                Holder.Blocks.REACTOR_CONTROLLER_BASIC, Holder.Blocks.REACTOR_FUELROD_BASIC,
////                Holder.Blocks.REACTOR_CONTROLROD_BASIC, Holder.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC,
////                Holder.Blocks.REACTOR_POWERTAP_FE_BASIC, Holder.Blocks.REACTOR_REDSTONEPORT_BASIC);
//    }
//
//    @Override
//    public void onCommonInit(FMLCommonSetupEvent event) {
//
//        super.onCommonInit(event);
////        ReactorGameData.register();
//    }
//
//    //region internals
//
//    private static void register(final IForgeRegistry<Item> registry, final Item.Properties properties, final ModBlock... blocks) {
//
//        for (final ModBlock block : blocks) {
//            registry.register(block.createBlockItem(properties));
//        }
//    }
//
//    private static void register(final IForgeRegistry<Item> registry, final Item.Properties properties, final String... names) {
//
//        for (final String name : names) {
//            registry.register(new ModItem(name, properties));
//        }
//    }
//
//    private static void register(final IForgeRegistry<Block> registry, final Block.Properties properties, final String... names) {
//
//        for (final String name : names) {
//            registry.register(new ModBlock(name, properties));
//        }
//    }
//
//    private static <T extends Container> void register(final IForgeRegistry<ContainerType<?>> registry,
//                                                       final IContainerFactory<T> factory, final String registryName) {
//        registry.register(IForgeContainerType.create(factory).setRegistryName(registryName));
//    }
//
//    private static void registerReactorParts(final IForgeRegistry<Block> registry) {
//
//        for (final ReactorVariant variant : ReactorVariant.values()) {
//            for (final ReactorPartType partType : ReactorPartType.values()) {
//
//                if (variant.isPartCompatible(partType)) {
//                    registry.register(partType.createBlock(variant));
//                }
//            }
//        }
//    }
//
//    private static void registerMetalBlocks(final IForgeRegistry<Block> registry) {
//
////        //TODO TEST
////        register(registry, Block.Properties.create(Material.IRON, DyeColor.LIME).sound(SoundType.METAL),
////                Names.Blocks.YELLORIUM_BLOCK,
////                Names.Blocks.CYANITE_BLOCK,
////                Names.Blocks.GRAPHITE_BLOCK
////        );
//    }
//
//    private static TileEntityType createTileType(final Supplier<? extends TileEntity> factory, final String name, Block... validBlocks) {
//        return TileEntityType.Builder.create(factory, validBlocks).build(null).setRegistryName(name);
//    }
//
//    //endregion
//}
