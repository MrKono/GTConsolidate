## Changelog
### 1.3.3.3-beta
- **Fix** Circuit Factory's `Max Recipe Tier` calculation logic.
- **Fix** Intake Hatch recipe required wrong Soldering Alloy amount.
---

### 1.1.3.2-beta
- **Fix** _CoA Recipe_.
  - Fix wrong conveyor material
---

### 1.1.3.1-beta
- **Fix** _Circuit Factory nerf_.
  - Re-changing the `Max Recipe Tier` calculation logic.
  - Parallel processing is now enabled.
---

### 1.1.3-beta
- **Add** _Creative Rotor Holder_.
- **Add** _Intake Hatches_.
---

### 1.1.2.2-beta
- Industrial Primitive Machine now accepts Steam Item Bus
---

### 1.1.2.1-beta
- **Fix** Circuit Factory recipe.
---

### 1.1.2-beta
- **Add** _Multiblock "Large" Tank_.
- **Add** _Advanced Tank Valve_.
- **Refactor** some Multiblock.
  - _Turbo Rotary Hearth Blast Smelter_: The temperature no longer resets except when the controller is destroyed.
  - _Circuit Factory_: Changing the `Max Recipe Tier` calculation logic.
  - Change some display text.
  
---

### 1.1.1.2-beta
- **Fix** Long Magnetic Neodymium Rod could not be crafted by Polarizer.
- **Fix** CoA Recipe: Wrong HV Motor's material.
---

### 1.1.1.1-beta
- **Fix** typo.
- **Refactor** _Absolute Freezer_.
  - _**It no longer constantly consumes UEV.**_
- **Refactor** _Turbo Rotary Hearth Blast Smelter_.
  - _**It no longer resets Heat Capacity.**_
  - _**The increase in heat capacity now makes proper sense.**_
---

