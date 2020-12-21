# AE2 Fluid Crafting

Everybody loves AE2 autocrafting!
When the need arises to procure a couple thousand 64k disks, there's no better solution than to make a crafting CPU do the work for you!

However, if you want to craft a couple thousand [Cryo-Stabilized Fluxducts](https://ftb.gamepedia.com/Cryo-Stabilized_Fluxduct), you're out of luck.
These fancy-looking knockoff kinesis pipes are crafted by transposing half a bucket of [Gelid Cryotheum](https://ftb.gamepedia.com/Gelid_Cryotheum_(Thermal_Foundation)) into a frame.
The problem here is that AE2 does not natively support fluids as first-class crafting ingredients and is thus unable to handle recipes with a quantity of fluid as an ingredient.

**AE2 Fluid Crafting** is a mod that offers an experimental solution: by treating fluids as items, it attempts to reconcile fluid ingredients with the existing item-based autocrafting algorithm.
This document will provide a brief overview of how this is done.

## Cooking Instructions

You'll want to craft two apparatus: the **ME Fluid Discretizer** and the **Fluid Pattern Encoder**.

### Fluid Discretizer

The **Fluid Discretizer** is a device that, when attached to an ME network, exposes the contents of its fluid storage grid as items, which take the form of "fluid drops".
It does this by functioning as a sort of storage bus: when fluid drops are removed from its storage via the item grid, it extracts the corresponding fluid from the fluid grid.
Conversely, when fluid drops are inserted into its storage via the item grid, it injects the corresponding fluid into the fluid grid.

Each fluid drop is equivalent to one mB of its respective fluid, which means a full stack of them is equivalent to 64 mB.
Fluid drops have an important property: when an ME interface attempts to export fluid drops to a machine, it will attempt to convert them to fluid.
This means an interface exporting drops of gelid cryotheum into a fluid transposer will successfully fill the transposer's internal tank rather than inserting the drops as items.
This is the central mechanic that makes fluid autocrafting possible.

Note that the only way to convert between fluids and fluid drops is a discretizer attached to an ME network.
While you could theoretically use this as a very convoluted method of transporting fluids, it is not recomomended to do so.

### Fluid Pattern Encoder

Most crafting recipes involving fluids require far more than 64 mB of a particular fluid, and so the standard pattern terminal will not do for encoding such recipes into patterns.
This problem is solved by the **Fluid Pattern Encoder**, a utility that functions similarly to a pattern terminal.
When a fluid-handling item (e.g. a bucket or tank) is inserted into the crafting ingredient slots, they will be converted into an equivalent stack of the corresponding fluid drops.
Using this, patterns for recipes that require more than a stack of fluid drops can easily be encoded.

AE2 Fluid Crafting also comes with a handy JEI integration module that allows the fluid pattern encoder to encode any JEI recipe involving fluids.
This is the recommended way to play with the mod, since encoding patterns by hand is a little cumbersome.

## Other Utilities

### Fluid Pattern Terminal

Encoding recipes in a big and bulky workbench separate from the rest of your AE2 equipment can be a little inconvenient.
Luckily, we have the **Fluid Pattern Terminal**, which combines the functionality of the standard pattern terminal and the fluid pattern encoder.
Now, you can encode your fluid recipes using the same familiar interface you know and love!

### Dual Interface

The standard ME interface lets you emit both items and fluids with AE2FC, but it will only accept items, as in vanilla AE2.
This is a little inconvenient when you want to build compact setups for autocrafting with fluid outputs, where you would need to use both an item interface for inputs and a separate fluid interface for outputs.
To make things easier, we have the **Dual Interface**, which functions as a combination of an item interface and a fluid interface!
Using a button in its GUI, you can switch between its item and fluid GUIs, allowing you to configure import and export rules for both items and fluids.
As a bonus, the dual interface even appears in the interface terminal, so you can easily access it from your central network hub.
Automating fluid crafting machines has never been this quick and painless!

### Ingredient Buffer

Sometimes, it becomes necessary to route different fluids and items in a single recipe to separate blocks—for example, when autocrafting with the GregTech assembly line.
This is a problem, as the ME Interface will only export to a block if it can export its entire inventory at once.
A simple solution is to use some sort of buffer as the destination for the interface, then piping the fluids and items out of that buffer as needed.
To this end, we have the **Ingredient Buffer**, a useful device that functions as both a chest and a tank.

The ingredient buffer can store up to nine item stacks and four tanks of fluid in its inventory.
It doesn't automatically export, so you'll need to use some external means of extracting items and fluids.
All tanks and item slots are accessible from any side of the device, so filtered pipes are recommended for separating the contents.
If you end up with fluid in the buffer that you don't want, you can void the contents of the tanks from the GUI.

### Precision Burette

If, for some reason, you need to be able to encode a recipe using a weird amount of fluid, you can use the **Precision Burette**, a device that transfers fluids in configurable quantities.
This device has an internal tank that holds 8 buckets of liquid and one slot of storage, where a fluid tank item can be placed.
From the GUI, a specific quantity of fluid can be specified and transferred between the internal tank and the item.
As with the ingredient buffer, excess fluid can be voided with a button in the GUI.

It's worth noting that the need for this device is largely superseded by the JEI integration module.

### Fluid Packets

This mod being experimental, it is possible that fluid insertion might sometimes fail, leaving you with a weird item called a "fluid packet".
This item is used internally by AE2 Fluid Crafting to represent bundled quantities of fluid and is of absolutely no use otherwise.
If you somehow manage to get your hands on one, you can use an **ME Fluid Packet Decoder** to convert them back into usable fluid.
Simply connect the decoder to your ME network and insert the fluid packet; the decoder will, if possible, inject the fluid into your fluid storage grid.

## PackagedAuto Integration

Some funky recipes require the use of more than 9 distinct inputs—recipes for big, fancy machines like the GregTech assembly line.
For such an item recipe, one might consider using the popular addon [PackagedAuto](https://github.com/TheLMiffy1111/PackagedAuto), which allows for the bundling of multiple inputs into one "package".
Now, AE2FC has its own PackagedAuto integration module to bring you the very same ingredient-packaging action that you know and love!
Simply toggle your package recipe encoder to the shiny new "fluid" recipe type and use the same ol' JEI integration to specify a funky recipe.
AE2FC will do the rest; all you need to do is pump your ingredients into a packager and you'll get them back out at an unpackager, duty-free.
Just like magic!

## Notable Contributors

* KilaBash (@Yefancy) - Implemented the dual interface and fluid pattern terminal.
