package Server

import akka.actor.Actor
import scala.collection.mutable

class GameAreaActor extends Actor {
  val players: mutable.Map[String, PlayerWithActor] = collection.mutable.LinkedHashMap[String, PlayerWithActor]()
  def takenPositions: Seq[Position] = players.values.map(_.player.position).toList

  override def receive: Receive = {
    case PlayerJoined(playerName,actor) => {
      if (players.size == 1){  //if there is a player inside, copy their shared items and states
        // determines player rendering e.g player 1 or 2 based on odd or even number
        // allows same name
        // no need to decrement - maintains the female / male character if same player joins and rejoins
        val prevPlayer = players.last._2.player
        val prevPlayerId = prevPlayer.id.split("_").last
        val newId = s"${playerName}_${prevPlayerId.toInt + 1}"
        players += (newId -> PlayerWithActor(Player(newId, Position(40,337),"idle", prevPlayer.inventory,
          prevPlayer.states, prevPlayer.plantedSpots, prevPlayer.waterState, prevPlayer.fertilizerSpot, prevPlayer.plantGrowth, prevPlayer.dayCounter), actor))
        notifyPlayersChanged()
      }
      else if (players.isEmpty){
        val playerId = s"${playerName}_1"
        val starterDays: Map[String, Int] = Map("daysRequired" -> 0, "daysLeft" -> 0)
        val starterGrowth: Map[String, Map[String, Int]] = Map("soilOverlayCarrot" -> starterDays, "soilOverlayCabbage" -> starterDays, "soilOverlaySunflower" -> starterDays)
        val starterStates: Map[String, Boolean] = Map("isFilling" -> false, "isFull" -> false, "isPlanting" -> false, "isWatering" -> false, "fertilizerUsed" -> false, "nextDay" -> false)
        val starterPlayer = Player(playerId, Position(40, 337), "idle", List("carrotSeedPack", "cabbageSeedPack", "sunflowerSeedPack"), starterStates,
          Map(), Map(), "", starterGrowth, 0)
        players += (playerId -> PlayerWithActor(starterPlayer, actor))
        notifyPlayersChanged()
      }
    }
    case PlayerLeft(id) => {
      players.keys.foreach(k => if (k.contains(id)) players -= k)
      notifyPlayersChanged()
    }
    case PlayerMoveRequest(playerName, direction) => {
      for ((k, v) <- players){
        if (k.contains(playerName)) {
          val playerValue = v.player
          val xPos = playerValue.position.x.toInt
          val yPos = playerValue.position.y.toInt
          var oldPos = playerValue.position

          // setting fence restriction
          var offset = setOffset(direction.split("_")(0), -3, 3, 3, -3)
          if ((xPos >= 6 && xPos <= 652) && (yPos >= 166 && yPos <= 760)) { //default
            offset = setOffset(direction.split("_")(0), -3, 3, 3, -3)
          }
          else if (xPos < 6 && (yPos >= 166 && yPos <= 760)) { //left horizontal border
            oldPos = Position(6, playerValue.position.y)
            offset = setOffset(direction.split("_")(0), -3, 3, 3, 0)
          }
          else if (xPos > 652 && (yPos >= 166 && yPos <= 760)) { //right horizontal border
            oldPos = Position(652, playerValue.position.y)
            offset = setOffset(direction.split("_")(0), -3, 3, 0, -3)
          }
          else if (yPos < 166 && (xPos >= 6 && xPos <= 652)) { //top vertical border
            oldPos = Position(playerValue.position.x, 166)
            offset = setOffset(direction.split("_")(0), 0, 3, 3, -3)
          }
          else if (yPos > 760 && (xPos >= 6 && xPos <= 652)) { //bottom vertical border
            oldPos = Position(playerValue.position.x, 760)
            offset = setOffset(direction.split("_")(0), -3, 0, 3, -3)
          }
          val newPosition = oldPos + offset
          if (!takenPositions.contains(newPosition)) {
            players(k) = PlayerWithActor(Player(k, newPosition, direction, playerValue.inventory,
              playerValue.states, playerValue.plantedSpots, playerValue.waterState, playerValue.fertilizerSpot, playerValue.plantGrowth, playerValue.dayCounter), v.actor)
            notifyPlayersChanged()
          }
        }
      }

    }
    case InventoryChangeRequest(value) => {
      val oldInventory = players.last._2.player.inventory
      var newInventory = oldInventory
      val item = value.split("_").filter(_ != "inventory")
      item(0) match {
        case "add" => newInventory = oldInventory :+ item(1)
        case "remove" => newInventory = oldInventory.filter(_ != item(1))
      }
      for((k,v) <- players){
        val playerValue = players(v.player.id).player
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction, newInventory,
          playerValue.states, playerValue.plantedSpots, playerValue.waterState, playerValue.fertilizerSpot, playerValue.plantGrowth, playerValue.dayCounter), players(k).actor)
      }
      notifyPlayersChanged()
    }

    case UpdateState(value) => {
      val state = value.split("_").filter(_ != "state")
      for ((k, v) <- players) {
        val playerValue = players(v.player.id).player
        val newStateList = playerValue.states + (state(0) -> state(1).toBoolean)
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction,playerValue.inventory,
          newStateList, playerValue.plantedSpots, playerValue.waterState, playerValue.fertilizerSpot, playerValue.plantGrowth, playerValue.dayCounter), players(k).actor)
      }
      notifyPlayersChanged()
    }

    case UpdatePlantedSpots(value) => {
      val spot = value.split("_").filter(_ != "spot")
      for ((k, v) <- players) {
        val playerValue = players(v.player.id).player
        val newSpotList = playerValue.plantedSpots + (spot(0) -> spot(1))
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction, playerValue.inventory,
          playerValue.states, newSpotList, playerValue.waterState, playerValue.fertilizerSpot, playerValue.plantGrowth, playerValue.dayCounter), players(k).actor)
      }
      notifyPlayersChanged()
    }

    case UpdateWaterState(value) => {
      val water = value.split("_").filter(_ != "water")
      for ((k, v) <- players) {
        val playerValue = players(v.player.id).player
        val newWaterState = playerValue.waterState + (water(0) -> water(1).toBoolean)
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction, playerValue.inventory,
          playerValue.states, playerValue.plantedSpots, newWaterState, playerValue.fertilizerSpot, playerValue.plantGrowth, playerValue.dayCounter), players(k).actor)
      }
      notifyPlayersChanged()
    }

    case SetFertilizerSpot(value) => {
      val spot = value.split("_")(1)
      for ((k, v) <- players) {
        val playerValue = players(v.player.id).player
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction, playerValue.inventory,
          playerValue.states, playerValue.plantedSpots, playerValue.waterState, spot, playerValue.plantGrowth, playerValue.dayCounter), players(k).actor)
      }
      notifyPlayersChanged()
    }

    case UpdatePlantGrowth(value) => {
      val plant = value.split("_").filter(_ != "grow")
      for ((k, v) <- players) {
        val playerValue = players(v.player.id).player
        val oldDaysReq = playerValue.plantGrowth(plant(0))("daysRequired")
        val oldDaysLeft = playerValue.plantGrowth(plant(0))("daysLeft")
        val newGrowth: Map[String, Int] = Map("daysRequired" -> plant(1).toInt, "daysLeft" -> plant(2).toInt)
        val newPlantGrowth = playerValue.plantGrowth + (plant(0) -> newGrowth)
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction, playerValue.inventory,
          playerValue.states, playerValue.plantedSpots, playerValue.waterState, playerValue.fertilizerSpot, newPlantGrowth, playerValue.dayCounter), players(k).actor)
      }
      notifyPlayersChanged()
    }

    case UpdateDayCounter() => {
      for ((k, v) <- players) {
        val playerValue = players(v.player.id).player
        val newCounter = playerValue.dayCounter + 1
        players(k) = PlayerWithActor(Player(k, playerValue.position, playerValue.direction, playerValue.inventory,
          playerValue.states, playerValue.plantedSpots, playerValue.waterState, playerValue.fertilizerSpot, playerValue.plantGrowth, newCounter), players(k).actor)
      }
    }
  }

  def notifyPlayersChanged(): Unit = {
    players.values.foreach(_.actor ! PlayersChanged(players.values.map(_.player)))
  }

  def setOffset(direction: String, u: Int, d: Int, r: Int, l: Int): Position = {
    direction match {
      case "up" => Position(0, u)
      case "down" => Position(0, d)
      case "right" => Position(r, 0)
      case "left" => Position(l, 0)
    }
  }
}
