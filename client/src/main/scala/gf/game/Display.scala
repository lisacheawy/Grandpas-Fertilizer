package gf.game

import akka.Done
import akka.stream.scaladsl.Sink
import scalafx.application.Platform
import scalafx.scene.layout.AnchorPane
import model.{CharactersModel, SharedData}
import scalafx.scene.image.{Image, ImageView}
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class Display(sharedData: SharedData, name:String) {
  val character1: AnchorPane = new AnchorPane
  val character2: AnchorPane = new AnchorPane
  var playerStates: Map[String, Any] = Map()
  val model = new CharactersModel()

  def setPosition(player: Player, images: List[Image]): ImageView = {
    if(player.direction.split("_")(1).toInt < 15) {
      returnView(player, images.head)
    }
    else if(player.direction.split("_")(1).toInt > 30){
      sharedData.steps = 0
      returnView(player, images.head)
    }
    else {
      returnView(player, images.last)
    }
  }

  def returnView(player: Player, image: Image): ImageView = {
    new ImageView(image) {
      x = player.position.x
      y = player.position.y
    }
  }

  def sink: Sink[List[Player], Future[Done]] = Sink.foreach[List[Player]] { playerPositions =>
    val playerList: ListBuffer[String] = ListBuffer()
    playerPositions.foreach(i => playerList += i.id)
    val playersShapes = playerPositions.map(player => {
      val pNum = player.id.split("_")
      playerStates = Map("playerList" -> playerList, "inventory" -> player.inventory, "states" -> player.states,
        "plantedSpots" -> player.plantedSpots, "waterState" -> player.waterState,
        "fertilizerSpot" -> player.fertilizerSpot, "plantGrowth" -> player.plantGrowth, "dayCounter" -> player.dayCounter)
      if(pNum.last.toInt % 2 != 0) {  //female character
        player.direction.split("_")(0) match {
          case "idle" => returnView(player, model.fFront)
          case "right" => setPosition(player, List(model.fSideRight, model.fSideRightWalk))
          case "left" => setPosition(player, List(model.fSideLeft, model.fSideLeftWalk))
          case "up" => setPosition(player, List(model.fBackWalk1, model.fBackWalk2))
          case "down" => {
            if (player.direction.split("_")(1) == "front") {
              returnView(player, model.fFront)
            }
            else {
              setPosition(player, List(model.fFrontWalk1, model.fFrontWalk2))
            }
          }
        }
      }
      else {  //male character
        player.direction.split("_")(0) match {
          case "idle" => returnView(player, model.mFront)
          case "right" => setPosition(player, List(model.mSideRight, model.mSideRightWalk))
          case "left" => setPosition(player, List(model.mSideLeft, model.mSideLeftWalk))
          case "up" => setPosition(player, List(model.mBackWalk1, model.mBackWalk2))
          case "down" => {
            if (player.direction.split("_")(1) == "front") {
              returnView(player, model.mFront)
            }
            else {
              setPosition(player, List(model.mFrontWalk1, model.mFrontWalk2))
            }
          }
        }
      }
    }
    )

    Platform.runLater({
      character1.children = playersShapes.head
      character1.setLayoutX(playersShapes.head.x.value)
      character1.setLayoutY(playersShapes.head.y.value)
      playersShapes.head.setX(0)
      playersShapes.head.setY(0)
      if (playersShapes.length > 1) {
        character2.children = playersShapes.last
        character2.setLayoutX(playersShapes.last.x.value)
        character2.setLayoutY(playersShapes.last.y.value)
        playersShapes.last.setX(0)
        playersShapes.last.setY(0)
      }
      else {
        character2.children.clear()
      }
      character1.requestLayout()
      character2.requestLayout()
      playerStates
    })

  }


}
