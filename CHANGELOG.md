# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]

## 1.3.18

### Changes
- Added the mod icon using NeoForge's current mods-screen metadata.
- Updated to NeoForge 26.3.0.3-beta and migrated Soul Cage inventories to the transfer API while preserving existing saved shards.
- Updated PolyLib to 2.0.15 for compatibility with NeoForge's updated events.
- Ported Soul Shards to Minecraft 26.3 and NeoForge 26.3.
- Updated PolyLib, JEI, Jade, and the NeoForge build plugin for Minecraft 26.3.
- Migrated recipe and loot generation to the reloadable registry system.
- Updated inventory returns and Soul Cage redstone connections for the new Minecraft APIs.

## 1.3.17

### Changes
- Ported Soul Shards to Minecraft 26.2 and NeoForge 26.2.
- Restored Soul Cage mob previews using Minecraft 26.2's render-state API.
- Fixed Soul Cage preview rendering and client synchronization.
- Fixed owner-online spawning and entity denylist checks.
- Added independent controls for vanilla spawner absorption and soul gathering from vanilla-spawned mobs.
