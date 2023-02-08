package gf.game

case class Player(id:String, position:Position, direction:String, inventory: List[String],
                  states: Map[String,Boolean], plantedSpots: Map[String, String], waterState: Map[String, Boolean],
                  fertilizerSpot: String, plantGrowth: Map[String, Map[String, Int]], dayCounter: Int)

case class Position(x:Double, y:Double)


