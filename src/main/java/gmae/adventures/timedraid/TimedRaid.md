# Timed Raid Window

## Overview
Timed Raid Window is a local 2-player mini-adventure where players move around a small grid, reach objective tiles, and use `work` to complete raid tasks before time runs out.

The base version of Timed Raid is turn-based. Players alternate turns, and after both players act, one round passes and the world clock advances.

## File Paths

### Main Timed Raid files
- `src/main/java/gmae/adventures/timedraid/TimedRaidAdventure.java`
- `src/main/java/gmae/adventures/timedraid/TimedRaidState.java`
- `src/main/java/gmae/adventures/timedraid/TimedRaidTest.java`

### Real-time support files
- `src/main/java/gmae/adventures/timedraid/TimedRaidMode.java`
- `src/main/java/gmae/adventures/timedraid/TimedRaidModeRunner.java`
- `src/main/java/gmae/adventures/timedraid/TimedRaidRealTimeManualTest.java`

## What Each File Does

### `TimedRaidAdventure.java`
This is the playable mini-adventure class that implements the `MiniAdventure` interface.

Responsibilities:
- starts and resets the adventure
- accepts player input
- converts text commands into actions
- shows terminal status text
- reports final result
- supports both turn-based and real-time Timed Raid modes
- connects the mini-adventure to GMAE

### `TimedRaidState.java`
This contains the main game rules and state.

Responsibilities:
- tracks player turns
- tracks player positions
- tracks objective progress
- handles movement and work actions
- advances time after both players act in turn-based mode
- supports direct action handling for real-time mode
- determines win or lose state

### `TimedRaidTest.java`
This is a simple test/demo runner for Timed Raid logic.

Responsibilities:
- creates a Timed Raid state directly
- simulates a sequence of actions
- prints time, rounds, and progress
- helps verify that the mini-adventure logic works

### `TimedRaidMode.java`
This defines the Timed Raid play mode.

Responsibilities:
- distinguishes between `TURN_BASED` and `REAL_TIME`

### `TimedRaidModeRunner.java`
This is a demo runner that shows both Timed Raid modes.

Responsibilities:
- runs a scripted turn-based example
- runs a scripted real-time example
- helps verify that both modes work

### `TimedRaidRealTimeManualTest.java`
This is a manual real-time terminal runner.

Responsibilities:
- launches Timed Raid in real-time mode
- accepts live player input from the terminal
- demonstrates the real-time countdown behavior
- demonstrates automatic timeout loss behavior

## Gameplay Rules

### Turn-Based Mode
- This is a local 2-player mini-adventure.
- Players alternate turns.
- After both players act, one round passes.
- The world clock advances each round.
- Players must move onto an objective tile and then use `work`.
- If all objectives are completed before rounds run out, the players win.
- If rounds reach 0 before all objectives are complete, the players lose.

### Objective Tiles
- **Get Artifact** at `(1,1)`
- **Activate Gate** at `(3,2)`
- **Clear Enemies** at `(4,4)`

### Default objective requirements
- Get Artifact: `2`
- Activate Gate: `2`
- Clear Enemies: `2`

### Default adventure settings used in manual/demo runs
- Rounds: `5`
- Minutes per round: `10`

### Default constructor settings
- Rounds: `7`
- Minutes per round: `10`
- Required objectives: `{2, 1, 1}`

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

## Extra Credit: Real-Time Mode
Timed Raid also supports an optional real-time mode for extra credit.

In this mode:
- the same grid, player positions, objective tiles, and commands are preserved
- players do not wait for alternating rounds
- actions are processed immediately as players continue entering commands
- a live wall-clock countdown is used instead of round-by-round time advancement
- the game ends in a win if all objectives are completed before the countdown expires
- the game ends in a loss if time expires before all objectives are completed

The real-time version also reports why the game ended, such as:
- all objectives were completed before time ran out
- time ran out before all objectives were completed

For terminal testing, the real-time manual runner also includes timeout checking so the game can automatically end when the countdown expires.

## How To Run

```bash
rm -rf out
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
```

## Run the basic Timed Raid test
```bash
java -cp out gmae.adventures.timedraid.TimedRaidTest
```

## Run the mode demo
```bash
java -cp out gmae.adventures.timedraid.TimedRaidModeRunner
```

## Run the manual real-time test
```bash
java -cp out gmae.adventures.timedraid.TimedRaidRealTimeManualTest
```

## Example a Winning Path

Player 1
	•	r
	•	d
	•	w
	•	w

Player 2
	•	r
	•	u
	•	w
	•	w
	•	r
	•	d
	•	d
	•	w
	•	w