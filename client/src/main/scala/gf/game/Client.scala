package gf.game

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.Future

class Client(playerName: String)(implicit val actorSystem: ActorSystem, implicit val actorMaterializer: ActorMaterializer) extends DefaultJsonProtocol {
  implicit val positionFormat: RootJsonFormat[Position] = jsonFormat2(Position)
  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat10(Player)

  val webSocketFlow: Flow[Message, List[Player], Future[WebSocketUpgradeResponse]] = Http().webSocketClientFlow(WebSocketRequest(s"ws://localhost:8080/?playerName=$playerName")).collect {
    case TextMessage.Strict(strMsg) => {
      strMsg.parseJson.convertTo[List[Player]]
    }
  }

  def run[M1,M2](input: Source[String, M1], output: Sink[List[Player],M2]): ((M1, Future[WebSocketUpgradeResponse]), M2) = {
    input.map(direction => TextMessage(direction))
        .viaMat(webSocketFlow)(Keep.both)
        .toMat(output)(Keep.both)
        .run()
  }
}
