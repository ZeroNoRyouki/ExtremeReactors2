/*
 *
 * ExtremeReactorsCommand.java
 *
 * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.extremereactors.gamecontent.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.reactor.*;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterial;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ExtremeReactorsCommand {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {

        dispatcher.register(Commands.literal("er")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("reactants")
                        .then(Commands.literal("get")
                                .then(nameParam().executes(ExtremeReactorsCommand::getReactant))
                        )
                        .then(Commands.literal("set")
                                .then(nameParam()
                                        .then(stringCommand("colour", ExtremeReactorsCommand::setReactantColour))
                                        .then(floatCommand("moderation", 0.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getModerationFactor(),
                                                (Reactant r, Float v) -> r.getFuelData().setModerationFactor(v))))
                                        .then(floatCommand("absorption", 0.0f, 1.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getAbsorptionCoefficient(),
                                                (Reactant r, Float v) -> r.getFuelData().setAbsorptionCoefficient(v))))
                                        .then(floatCommand("hardness", 1.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getHardnessDivisor(),
                                                (Reactant r, Float v) -> r.getFuelData().setHardnessDivisor(v))))
                                        .then(floatCommand("fissionevents", 0.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getFissionEventsPerFuelUnit(),
                                                (Reactant r, Float v) -> r.getFuelData().setFissionEventsPerFuelUnit(v))))
                                        .then(floatCommand("fuelunits", 0.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getFuelUnitsPerFissionEvent(),
                                                (Reactant r, Float v) -> r.getFuelData().setFuelUnitsPerFissionEvent(v))))
                                )
                        )
                )
                .then(Commands.literal("moderators")
                        .then(Commands.literal("get")
                                .then(blockParam(buildContext).executes(ExtremeReactorsCommand::getModerator))
                        )
                        .then(Commands.literal("set")
                                .then(blockParam(buildContext)
                                        .then(floatCommand("absorption", 0.0f, 1.0f,
                                                context -> setModeratorValue(context, Moderator::getAbsorption, Moderator::setAbsorption)))
                                        .then(floatCommand("heatEfficiency", 0.0f, 1.0f,
                                                context -> setModeratorValue(context, Moderator::getHeatEfficiency, Moderator::setHeatEfficiency)))
                                        .then(floatCommand("moderation", 1.0f,
                                                context -> setModeratorValue(context, Moderator::getModeration, Moderator::setModeration)))
                                        .then(floatCommand("heatConductivity", 0.0f,
                                                context -> setModeratorValue(context, Moderator::getHeatConductivity, Moderator::setHeatConductivity)))
                                )
                        )
                )
                .then(Commands.literal("reaction")
                        .then(Commands.literal("get")
                                .then(nameParam().executes(ExtremeReactorsCommand::getReaction))
                        )
                        .then(Commands.literal("set")
                                .then(nameParam()
                                        .then(floatCommand("reactivity", 1.0f, context -> setReactionValue(context, "_reactivity", getFloat(context))))
                                        .then(floatCommand("fissionRate", 0.0001f, context -> setReactionValue(context, "_fissionRate", getFloat(context))))
                                )
                        )
                )
                .then(Commands.literal("coils")
                        .then(Commands.literal("get")
                                .then(tagIdParam().executes(ExtremeReactorsCommand::getCoil))
                        )
                        .then(Commands.literal("set")
                                .then(tagIdParam()
                                        .then(floatCommand("efficiency", 0.0f, context -> setCoilValue(context,
                                                CoilMaterial::getEfficiency, CoilMaterial::setEfficiency)))
                                        .then(floatCommand("bonus", 0.0f, context -> setCoilValue(context,
                                                CoilMaterial::getBonus, CoilMaterial::setBonus)))
                                        .then(floatCommand("energyExtractionRate", 0.0f, context -> setCoilValue(context,
                                                CoilMaterial::getEnergyExtractionRate, CoilMaterial::setEnergyExtractionRate)))
                                )
                        )
                )
        );
    }

    //region internals

    private ExtremeReactorsCommand() {
    }

    //region commands & parameters

    private static ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> stringCommand(final String propertyName,
                                                                                                                 final Command<CommandSourceStack> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_VALUE, StringArgumentType.string()).executes(cmd));
    }

    private static ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> floatCommand(final String propertyName,
                                                                                                                final float min,
                                                                                                                final Command<CommandSourceStack> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_VALUE, FloatArgumentType.floatArg(min)).executes(cmd));
    }

    private static ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> floatCommand(final String propertyName,
                                                                                                                final float min, final float max,
                                                                                                                final Command<CommandSourceStack> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_VALUE, FloatArgumentType.floatArg(min, max)).executes(cmd));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> nameParam() {
        return Commands.argument(PARAM_NAME, StringArgumentType.string());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, BlockInput> blockParam(CommandBuildContext context) {
        return Commands.argument(PARAM_BLOCK, BlockStateArgument.block(context));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> tagIdParam() {
        return Commands.argument(PARAM_TAG, ResourceLocationArgument.id());
    }

    private static String getName(final CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, PARAM_NAME);
    }

    private static BlockInput getBlock(final CommandContext<CommandSourceStack> context) {
        return BlockStateArgument.getBlock(context, PARAM_BLOCK);
    }

    private static ResourceLocation getTagId(final CommandContext<CommandSourceStack> context) {
        return ResourceLocationArgument.getId(context, PARAM_TAG);
    }

    private static String getString(final CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, PARAM_VALUE);
    }

    private static float getFloat(final CommandContext<CommandSourceStack> context) {
        return FloatArgumentType.getFloat(context, PARAM_VALUE);
    }

    //endregion
    //region reactants

    private static int getReactant(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(() -> ReactantsRegistry.get(getName(context))
                .map(ExtremeReactorsCommand::getTextFrom)
                .orElse(Component.literal("Reactant not found")), true);
        return 0;
    }

    private static int setReactantColour(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(() -> ReactantsRegistry.get(getName(context))
                .map(r -> setReactantColour(r, (int)Long.parseLong(getString(context), 16)))
                .orElse(Component.literal("Reactant not found")), true);
        return 0;
    }

    private static Component setReactantColour(final Reactant reactant, final int colour) {

        try {

            final Field f = reactant.getClass().getDeclaredField("_colour");

            f.setAccessible(true);
            f.set(reactant, Colour.fromRGBA(colour));
            return Component.literal(String.format("Reactant %s colour set to 0x%08X", reactant.getName(), colour));

        } catch (Exception ex) {

            Log.LOGGER.error(ex);
            return Component.literal("Exception raised while setting colour field");
        }
    }

    private static int setReactantFuelValue(final CommandContext<CommandSourceStack> context, final Function<Reactant, Float> getter,
                                            final BiConsumer<Reactant, Float> setter) {

        context.getSource().sendSuccess(() -> ReactantsRegistry.get(getName(context))
                .filter(r -> r.test(ReactantType.Fuel))
                .map(r -> setValue(r, getFloat(context), getter, setter))
                .orElse(Component.literal("Fuel Reactant not found")), true);
        return 0;
    }

    private static Component getTextFrom(final Reactant reactant) {

        final MutableComponent text = Component.literal(String.format("[" +
                        ChatFormatting.BOLD + "%s" + ChatFormatting.RESET + "] " + ChatFormatting.GOLD + "%s; " + ChatFormatting.RESET +
                        ChatFormatting.ITALIC + "color: " + ChatFormatting.RESET + "%08X",
                reactant.getType(), reactant.getName(), reactant.getColour().toRGBA()));

        if (reactant.test(ReactantType.Fuel)) {

            final FuelProperties properties = reactant.getFuelData();

            text.append(Component.literal(String.format("; " +
                            ChatFormatting.ITALIC + "moderation: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "absorption: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "hardness: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "fissionEventsPerFuelUnit: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "fuelUnitsPerFissionEvent: " + ChatFormatting.RESET + "%f",
                    properties.getModerationFactor(), properties.getAbsorptionCoefficient(), properties.getHardnessDivisor(),
                    properties.getFissionEventsPerFuelUnit(), properties.getFuelUnitsPerFissionEvent())));
        }

        return text;
    }

    //endregion
    //region moderators

    private static int getModerator(final CommandContext<CommandSourceStack> context) {

        final BlockState state = getBlock(context).getState();

        context.getSource().sendSuccess(() -> ModeratorsRegistry.getFrom(state)
                .map(moderator -> getTextFrom(state, moderator))
                .orElse(Component.literal("Moderator not found")), true);
        return 0;
    }

    private static int setModeratorValue(CommandContext<CommandSourceStack> context, final Function<Moderator, Float> getter,
                                         BiConsumer<Moderator, Float> setter) {

        final BlockState state = getBlock(context).getState();

        context.getSource().sendSuccess(() -> ModeratorsRegistry.getFrom(state)
                .map(r -> setValue(r, getFloat(context), getter, setter))
                .orElse(Component.literal("Moderator not found")), true);

        return 0;
    }
    private static Component getTextFrom(BlockState state, Moderator moderator) {
        return Component.literal("[")
                .append(Component.empty().withStyle(ChatFormatting.BOLD).append(state.getBlock().getName()))
                .append("] ")
                .append(Component.literal("absorption: ").withStyle(ChatFormatting.ITALIC))
                .append(String.format("%f; ", moderator.getAbsorption()))
                .append(Component.literal("heatEfficiency: ").withStyle(ChatFormatting.ITALIC))
                .append(String.format("%f; ", moderator.getHeatEfficiency()))
                .append(Component.literal("moderation: ").withStyle(ChatFormatting.ITALIC))
                .append(String.format("%f; ", moderator.getModeration()))
                .append(Component.literal("heatConductivity: ").withStyle(ChatFormatting.ITALIC))
                .append(String.format("%f; ", moderator.getHeatConductivity()));
    }

    //endregion
    //region reactions

    private static int getReaction(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(() -> ReactantsRegistry.get(getName(context))
                .flatMap(ReactionsRegistry::get)
                .map(ExtremeReactorsCommand::getTextFrom)
                .orElse(Component.literal("Reactant or reaction not found")), true);
        return 0;
    }

    private static Component getTextFrom(final Reaction reaction) {
        return Component.literal(String.format("[" +
                        ChatFormatting.BOLD + "%s" + ChatFormatting.RESET + " -> " +
                        ChatFormatting.BOLD + "%s" + ChatFormatting.RESET + "] " +
                        ChatFormatting.ITALIC + "reactivity: " + ChatFormatting.RESET + "%f; " +
                        ChatFormatting.ITALIC + "fissionRate: " + ChatFormatting.RESET + "%f",
                reaction.getSource(), reaction.getProduct(), reaction.getReactivity(), reaction.getFissionRate()));
    }

    private static int setReactionValue(final CommandContext<CommandSourceStack> context, final String fieldName, final float value) {

        context.getSource().sendSuccess(() -> ReactantsRegistry.get(getName(context))
                .flatMap(ReactionsRegistry::get)
                .map(reaction -> {
                    try {

                        final Field f = reaction.getClass().getDeclaredField(fieldName);

                        f.setAccessible(true);
                        f.set(reaction, value);
                        return Component.literal(String.format("Reaction %s parameter %s set to %f", reaction.getSource(), fieldName, value));

                    } catch (Exception ex) {

                        Log.LOGGER.error(ex);
                        return Component.literal(String.format("Exception raised while setting Reaction field %s", fieldName));
                    }
                })
                .orElse(Component.literal("Reactant or reaction not found")), true);
        return 0;
    }

    //endregion
    //region coils

    private static int getCoil(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(() -> getCoilByName(context)
                .map(ExtremeReactorsCommand::getTextFrom)
                .orElse(Component.literal("Coil not found")), true);
        return 0;
    }

    private static int setCoilValue(final CommandContext<CommandSourceStack> context, final Function<CoilMaterial, Float> getter,
                                    final BiConsumer<CoilMaterial, Float> setter) {

        context.getSource().sendSuccess(() -> getCoilByName(context)
                .map(c -> setValue(c, getFloat(context), getter, setter))
                .orElse(Component.literal("Coil not found")), true);
        return 0;
    }

    private static Component getTextFrom(final CoilMaterial coil) {
        return Component.literal(String.format(
                ChatFormatting.ITALIC + "efficiency: " + ChatFormatting.RESET + "%f; " +
                        ChatFormatting.ITALIC + "bonus: " + ChatFormatting.RESET + "%f; " +
                        ChatFormatting.ITALIC + "energyExtractionRate: " + ChatFormatting.RESET + "%f",
                coil.getEfficiency(), coil.getBonus(), coil.getEnergyExtractionRate()));
    }

    private static Optional<CoilMaterial> getCoilByName(final CommandContext<CommandSourceStack> context) {
        return CoilMaterialRegistry.get(TagsHelper.BLOCKS.createKey(getTagId(context)));
    }

    //endregion

    private static <T> Component setValue(final T data, final float value, final Function<T, Float> getter,
                                          final BiConsumer<T, Float> setter) {

        setter.accept(data, value);
        return Component.literal(String.format("Value set to %f", getter.apply(data)));
    }

    private static final String PARAM_NAME = "name";
    private static final String PARAM_TAG = "tag";
    private static final String PARAM_BLOCK = "block";
    private static final String PARAM_VALUE = "value";

    //endregion
}
