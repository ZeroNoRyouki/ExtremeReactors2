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
import it.zerono.mods.extremereactors.api.reactor.FuelProperties;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.api.reactor.ReactantsRegistry;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ExtremeReactorsCommand {

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {

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
        );
    }

    //region internals

    private ExtremeReactorsCommand() {
    }

    private static ArgumentBuilder<CommandSource, LiteralArgumentBuilder<CommandSource>> stringCommand(final String propertyName,
                                                                                                       final Command<CommandSource> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_REACTANT_VALUE, StringArgumentType.string()).executes(cmd));
    }

    private static ArgumentBuilder<CommandSource, LiteralArgumentBuilder<CommandSource>> floatCommand(final String propertyName,
                                                                                                      final float min,
                                                                                                      final Command<CommandSource> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_REACTANT_VALUE, FloatArgumentType.floatArg(min)).executes(cmd));
    }

    private static ArgumentBuilder<CommandSource, LiteralArgumentBuilder<CommandSource>> floatCommand(final String propertyName,
                                                                                                      final float min, final float max,
                                                                                                      final Command<CommandSource> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_REACTANT_VALUE, FloatArgumentType.floatArg(min, max)).executes(cmd));
    }

    private static RequiredArgumentBuilder<CommandSource, String> nameParam() {
        return Commands.argument(PARAM_REACTANT_NAME, StringArgumentType.string());
    }

    private static String getName(final CommandContext<CommandSource> context) {
        return StringArgumentType.getString(context, PARAM_REACTANT_NAME);
    }

    private static String getString(final CommandContext<CommandSource> context) {
        return StringArgumentType.getString(context, PARAM_REACTANT_VALUE);
    }

    private static float getFloat(final CommandContext<CommandSource> context) {
        return FloatArgumentType.getFloat(context, PARAM_REACTANT_VALUE);
    }

    private static int getReactant(final CommandContext<CommandSource> context) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .map(ExtremeReactorsCommand::getTextFrom)
                .orElse(new StringTextComponent("Reactant not found")), true);
        return 0;
    }

    private static int setReactantColour(final CommandContext<CommandSource> context) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .map(r -> setReactantColour(r, (int)Long.parseLong(getString(context), 16)))
                .orElse(new StringTextComponent("Reactant not found")), true);
        return 0;
    }

    private static ITextComponent setReactantColour(final Reactant reactant, final int colour) {

        try {

            final Field f = reactant.getClass().getDeclaredField("_colour");

            f.setAccessible(true);
            f.set(reactant, Colour.fromRGBA(colour));
            return new StringTextComponent(String.format("Reactant %s colour set to 0x%08X", reactant.getName(), colour));

        } catch (Exception ex) {

            Log.LOGGER.error(ex);
            return new StringTextComponent("Exception raised while setting colour field");
        }
    }

    private static int setReactantFuelValue(final CommandContext<CommandSource> context, final Function<Reactant, Float> getter,
                                            final BiConsumer<Reactant, Float> setter) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .filter(r -> r.test(ReactantType.Fuel))
                .map(r -> setReactantFuelValue(r, getFloat(context), getter, setter))
                .orElse(new StringTextComponent("Fuel Reactant not found")), true);
        return 0;
    }

    private static ITextComponent setReactantFuelValue(final Reactant reactant, final float value,
                                                       final Function<Reactant, Float> getter,
                                                       final BiConsumer<Reactant, Float> setter) {

        setter.accept(reactant, value);
        return new StringTextComponent(String.format("Value set to %f", getter.apply(reactant)));
    }

    private static ITextComponent getTextFrom(final Reactant reactant) {

        final IFormattableTextComponent text = new StringTextComponent(String.format("[" +
                        TextFormatting.BOLD + "%s" + TextFormatting.RESET + "] " + TextFormatting.GOLD + "%s; " + TextFormatting.RESET +
                        TextFormatting.ITALIC + "color: " + TextFormatting.RESET + "%08X",
                reactant.getType(), reactant.getName(), reactant.getColour().toRGBA()));

        if (reactant.test(ReactantType.Fuel)) {

            final FuelProperties properties = reactant.getFuelData();

            text.append(new StringTextComponent(String.format("; " +
                            TextFormatting.ITALIC + "moderation: " + TextFormatting.RESET + "%f; " +
                            TextFormatting.ITALIC + "absorption: " + TextFormatting.RESET + "%f; " +
                            TextFormatting.ITALIC + "hardness: " + TextFormatting.RESET + "%f; " +
                            TextFormatting.ITALIC + "fissionEventsPerFuelUnit: " + TextFormatting.RESET + "%f; " +
                            TextFormatting.ITALIC + "fuelUnitsPerFissionEvent: " + TextFormatting.RESET + "%f",
                    properties.getModerationFactor(), properties.getAbsorptionCoefficient(), properties.getHardnessDivisor(),
                    properties.getFissionEventsPerFuelUnit(), properties.getFuelUnitsPerFissionEvent())));
        }

        return text;
    }

    private static final String PARAM_REACTANT_NAME = "name";
    private static final String PARAM_REACTANT_VALUE = "value";

    //endregion
}
