# API

SpawnerSilk exposes a small API through the `SpawnerAPI` class.

## Create a Spawner Item from an Entity Type

```java
public static ItemStack getSpawner(EntityType entity)
```

Returns a spawner item for the given entity type.

If the entity type is invalid or unsupported, the method returns a fallback spawner item with a placeholder display name.

Example:

```java
SpawnerAPI.getSpawner(EntityType.SKELETON_HORSE);
```

## Read the Entity Type from a Spawner Item

```java
public static EntityType getEntityType(ItemStack is)
```

Returns the `EntityType` represented by the given spawner item.

If the item is invalid or unsupported, the method returns `EntityType.UNKNOWN`.

Example:

```java
SpawnerAPI.getEntityType(myItemStackSpawner);
```

## Create a Spawner Item from an Entity Name

```java
public static ItemStack stringToCustomItemStack(String mobName, int amount)
```

Returns a spawner item for the given mob name and amount.

Example:

```java
SpawnerAPI.stringToCustomItemStack("cat", 1);
```

## Notes

- Add the SpawnerSilk jar to your plugin project before calling the API.
- The exact item output depends on the entity type support available in SpawnerSilk.
