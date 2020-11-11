/*
 *
 * TurbineBlockStateGenerator.java
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

package it.zerono.mods.extremereactors.datagen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class TurbineBlockStateGenerator
        extends AbstractMultiblockBlockStateGenerator {

    public TurbineBlockStateGenerator(final DataGenerator gen, final ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper);
    }

    //region IDataProvider

    @Nonnull
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Turbine blockstates and models";
    }

    @Override
    protected void registerStatesAndModels() {

        String variant;

        //region basic

        variant = "turbine/basic";
        this.genAssembledPlatingModel(variant);
        this.genFrame(Content.Blocks.TURBINE_CASING_BASIC, "casing", variant);
        this.genGlass(Content.Blocks.TURBINE_GLASS_BASIC, "glass", variant);
        this.genController(Content.Blocks.TURBINE_CONTROLLER_BASIC, variant);

        //TURBINE_ROTORBEARING_BASIC
        //TURBINE_ROTORSHAFT_BASIC
        //TURBINE_ROTORBLADE_BASIC

        this.genRedstonePort(Content.Blocks.TURBINE_REDSTONEPORT_BASIC, variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC, "powertap_fe_active", variant, true);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC, "powertap_fe_passive", variant, true);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, "coolantport_forge_active", variant);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC, "coolantport_forge_passive", variant);

        //endregion
        //region reinforced

        variant = "turbine/reinforced";
        this.genAssembledPlatingModel(variant);
        this.genFrame(Content.Blocks.TURBINE_CASING_REINFORCED, "casing", variant);
        this.genGlass(Content.Blocks.TURBINE_GLASS_REINFORCED, "glass", variant);
        this.genController(Content.Blocks.TURBINE_CONTROLLER_REINFORCED, variant);

        //TURBINE_ROTORBEARING_REINFORCED
        //TURBINE_ROTORSHAFT_REINFORCED
        //TURBINE_ROTORBLADE_REINFORCED

        this.genRedstonePort(Content.Blocks.TURBINE_REDSTONEPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED, "powertap_fe_active", variant, true);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED, "powertap_fe_passive", variant, true);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, "coolantport_forge_active", variant);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED, "coolantport_forge_passive", variant);
        this.genComputerPort(Content.Blocks.TURBINE_COMPUTERPORT_REINFORCED, variant);

        //endregion
    }

    //endregion
    //region internals


    //endregion
}
