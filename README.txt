
Todo:


Make Demo level more understandable

Add level music

Add running sound effect

Finish respective levels

Reset enemies(maybe all entities?) if player dies


LEVELS
------------

    Creating a level:

        Make a new Level object in the LevelManager class.
            e.g:
                public Level DEMO;

       Then in loadLevels() you need to initialise it:
            e.g:
                DEMO = new Level(this, 1, "resources/levels/level_demo.txt");


    Loading a level:

        Use the setActiveLevel() function in the Game.class to set the level. By default Level.DEMO is set as the first level.
        Alternatively you can set the 'next_level' in resources/levels/level_demo.txt to the file name of your new level.

        For example, if you made a new level called level_1, set the next_level in level_demo.txt to level_1. Then when you go
        through the door, it will load the level_1 level.


ENTITIES
------------

    Creating an entity:

        Entities are anything that isn't a block e.g. player, plant monster ect.

        To make a new entity you first have to define it in the EntityType class. Then in the brackets set the path to the entity's
        image.
            e.g:
                    PLANT_MONSTER("resources/images/plantAttack.gif");

        Then you want to make a new class for it.
            e.g:
                    public class EnemyPlant extends Enemy {

                        public EnemyPlant(Level level, Location loc) {
                                super(level, EntityType.PLANT_MONSTER, loc);    <----- Here you want to change EntityType.PLANT_MONSTER
                        }                                                              to the entity type you made before.

                    }

        Next, you want to set its letter icon so the levels know what it is. So in the Level.class file you'll want to add this
        code in the load() function during the for loop inside the "if (entityKeyMap.containsKey(key))":
            e.g:
                     if (type == EntityType.PLANT_MONSTER) {
                        entity = new EnemyPlant(this, spawnLoc);                     <----- This you'll want to make it your new entity class
                                                                                            in this case it is EnemyPlant
                     }

        Lastly, you just want to define it's texture in the Game.class under the loadCharacterImages() functions
            e.g:

                Inside the loadCharacterImages() function, add:

                imageBank.put("plant_monster", loadImage(EntityType.PLANT_MONSTER.getFilePath()));      <----- Make sure the name e.g. "plant_monster" matches the
                                                                                                               EntityType name, in this case it is EntityType.PLANT_MONSTER


