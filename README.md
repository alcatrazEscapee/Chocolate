# Chocolate

*Vanilla is good - but chocolate is better. ;)*

This mod exists to fix vanilla bugs - mostly to do with custom world generation - which have a much larger impact on modded.

### Fixes

**[MC-202036](https://bugs.mojang.com/browse/MC-202036): Biomes IDs may become shuffled when adding or removing biomes**

In vanilla, adding or removing a biome datapack may lead to shuffled biome IDs in existing worlds. This has a much larger impact on modded, as adding or removing biome mods, or even blacklisting certain biomes may cause biomes in existing worlds to shuffle. In early versions of Forge for 1.16, this also could occur if a mod changed the order of their biome registrations (Thankfully, this was fixed as of Forge 34.1.42).

Chocolate fixes this by adding a palette of biomes to each chunk when it is saved. This means chunks saved while this mod is active will never experience biome shuffling as long as the biome exists. If a biome is removed from the world, Chocolate will detect this also and re-generate the biome according to the vanilla world generation at that location (as opposed to assigning it a random biome based on the previous ID, as in vanilla).

**[MC-197616](https://bugs.mojang.com/browse/MC-197616): Data Pack Biomes with the "Single Biome" world preset causes massive log spam and invalid client biomes.**

Related to the above issue, when using the "Single Biome" world type, biomes in world do not exist in the runtime biome registry. Chocolate is able to fix biome serialization over the network in this case, which prevents the aforementioned ill effects when trying to deserialize invalid biomes on a client.
- Fixes [MC-202036](https://bugs.mojang.com/browse/MC-202036) : Biomes are saved to chunks as raw IDs, and can become shuffled when adding new biomes (either via data packs or mods), or the registration order of existing biomes changes. Chocolate fixes this by adding a palette to the chunk save data, and guards biome serialization with this registry key based approach.
  - Note: Forge has *mostly* fixed this for modded biomes by serializing the forge registry. However, this is an incomplete fix, as it does not take into account data pack biomes and so the issue may still occur.

*Chocolate also previously fixed [MC-197860](https://bugs.mojang.com/browse/MC-197860), but that has been incorporated into Forge as of 35.1.32, and as such is no longer also fixed by Chocolate.* 

### Design

In order to fix these features while maintaining *most of* vanilla parity, this mod has been designed with a couple things in mind:

- Obey strong contracts of behavior. For example, enforcing that `BiomeContainer` *must* contain a reference to a `Registry<Biome>` at construction time, as opposed to checking it at serialization time and either assuming, or checking backups for the biome registry. This enforcement makes it much easier to write semantically correct - and verifiable - code, and makes it easier to identify problems when and where they occur. Which leads to the next important point:
- Identify failure states, and fail fast, and with as much information as possible. In places where failures are possible due to external factors (such as unknown chunk data format changes, or adding behavioral contracts to otherwise unrestricted code), Chocolate has been designed to fail fast - throwing exceptions - and with as much debugging information as possible, in order to identify what assumption has been violated, and try to arrive at a fix.
- Test cases! Chocolate has an entire test suite of all its modifications, thanks to [mcjunitlib](https://github.com/alcatrazEscapee/mcjunitlib).

### Possibilities

I am open to fixing other potential issues in this mod. However, due to my own area of expertise, and plan for this mod, requirements for possible future vanilla and/or forge bugfixes are:

- It needs to be deterministically fixable, without breaking vanilla contracts or causing undue pain to other mods which may rely on some vanilla behavior.
- It should be a high visibility or high impact bug that predominantly affects modded or data pack world generation.
- It is something which cannot be avoided or skirted around using the standard set of tools which are available for mods.

### Usage (For Modders)

To add this mod as a dependency in dev, first add jitpack to your `repositories` section. **Do not** add this to your `buildscript` section!

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Secondly, add the version of Chocolate you desire to the `dependencies` section. `VERSION` and `MINECRAFT_VERSION` must be replaced with the relevant versions, or another valid jitpack version identifier (such as a commit hash). Make sure to include `transitive = false` due to [ForgeGradle#584](https://github.com/MinecraftForge/ForgeGradle/issues/584). Latest tagged versions can be checked in the "Releases" tab on github.

```groovy
dependencies {
    testImplementation fg.deobf('com.github.alcatrazEscapee:chocolate:VERSION-MINECRAFT_VERSION') {
        transitive = false
    }
}
```