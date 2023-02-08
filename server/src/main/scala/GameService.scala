package Server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import scala.collection.mutable.ListBuffer

class GameService(implicit val actorSystem : ActorSystem, implicit  val actorMaterializer: ActorMaterializer) extends Directives {

  val websocketRoute: Route = (get & parameter("playerName")){ playerName =>
    handleWebSocketMessages(flow(playerName))
  }

  val gameAreaActor: ActorRef = actorSystem.actorOf(Props(new GameAreaActor()))
  val playerActorSource: Source[GameEvent, ActorRef] = Source.actorRef[GameEvent](5,OverflowStrategy.fail)
  def flow(playerName: String): Flow[Message, Message, Any] = Flow.fromGraph(GraphDSL.create(playerActorSource){ implicit builder => playerActor =>
    import GraphDSL.Implicits._

    val materialization = builder.materializedValue.map(playerActorRef => PlayerJoined(playerName,playerActorRef))
    val merge = builder.add(Merge[GameEvent](2))

    val messagesToGameEventsFlow = builder.add(Flow[Message].collect {
      case TextMessage.Strict(value) => {
        (value.split("_")(0)) match {
          case "inventory" => InventoryChangeRequest(value)
          case "state" => UpdateState(value)
          case "spot" => UpdatePlantedSpots(value)
          case "water" => UpdateWaterState(value)
          case "fertilizer" => SetFertilizerSpot(value)
          case "grow" => UpdatePlantGrowth(value)
          case "day" => UpdateDayCounter()
          case _ => PlayerMoveRequest(playerName,value)
        }
      }
    })

    val gameEventsToMessagesFlow = builder.add(Flow[GameEvent].map {
      case PlayersChanged(players) => {
        import spray.json._
        import DefaultJsonProtocol._
        implicit val positionFormat: RootJsonFormat[Position] = jsonFormat2(Position)
        implicit val playerFormat: RootJsonFormat[Player] = jsonFormat10(Player)
        TextMessage(players.toList.toJson.toString)
      }
    })

    val gameAreaActorSink = Sink.actorRef[GameEvent](gameAreaActor,PlayerLeft(playerName))

    materialization ~> merge ~> gameAreaActorSink
    messagesToGameEventsFlow ~> merge

    playerActor ~> gameEventsToMessagesFlow

    FlowShape(messagesToGameEventsFlow.in,gameEventsToMessagesFlow.out)
  })
}