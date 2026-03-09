# Timed Raid Window

## Overview
Timed Raid Window is a two-player local mini-adventure built for the GuildQuest Mini-Adventure Environment.

The goal is to complete all objectives before time runs out.  
Players take turns on the same machine. After both players act, one round passes and the world clock advances.

## File Paths

### Main Timed Raid files
- `src/main/java/gmae/adventures/timedraid/TimedRaidAdventure.java`
- `src/main/java/gmae/adventures/timedraid/TimedRaidState.java`
- `src/main/java/gmae/adventures/timedraid/TimedRaidTest.java`

## What Each File Does

### `TimedRaidAdventure.java`
This is the playable mini-adventure class that implements the `MiniAdventure` interface.

Responsibilities:
- starts and resets the adventure
- accepts player input
- converts text commands into actions
- shows terminal status text
- reports final result
- connects the mini-adventure to GMAE

### `TimedRaidState.java`
This contains the main game rules and state.

Responsibilities:
- tracks player turns
- tracks player positions
- tracks objective progress
- handles movement and work actions
- advances time after both players act
- determines win or lose state

### `TimedRaidTest.java`
This is a simple test/demo runner for Timed Raid logic.

Responsibilities:
- creates a Timed Raid state directly
- simulates a sequence of actions
- prints time, rounds, and progress
- helps verify that the mini-adventure logic works

## Gameplay Rules

- This is a local 2-player mini-adventure.
- Players alternate turns.
- After both players act, one round passes.
- The world clock advances each round.
- Players must move onto an objective tile and then use `work`.
- If all objectives are completed before rounds run out, the players win.
- If rounds reach 0 before all objectives are complete, the players lose.

## Objective Tiles

- **Get Artifact** at `(1,1)`
- **Activate Gate** at `(3,2)`
- **Clear Enemies** at `(4,4)`

Default objective requirements:
- Get Artifact: `2`
- Activate Gate: `1`
- Clear Enemies: `1`

Default adventure settings:
- Rounds: `7`
- Minutes per round: `10`

## Commands

### Movement
- `u` = up
- `d` = down
- `l` = left
- `r` = right

### Actions
- `w` = work
- `p` = pass

Important:
- `work` only helps when the current player is standing on an objective tile.

## How To Run

```bash
rm -rf out
mkdir -p out
javac -d out $(find src/main/java -name "*.java")

```

## Example of a Winning Path

r
r
d
u
w
w
w
r
p
d
p
d
w

