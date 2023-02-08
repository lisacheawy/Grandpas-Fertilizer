package Server

import akka.actor.ActorRef

trait GameEvent

case class Player(id: String, position: Position, direction:String, inventory: List[String],
                  states: Map[String, Boolean], plantedSpots: Map[String, String], waterState: Map[String, Boolean],
                  fertilizerSpot: String, plantGrowth: Map[String, Map[String, Int]], dayCounter: Int)
case class PlayerJoined(playerName: String,actorRef: ActorRef) extends GameEvent
case class PlayerLeft(playerName: String) extends GameEvent
case class PlayerMoveRequest(playerName: String, direction: String) extends GameEvent
case class PlayersChanged(players: Iterable[Player]) extends GameEvent
case class PlayerWithActor(player: Player, actor: ActorRef)

case class InventoryChangeRequest(item: String) extends GameEvent
case class UpdateState(state: String) extends GameEvent
case class UpdatePlantedSpots(spot: String) extends GameEvent
case class UpdateWaterState(state: String) extends GameEvent
case class SetFertilizerSpot(spot: String) extends GameEvent
case class UpdatePlantGrowth(plant: String) extends GameEvent
case class UpdateDayCounter() extends GameEvent

case class Position(x: Double, y: Double) {
  def +(other: Position): Position = {
    Position(x + other.x, y + other.y)
  }
}

