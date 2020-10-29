# Chocolate

*Vanilla is good - but chocolate is better. ;)*

This mod exists to fix vanilla bugs - mostly to do with custom world generation - which have a much larger impact on modded.

Currently:

- Fixes [MC-202036](https://bugs.mojang.com/browse/MC-202036) : Biomes are saved to chunks as raw IDs, and can become shuffled when adding new biomes (either via data packs or mods), or the registration order of existing biomes changes. Chocolate fixes this by adding a palette to the chunk save data, and guards biome serialization with this registry key based approach.
- Fixes [MC-197616](https://bugs.mojang.com/browse/MC-197616) : Using a data pack biome with the "Single Biome" world preset causes massive log spam, and invalid biomes to be detected on client. This occurs in vanilla due to biomes not existing in the runtime dynamic registry instance. Chocolate fixes this by tracking biome registry keys, and transmuting biomes at runtime where nessecary into their current registry present objects.


