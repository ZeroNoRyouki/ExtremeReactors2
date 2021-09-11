**Extreme Reactors ModPack API**

Modpacks authors can now access the Extreme Reactors API without the need of an external mod: all you need to do is create a file name modpack_api.json in ER config directory and fill it out following your specific needs. An example file can be found alongside this README.

You can use the ER API to add reactants or moderators to be used in a Reactor or a new Turbine coil for example.

**_The modpack_api.json file_**

The modpack_api.json is divided in sections, one for each aspect of the ER API. The "Enabled" property at the beginning of the file will determine if this file is processed (true) or not (false).
You can leave out any sections that you don't need.

The ER API is heavily based on Minecraft Tags so a good understanding on how those works is needed.

Any section is composed by three properties:

- "WipeExistingValuesBeforeAdding" : if true, **ALL** existing value will be removed automatically before adding the new ones provided by this section. Use with caution.
- "Add" : an array of new value to add.
- "Remove" : an array of names (strings). Any existing value which name match a name in this array will be removed.

If you add an element with the same name of an existing one the existing one will be replaced.

_**The coolants system**_

A Reactor equipped with Fluid Ports do not generate energy directly but vaporize a valid coolant in its vapor counterpart. The vapor is then used to generate power, usually in a Turbine.

Coolants and vapors need to be registered before they can be actually used in a Reactor (or Turbine) and a source fluid for them should be added too. The source fluid is what you then pump into a Fluid Port.

A Reactor vaporize a coolant to its registered vapor and a Turbine will condensate a vapor into its registered coolant. Those transitions need to be registered too, making the coolant system capable to handle your new coolants and vapors.

Extreme Reactors by default add the following entries to the coolant system:

- a coolant named "water"
- a vapor named "steam"
- any fluids tagged as "water" is added as a source for the "water" coolant
- any fluids tagged as "steam" is added as a source for the "steam" vapor
- a 1:1 transition is added to vaporize "water" into "steam" and condensate "steam" into "water"

_Coolants_

Manage the list of registered Coolants.
The properties of the items you can add are:

- BoilingPoint : the boiling point (in Celsius) of the coolant (a float number)
- EnthalpyOfVaporization : the amount of energy needed to transform the coolant into a gas (a float number)
- Name : the name for this coolant (a text string without spaces)
- TranslationKey : the translation key for the "in-game" name of the coolant

_Vapors_

Manage the list of registered Vapors.
The properties of the items you can add are:

- FluidEnergyDensity : the energy density of this vapor, in FE per mB (a float number)
- Name : the name for this vapor (a text string without spaces)
- TranslationKey : the translation key for the "in-game" name of the vapor

_CoolantSources_

Manage the list of registered fluid sources for a Coolant.
The properties of the items you can add are:

- SourceTagId : the fluid tag id (in the form of modid:path) for the source fluid
- ProductName : the name of the coolant that this source produce (must be the name of a registered coolant)
- ProductQuantity : the amount of coolant produced by one unit of this source

_VaporSources_

Manage the list of registered fluid sources for a Vapor.
The properties of the items you can add are:

- SourceTagId : the fluid tag id (in the form of modid:path) for the source fluid
- ProductName : the name of the vapor that this source produce (must be the name of a registered vapor)
- ProductQuantity : the amount of vapor produced by one unit of this source

_FluidTransitions_

Manage the list of registered transition between coolants and vapors (and vice versa).
The properties of the items you can add are:

- Source : the name of the coolant (must be the name of a registered coolant)
- SourceQuantity : the amount of coolant to vaporize to produce the vapor
- Product : the name of the vapor (must be the name of a registered vapor)
- ProductQuantity : the amount of vapor produced by vaporizing the coolant

A condensation transition (vapor to coolant) will be automatically added for you


_**The Reactor**_

Basically, the Reactor irradiate reactants contained in its Fuel Rod to produce heat and the heat is then used to produce energy or vaporize coolants to produce vapors.

Moderators are blocks that can be placed in the internal Reactor volume to moderate the radiation emitted by the Fuel Rods

_ReactorReactants_

Manage the list of registered reactants (fuels or wastes).
The properties of the items you can add are:

- Name : the name for this reactant (a text string without spaces)
- TranslationKey : the translation key for the "in-game" name of the reactant
- IsFuel : true if this reactant is a fuel, false if it is a waste product.
- RgbColour : a color in RGB form associated with this reactant
- FuelProperties : the properties of this fuel (see next section). If this reactant is not a fuel or if you want to give it the default properties, you can omit this value entirely. 

_FuelProperties_

Define the properties of a fuel reactant, as follows:

- ModerationFactor : how well this fuel moderate, but not stop, radiation. Must be greater or equal to 1. Anything under 1.5 is "poor", 2-2.5 is "good", above 4 is "excellent". (a float number)
- AbsorptionCoefficient : how well this fuel absorbs radiation. Must be between 0 and 1. (a float number)
- HardnessDivisor : how this fuel tolerates hard radiation. Must be greater or equal to 1. (a float number)

_ReactorReactantSources_

Manage the list of registered item tags that are a source of a reactant.
The properties of the items you can add are:

- SourceTagId : the item tag id (in the form of modid:path) for the source item
- ProductName : the name of the reactant that this source produce (must be the name of a registered reactant)
- ProductQuantity : the amount of reactant produced by one unit of this source

_ReactorReactantReaction_

Manage the list of registered reactions that will convert spent fuel in it's associated waste.
The properties of the items you can add are:

- SourceReactant : the name of the fuel reactant (must be the name of a registered reactant)
- ProductReactant : the name of the waste reactant (must be the name of a registered reactant)
- Reactivity : the reactivity of the reactant (a float number)
- FissionRate : the fission rate of the reactant (a float number)

_ReactorSolidModerators_

Manage the list of registered solid (blocks) moderators.
The properties of the items you can add are:

- TagId : the block tag id (in the form of modid:path) for the moderator block
- Absorption : how much radiation this material absorbs and converts to heat (0.0 = none, 1.0 = all). (a float number)
- HeatEfficiency : how efficiently radiation is converted to heat (0.0 = no heat, 1.0 = all heat). (a float number)
- Moderation : how well this material moderates radiation. This is a divisor; should not be below 1.0. (a float number)
- HeatConductivity : how well this material conducts heat, in FE/t/m2. (a float number)

_ReactorFluidModerators_

Same as ReactorSolidModerators, but for fluids. 
Instead of block tags ids you should provide the fluid registration id (for example, "minecraft:water" for the vanilla water fluid in still form)
Keep in mind that if you want both the still and flowing version of the fluid to act as a moderator, you have to add both. This let you keep full control on what fluid is allowed and let you assign different values to each fluid version.

_**The Turbine**_

The Turbine use metal coils placed around it's Rotor to produce energy

_TurbineCoils_

Manage the list of registered coils.
The properties of the items you can add are:

- BlockTagId : the block tag id (in the form of modid:path) for the coil block
- Efficiency : the efficiency of the block (1.0 == iron, 2.0 == gold, etc). (a float number)
- Bonus : energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks! (a float number)
- ExtractionRate : the energy extraction rate (a float number)










