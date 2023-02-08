package gf.game.view

import gf.game.controller.RoomFullController
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scalafx.Includes._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

class RoomFull {
  def roomFull(stage: PrimaryStage): Unit = {
    val controller = new RoomFullController()
    val blackScreen: Rectangle = new Rectangle() {
      width = 1344
      height = 960
      fill = Color.Black
    }
    val scene: Scene = new Scene(1344, 960) {
      content = new ListBuffer()
      content ++= Seq(blackScreen,controller.pModel.roomFullPromptView, controller.pModel.okButtonView)
      controller.pModel.okButtonView.onMousePressed = _ => {
        new MainMenu().mainMenu(stage)
      }
    }
    stage.setScene(scene)
  }
}
