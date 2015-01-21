package dungeondagger

class RandomFrogAgent(world: World) extends Agent(AgentKind.Frog){
  override def act: Option[Action] = {
    if(world.rand.nextBoolean()){
      Some(Move(this, world.rand.nextInt(4).toByte))
    } else if(world.rand.nextInt(100) == 0){
      Some(Die)
    } else None
  }
}
