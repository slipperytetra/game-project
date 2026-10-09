Further optimisations:
  Audio Bank: Instead of loading all audio sounds into the bank when the game starts, i want to only load audio that is needed per level. So i guess will need to check what sounds are used before the level is load and only load those into the bank.

  Texture: Exact same for textures as well.

  The only thing to keep is mind is for textures the entire bank will need to be loaded when the level editor is activated as the player will have access to every block so those textures will be needed. Audio shouldn't need this is as when editing the level is in a 'frozen' state so the only sound playing is ui interactions and background music. However will still need to check the level after when exiting so reload any new audio sources (new enemies added ect)