### 1.1.1-beta
- **New Machines**
  - Industrial Bricked Furnace (Parallelized Primitive Blast Furnace)
  - Industrial Coke Oven (Parallelized Coke Oven)
  - Ore Factory (port from [GTMoreOreProcessing](https://github.com/MrKono/GTCEu-MoreOreProcessing))
- **New Multiblock Parts** (_Whether to add it is configurable_)
  - LV and MV Rotor Holders (default: false)
  - UHV+ Rotor Holders (default: true)
  - Power-Enhanced Rotor Holders (default: true)
  - Speed-Enhanced Rotor Holders (default: true)
---

### 1.1.0.4.1-beta
- **Fix** conveyor recipe's fluid input (CoA)
--- 

### 1.1.0.4-beta
- **Fix** crash with GTExpertCore.
- **Re-balance** CoA recipe.
---

### 1.1.0.3-beta
- _Apply 1.0.8.4-beta_
---

### 1.1.0.2-beta
- _Apply 1.0.8.3-beta_
---

### 1.1.0.1-alpha
- **Fix** NPE when without Gregified Energistics
---

### 1.1.0-alpha
- Bump version to MixinBooter v10
  - Updated MixinBooter from v9.1 to v10.6
- **New integration**: Gregified Energistics
  - `ME Assembly Line Bus` and `ME Assembly Line Optical Bus` are now available in Parallelized Assembly Line.
---

### 1.0.8.4-beta
- _Component Assembly Line_
  - **Fix** numerous issues related to incorrect ingredients and ingredient counts. (Thanks to @sivaDog)
---

### 1.0.8.3-beta
- _Component Assembly Line_
  - :warning:**_WARNING_**:warning: Component recipe has undergone significant changes. Users of AE2 and similar mods should take note.
- **Fix** Circuit Assembler recipe (NAND Chip).
---

### 1.0.8.2-beta
- _Circuit Factory_
  - **Add** NAND Chip recipe using `PLASTIC_CIRCUIT_BOARD`.
  - Refactor Energy Hatch Limit. (4 -> 8)
  - Change OC Logic to _perfectOC_.
---

### 1.0.8.1-beta
- **Fix** crash or cannot open GUI when CircuitFactory was broken
---

### 1.0.8-beta
- **Add** _Display Text for Multiblock_.
- **Rework** _Circuit Factory_.
  - Fix missed intelligent.
  - Reduce duration and EU/t.
  - Remove T1 and T2 circuit recipe. (if you want to generate recipe, please modify cfg)
  - Add x128 and x256 recipe.
  - Relaxed the step-up conditions for the "Max Recipe Tier".
---

### 1.0.7.7-beta
- **Fix** _Absolute Freezer cannot chill_ `liquid metal` again and again.
- **Fix** _Circuit Factory cannot process any recipe_.
- **Fix** _some ABS recipes were not added to Turbo Rotary Hearth Blast Smelter when `GTExpertCore` is loaded_.
---

### 1.0.7.6-beta
- **Fix** _Absolute Freezer cannot chill_ `liquid metal` again.
---

#### 1.0.7.5-beta
- **Fix** _CoA recipe input_.
---

### 1.0.7.4-beta
- **Fix** _Turbo Rotary Hearth Blast Smelter cannot process ABS recipe_.
- **Fix** _recipe confit_ (Turbo Rotary Hearth Blast Smelter).
---

### 1.0.7.3-beta
- **Fix** _Turbo Rotary Hearth Blast Smelter does not use_ `blastFurnaceTemp()`.
- **Fix** _recipe confit_ (Turbo Rotary Hearth Blast Smelter).
- **Fix** _Absolute Freezer cannot chill_ `liquid metal`.
---

### 1.0.7.2-beta
- **Add** _extra tooltip_. (`Blocks that are necessary but not displayed in Multiblock Pattern`)
- **Refactor** _Absolute Freezer recipe_. (Processing only)
---

### 1.0.7.1-beta
- **Fix** _some lang key was missed_ ( #19 ).
- **Fix** _ja_jp.lang_.
- **Fix** _Tritanium Study Casing recipe_.
---

### 1.0.7-beta
- **Add** _more Parallel Hatches !!_ (Configurable, default: **false**).
- **Add** _Circuit Factory_.
- **Add** _Extended Processing Array_.
- **Refactor** _Large Greenhouse Structure_.
- _CoA now accepts 64A Hatch_.
---

### 1.0.6.2-beta
- **Refactor** _Adv.EBF_ and _Adv. AL_ tooltip.
----

### 1.0.6.1-beta
- **Fix** _crash when `highTierContent` is true_. (GTConsolidate#14)
---

### 1.0.6-beta
- **Fix** _invalided input_.
- **Add** _Turbo Rotary Hearth Blast Smelter_.
- **Add** _Absolute Freezer_.
---

### 1.0.5-beta
- **Add** _Component Assembly Line_.
- **MufflerHatch now available in Cleanroom!!** (Configurable)
- **Add** _Advanced Multi Smelter_.
- **Add** _Industrial Sawmill_.
---

### 1.0.4-beta
- **Add** _mode setting_ (configurable) ( #5 ). 
  - Parallelized EBF and VF are too OP, added mode setting for balance adjustment.
- **Fix** _Filtered Item Bus Glitch_ ( #6 ).
---

### 1.0.3.1-beta
- **Fix** _Large Greenhouse does not accept Fluid Export Hatch_
---

### 1.0.3-beta  
- **Add** _Large Greenhouse_
---

### 1.0.2.1-beta
- **Fix** _Filtered Item Bus issue_ (Thanks to report, @KatatsumuriPan)
---

### 1.0.2-beta
- **Add** _Filtered Input Bus_
- **Fix** _Parallelized Assembly Line Mark.3 does not work_
---

### 1.0.1.2-beta
- **Fix** _JEI Preview_
---

### 1.0.1.1-beta  
- **Fix** _assembly line casing recipe_
---

### 1.0.1-beta
- **Add** _Parallelized Assembly Line_
---

### 1.0.0-beta
:warning:**_WARNING_**:warning:  
**ID changes have been made to Advanced Fusion Reactor. It will disappear. :(**  

- **Add** _Parallelized Electric Blast Furnace_  
- **Add** _Parallelized Vacuum Freezer_
---

### 0.0.1-beta
**_Initial Release!_**<br>
- Add **Advanced Fusion Reactor**
