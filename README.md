# BattleArena

A complete match and event framework for Minecraft. Supports creating modes through config files, or fully custom modes through plugins.

## Default Modes
Active games in BattleArena are referred to as Competitions. BattleArena natively supports two competition types:
- Match: A game that is started when a certain condition is met (i.e. number of players), or is always active. These games can be joined at any time, as long as there are available maps.
- Event: A game that is started based on a certain interval, or when triggered by a server action. These games cannot be joined normally unless the event is active.

## Builtin Match Types
- Arena: Simple duels mode that you fight with what is given to you in the config.
- Skirmish: You bring in items you want to fight with. The game is always running, and you can join and leave at any time.
- Colosseum: 4v4 team deathmatch. Last team standing wins.
- Battlegrounds: 1 minute match in which the winner is the player with the most kills.

## Builtin Event Types
- Free for All: A free for all deathmatch that starts every 30 minutes. Last player alive wins.
- Deathmatch: A 2-minute event where if you die you respawn. The player with the highest number of kills wins.
- Tournament: Bracket tournament for any number of teams.

## For Developers
BattleArena is designed to be easily extendable. You can create your own modes, events, and even competitions. You can also create your own commands and listeners to handle events in your own way.

### Creating an Arena
In BattleArena, the root logic for a game is in the `Arena` class. This class is responsible for handling the game logic, and is the main class that is extended when creating a new mode. Most all aspects of BattleArena are event-driven, meaning that rather than implementing or overriding methods, you will be listening for various game events, or adding your own. Here is a simple example of an Arena class:
```java
public class MyArena extends Arena {
    
    @ArenaEventHandler
    public void onArenaJoin(ArenaJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to my arena!");
    }
}
```

And for registering it:
```java
BattleArena.getInstance().registerArena("MyArena", MyArena.class, MyArena::new);
```

### Arena Events
Arena events are at the core of BattleArena. They are fired when certain actions happen in the game, and can be used to listen for and handle these actions. BattleArena opts to use the `@ArenaEventHandler` annotation compared to Bukkit's `@EventHandler` annotation, as it allows for capturing events specifically in an Arena, rather than globally. 

A list of all Arena events can be found in the `org.battleplugins.arena.event` package.

Here is an example of an Arena event handler:
```java
@ArenaEventHandler
public void onInteract(PlayerInteractEvent event) {
    event.getPlayer().sendMessage("Interact while in Arena!");
}
```

Arena event listeners must implement the `ArenaListener` class rather than Bukkit's `Listener` class and rather than registered through Bukkit's `PluginManager`, they are registered through the `ArenaEventManager`. Here is an example of registering an Arena event listener:
```java
Arena arena = ...; // your Arena instance
arena.getEventManager().registerEvents(new MyArenaListener());
```

It is important to note that the `@ArenaEventHandler` annotation will not work for every event. They can only listen for events that can capture a player (i.e. PlayerInteractEvent, PlayerMoveEvent, etc.) as BattleArena needs to know which Arena to fire the event in. Any event that implements `PlayerEvent` or `EntityEvent` will automatically be captured by this. If you wish to implement your own resolver to capture an event to an Arena, you can use the `ArenaEventManager#registerArenaResolver` method.

### Creating Custom Event Triggers
BattleArena has multiple event triggers implemented by default used in the config. These include `on-join`, `on-complete`, and many others used throughout the plugin. However, you can create your own event triggers to use in the config. 

In order to add your own, ensure your `Event` class implements the `ArenaEvent` or `ArenaPlayerEvent` class. The difference between the two is that `ArenaPlayerEvent` will only capture a single player, which is the player in the event (see `on-join` as an example), while `ArenaEvent` will be fired for all players in a competition (see `on-complete` as an example).

Once you have added the `@EventTrigger` annotation, then run `ArenaEventType.create(<name>, <event class>)` which will create the event type and allow it to be used in the config. Then, in order to trigger this, call your event through the `ArenaEventManager` visible in your `Arena` class.