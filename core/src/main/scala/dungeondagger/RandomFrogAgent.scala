package dungeondagger

class RandomFrogAgent extends Agent(AgentNiche.Front){
  override def act: Option[Action] = {
    if(World.rand.nextBoolean()){
      Some(Move(this, World.rand.nextInt(6).toByte))
    } else if(World.rand.nextInt(100) == 0){
      Some(Die)
    } else None
  }
}
