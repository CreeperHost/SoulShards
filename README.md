# Soul Shards

<a href="https://www.curseforge.com/minecraft/mc-mods/creeperhost-presents-soul-shards"><img src="http://cf.way2muchnoise.eu/full_576589_downloads.svg" /></a>


Ever wanted to create your own mob spawners? Now you can!

## Links

* [Maven](https://maven.creeperhost.net/release)

## Information

This is a fan continuation of the popular 1.4.7 mod, [Soul Shards](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1285901-1-6-4-forgeirc-v1-0-18-soul-shards-v2-0-15-and#soulshards).

This version of the mod is based on the sources of [Soul Shards: Reborn by Moze_Intel](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1445947-1-7-10-soul-shards-reborn-original-soul-shards) and [Soul Shards: The Old Ways by Team Whammich](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2329877-soul-shards-the-old-ways-rc9-update).

This version is a near direct clone of the original mod.

## Developing Addons

Add to your `build.gradle`:

    repositories {
      maven {
        url "https://maven.creeperhost.net/release"
      }
    }
    
    dependencies {
      implementation "net.creeperhost.soulshards:soulshards-neoforge:${ssr_version}"
    }
`${ssr_version}` is the full Maven version, including the Minecraft version, for example `26.3-1.3.18`.

## License

Soul Shards Respawn is licensed under the [MIT](https://tldrlegal.com/license/mit-license) license.
